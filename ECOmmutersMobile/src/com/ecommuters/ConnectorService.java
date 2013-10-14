package com.ecommuters;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * @author perasso
 * 
 */
public class ConnectorService extends Service implements LocationListener {

	private static final int TIMEOUT = 300000;

	private static final int distanceMeters = 25;

	private static final int MAX_OUT_OF_TRACK_COUNT = 20;
	private static final String CONNECTOR_SERVICE = "ConnectorService";
	private LocationManager mlocManager;
	private boolean isSynchingData;
	private Thread mWorkerThread;
	private Handler mHandler;
	private boolean liveTracking;
	List<Route> mFollowedRoutes = new ArrayList<Route>();

	private Route[] mRoutes;

	private int mTrackingCount;

	// procedura di sincronizzazione itinerari dal e verso il server
	private long syncRoutesInterval = 300000;// 5 minuti
	private Runnable syncRoutesProcedureRunnable;

	// procedura di invio della posizione corrente
	private long sendLatestPositionInterval = 30000;// 30 secondi
	private Runnable sendLatestPositionProcedureRunnable;

	private GPSManager mGPSManager = new GPSManager();
	private ECommuterPosition mLocation;
	private NotificationManager mNotificationManager;
	private Timer mTimer = new Timer(true);
	private TimerTask timerTask = null;
	private int outOfTrackCount;
	private Task startupTask;

	public ConnectorService() {
	}

	public void onFollowingRouteChanged(boolean following, String routeName) {
		if (following)
			setNotification(getString(R.string.following_route, routeName),
					false);
		else
			setNotification(getString(R.string.not_following_route, routeName),
					false);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		startupTask = (Task) intent.getExtras().getSerializable(Task.TASK);
		mWorkerThread.start();
		super.onStart(intent, startId);
	}

	public void onCreate() {
		mWorkerThread = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();

				mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				mHandler = new Handler();
				mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

				syncRoutesProcedure();
				sendLatestPositionProcedure();
				LiveTrackingEventHandler onLiveTrackingChanged = new LiveTrackingEventHandler() {

					@Override
					public void onEvent(Object sender,
							LiveTrackingEventArgs args) {
						if (args.isActive()) {
							activateGPS(GPSManager.MAX_GPS_LEVELS);
						} else {
							stopGPS(GPSManager.MAX_GPS_LEVELS);
						}
					}
				};

				MyApplication.getInstance().ManualLiveTrackingChanged
						.addHandler(onLiveTrackingChanged);
				if (startupTask != null)
					OnExecuteTask(startupTask);
				Looper.loop();

				mlocManager.removeUpdates(ConnectorService.this);
				mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);
				MyApplication.getInstance().ManualLiveTrackingChanged
						.removeHandler(onLiveTrackingChanged);

