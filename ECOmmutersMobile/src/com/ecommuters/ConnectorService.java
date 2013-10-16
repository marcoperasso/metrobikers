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

	private static final int DISTANCE_METERS = 40;

	private static final int MAX_DISTANCE_FROM_TRACK_METERS = 500;
	private static final String CONNECTOR_SERVICE = "ConnectorService";
	private LocationManager mlocManager;
	private Thread mWorkerThread;
	private Handler mHandler;
	private boolean automaticTracking;
	List<Route> mFollowedRoutes = new ArrayList<Route>();

	private Route[] mRoutes;

	// procedura di invio della posizione corrente
	private long sendLatestPositionInterval = 30000;// 30 secondi
	private Runnable sendLatestPositionProcedureRunnable;

	private GPSManager mGPSManager = new GPSManager();
	private ECommuterPosition mLocation;
	private NotificationManager mNotificationManager;
	private Timer mTimer = new Timer(true);
	private TimerTask timerTask = null;

	public ConnectorService() {
	}

	public static boolean isManualLiveTracking() {
		ConnectorService connectorService = MyApplication.getInstance()
				.getConnectorService();
		return connectorService == null ? false : connectorService
				.isManualTracking();
	}

	public static void resetGPSStatus() {
		ConnectorService connectorService = MyApplication.getInstance()
				.getConnectorService();
		if (connectorService != null)
			connectorService.resetGPS();
	}

	public static void executeTask(Task t) {

		Intent intent = new Intent(MyApplication.getInstance(),
				ConnectorService.class);
		intent.putExtra(Task.TASK, t);
		MyApplication.getInstance().startService(intent);
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
	public int onStartCommand(Intent intent, int flags, int startId) {
		final Task task = (Task) intent.getExtras().getSerializable(Task.TASK);
		if (task == null) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		if (mWorkerThread == null) {
			mWorkerThread = new Thread(new Runnable() {
				public void run() {
					Looper.prepare();

					mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					mHandler = new Handler();
					mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

					sendLatestPositionProcedure();

					if (task != null)
						OnExecuteTask(task);
					Looper.loop();
					for (Route r : mRoutes)
					{
						r.getTrackingInfo().reset();
					}
					mlocManager.removeUpdates(ConnectorService.this);
					mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);

					Log.d(Const.ECOMMUTERS_TAG, "Stopping connector service");

				}
			});
			mWorkerThread.setDaemon(true);
			mWorkerThread.start();
		} else {
			OnExecuteTask(task);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private boolean isManualTracking() {
		return mGPSManager.currentLevel == GPSManager.MANUAL_TRACKING;
	}

	public void onCreate() {
		Log.i(Const.ECOMMUTERS_TAG, "Starting connector service");

		sendLatestPositionProcedureRunnable = new Runnable() {
			public void run() {
				sendLatestPositionProcedure();
			}
		};
		mRoutes = MyApplication.getInstance().getRoutes();
		MyApplication.getInstance().setConnectorService(this);
		super.onCreate();
	}

	public void locationChanged(ECommuterPosition location) {
		double status = calculateRoutesByPosition(location);
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		if (status == -1)// traccia terminata
		{
			setAutomaticLiveTracking(false);
			Log.i(Const.ECOMMUTERS_TAG,
					"Stop automatic tracking: reached end of route.");
			return;
		}

		boolean b = status == 0;// sulla traccia
		if (b) {
			timerTask = new TimerTask() {

				@Override
				public void run() {
					Log.i(Const.ECOMMUTERS_TAG,
							"Stop automatic tracking: time out waiting for GPS.");

					setAutomaticLiveTracking(false);
				}
			};
			mTimer.schedule(timerTask, TIMEOUT);
		}
		if (b == automaticTracking)
			return;
		// se non sono più sulla traccia, non mi metto subito fuori dal live
		// tracking, aspetto un po',
		// magari ci rientro... solo se mi allontano più
		// di 500 metri esco
		if (!b) {
			if (status < MAX_DISTANCE_FROM_TRACK_METERS)
				return;
			Log.i(Const.ECOMMUTERS_TAG,
					"Stop automatic tracking: out of route.");

		}

		setAutomaticLiveTracking(b);
	}

	private void setAutomaticLiveTracking(boolean b) {
		automaticTracking = b;

		if (automaticTracking) {
			activateGPS(GPSManager.AUTOMATIC_TRACKING);
		} else {
			stopGPS(GPSManager.AUTOMATIC_TRACKING);
		}
	}

	// -1: traccia terminata; 0: sulla traccia; numero positivo: fuori traccia,
	// distanza minima dalla traccia
	private double calculateRoutesByPosition(ECommuterPosition position) {
		double minDistance = Double.MAX_VALUE;
		for (Route r : mRoutes) {
			boolean followed = false;
			for (int i = r.getTrackingInfo().getLatestIndex(); i < r.getPoints().size(); i++) {
				RoutePoint pt = r.getPoints().get(i);
				double distance = position.distance(pt);
				minDistance = Math.min(minDistance, distance);
				if (distance < DISTANCE_METERS) {
					r.getTrackingInfo().setLatestIndex(i);
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
		if (mFollowedRoutes.size() == 0)
			return minDistance;
		boolean end = true;
		for (Route r : mFollowedRoutes)
			if (r.getTrackingInfo().getLatestIndex() != r.getPoints().size() - 1) {
				end = false;
				break;
			}
		return end ? -1 : 0;
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
						stopSelf();
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
					stopSelf();
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
		if (mHandler != null) {
			mHandler.post(new Runnable() {

				public void run() {
					Looper.myLooper().quit();

				}
			});
		}
		super.onDestroy();
	}

	private Boolean liveTracking() {
		return automaticTracking || isManualTracking();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void OnExecuteTask(final Task task) {
		Log.i(Const.ECOMMUTERS_TAG, String.format(
				"Executing task %s with weight: %d.",
				task.getType().toString(), task.getWeight()));

		mHandler.post(new Runnable() {
			public void run() {
				switch (task.getType()) {
				case START_TRACKING:
					activateGPS(task.getWeight());
					break;
				case STOP_TRACKING:
					stopGPS(task.getWeight());
					break;
				default:
					break;
				}

			}
		});

	}

}