				Log.d(Const.ECOMMUTERS_TAG, "Worker thread ended");

			}
		});
		syncRoutesProcedureRunnable = new Runnable() {
			public void run() {
				syncRoutesProcedure();
			}
		};

		sendLatestPositionProcedureRunnable = new Runnable() {
			public void run() {
				sendLatestPositionProcedure();
			}
		};

		mWorkerThread.setDaemon(true);

		MyApplication.getInstance().setConnectorService(this);
		super.onCreate();
	}

	public void locationChanged(ECommuterPosition location) {
		boolean b = calculateRoutesByPosition(location);
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		if (b) {
			timerTask = new TimerTask() {

				@Override
				public void run() {
					setLiveTracking(false);
				}
			};
			mTimer.schedule(timerTask, TIMEOUT);
		}
		if (b == liveTracking)
			return;
		// se non sono più sulla traccia, non mi metto subito fuori dal live
		// tracking, aspetto un po',
		// magari ci rientro... dopo MAX_OUT_OF_TRACK_COUNT che entro qui
		// dentro, allora mi considero fuori traccia
		if (!b) {
			outOfTrackCount++;
			if (outOfTrackCount < MAX_OUT_OF_TRACK_COUNT)
				return;
		}

		setLiveTracking(b);
	}

	private void setLiveTracking(boolean b) {
		outOfTrackCount = 0;
		liveTracking = b;

		if (liveTracking) {
			activateGPS(GPSManager.MAX_GPS_LEVELS);
		} else {
			stopGPS(GPSManager.MAX_GPS_LEVELS);
		}
	}

	public boolean isLiveTracking() {
		return liveTracking;
	}

	private boolean calculateRoutesByPosition(ECommuterPosition position) {
		boolean atLeastOneRoute = false;
		float error = distanceMeters;
		for (Route r : mRoutes) {

			boolean followed = false;
			for (int i = r.latestIndex; i < r.getPoints().size(); i++) {
				RoutePoint pt = r.getPoints().get(i);
				double distance = position.distance(pt);
				if (distance < error) {
					atLeastOneRoute = true;
					r.latestIndex = i;
					followed = true;
					break;
				}
			}
			if (followed) {
				if (!mFollowedRoutes.contains(r)) {
					mFollowedRoutes.add(r);
					onFollowingRouteChanged(true, r.getName());
				}
			} else {
				if (mFollowedRoutes.contains(r)) {
					mFollowedRoutes.remove(r);
					onFollowingRouteChanged(false, r.getName());
				}
			}

		}
		return atLeastOneRoute;
	}

	private void activateGPS(final int level) {
		mHandler.post(new Runnable() {
			public void run() {
				boolean wasListening = mGPSManager.requestingLocation();
				// se cambio di livello, cambio anche il listener 8cambia la
				// sensibilit'
				// di tracciatura(
				if (mGPSManager.startGPS(level)) {
					// se entro qui dentro è perché il livello è cambiato
					if (!wasListening) {
						// solo quando inizio a tracciare mando il messaggio di
						// tracking
						// attivato
						String text = getString(R.string.live_tracking_on)
								+ " "
								+ getString(R.string.live_tracking_frequency,
										mGPSManager.getMinTimeSeconds(),
										mGPSManager.getMinDinstanceMeters());
						setNotification(text, true);
					} else {
						// per prima cosa elimino il listener corrente
						mlocManager.removeUpdates(ConnectorService.this);
						String text = getString(
								R.string.live_tracking_frequency,
								mGPSManager.getMinTimeSeconds(),
								mGPSManager.getMinDinstanceMeters());
						setNotification(text, false);
					}
					mlocManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							mGPSManager.getMinTimeSeconds() * 1000,
							mGPSManager.getMinDinstanceMeters(),
							ConnectorService.this);

				}

			}
		});

	}

	private void stopGPS(final int level) {
		mHandler.post(new Runnable() {

			public void run() {
				// se sono arrivato a livello zero, fermo il gps, altrimenti
				// aggiusto la
				// frequenza di aggiornamento
				if (mGPSManager.stopGPS(level)) {
					// se entro qui dentro è perché il livello è cambiato
					// per prima cosa elimino il listener corrente
					mlocManager.removeUpdates(ConnectorService.this);
					if (mGPSManager.requestingLocation()) {

						String text = getString(
								R.string.live_tracking_frequency,
								mGPSManager.getMinTimeSeconds(),
								mGPSManager.getMinDinstanceMeters());
						setNotification(text, false);

						// poi, se devo continuare a registrare su un livello
						// diverso,
						// mi ri registro
						mlocManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								mGPSManager.getMinTimeSeconds() * 1000,
								mGPSManager.getMinDinstanceMeters(),
								ConnectorService.this);
					} else {
						mNotificationManager
								.cancel(Const.TRACKING_NOTIFICATION_ID);
					}
				}

			}
		});

	}

	/**
	 * Spegne il GPS, a meno che non siamo in livetracking manuale
	 */
	public void resetGPS() {
		mHandler.post(new Runnable() {

			public void run() {
				if (mGPSManager.resetLevels()
						&& !mGPSManager.requestingLocation()) {
					mlocManager.removeUpdates(ConnectorService.this);
					mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);
				}

			}
		});

	}

	public void onLocationChanged(Location location) {
		Credentials currentCredentials = MySettings.CurrentCredentials;
		mLocation = new ECommuterPosition(currentCredentials == null ? 0
				: currentCredentials.getUserId(),
				(int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6), location.getAccuracy(),
				(long) (System.currentTimeMillis() / 1E3));
		if (!mGPSManager.requestingLocation())
			return;
		locationChanged(mLocation);
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	private void sendLatestPositionProcedure() {
		sendLatestPosition();
		mHandler.postDelayed(sendLatestPositionProcedureRunnable,
				sendLatestPositionInterval);

	}

	private void setNotification(String message, boolean noisy) {
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, new Intent(), // add this
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.livetracking);

		Notification notification = mBuilder.build();
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				message, contentIntent);
		if (noisy)
			notification.defaults |= Notification.DEFAULT_ALL;
		mNotificationManager.notify(Const.TRACKING_NOTIFICATION_ID,
				notification);
	}

	private void sendLatestPosition() {
		if (mLocation == null || !liveTracking()
				|| !Helper.isOnline(ConnectorService.this))
			return;
		String text = getString(R.string.tracking_position);
		setNotification(text, false);

		try {
			if (RequestBuilder.sendPositionData(mLocation))
				mLocation = null;
		} catch (Exception e) {
			Log.e(CONNECTOR_SERVICE, e.toString());
		}
	}

	@Override
	public void onDestroy() {
		MyApplication.getInstance().setConnectorService(null);
		mHandler.post(new Runnable() {

			public void run() {
				Looper.myLooper().quit();

			}
		});
		super.onDestroy();
	}

	private void syncRoutesProcedure() {
		try {
			if (Helper.isOnline(this) && !isSynchingData)
				syncRoutes();
		} finally {
			mHandler.postDelayed(syncRoutesProcedureRunnable,
					syncRoutesInterval);
		}
	}

	private Boolean liveTracking() {
		return isLiveTracking() || MyApplication.getInstance().isLiveTracking();
	}

	private void syncRoutes() {
		if (isSynchingData)
			return;
		isSynchingData = true;
		final long latestUpdate = MySettings
				.getLatestSyncDate(ConnectorService.this);

		final List<Route> newRoutes = getNewRoutes(latestUpdate);
		if (newRoutes.size() == 0) {
			isSynchingData = false;
			return;
		}
		Credentials.testCredentials(this, new OnAsyncResponse() {
			public void response(boolean success, String message) {

				try {
					if (!success)
						return;

					for (Route r : newRoutes) {
						if (!RequestBuilder.sendRouteData(r))
							throw new Exception("Cannot send route to server: "
									+ r.getName());
					}

					MySettings.setLatestSyncDate(ConnectorService.this,
							(long) (System.currentTimeMillis() / 1e3));
				} catch (Exception e) {
					Log.e(CONNECTOR_SERVICE, e.toString());

				} finally {
					isSynchingData = false;
				}

			}
		});

	}

	private List<Route> getNewRoutes(long latestUpdate) {
		List<Route> newRoutes = new ArrayList<Route>();
		for (Route r : MyApplication.getInstance().getRoutes()) {
			if (r.getLatestUpdate() > latestUpdate)
				newRoutes.add(r);
		}
		return newRoutes;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void OnExecuteTask(final Task task) {

		mHandler.post(new Runnable() {
			public void run() {
				switch (task.getType()) {
				case START_TRACKING:
					mTrackingCount++;
					activateGPS(task.getWeight());
					break;
				case STOP_TRACKING:
					stopGPS(task.getWeight());
					mTrackingCount--;
					if (mTrackingCount == 0)
						for (Route r : mRoutes)
							r.latestIndex = 0;
					break;
				default:
					break;
				}

			}
		});

	}

}
