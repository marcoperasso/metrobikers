package com.ecommuters;

import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;

public class ConnectorService extends Service implements LocationListener {

	private static final String CONNECTOR_SERVICE = "ConnectorService";
	private LocationManager mlocManager;
	private boolean isSynchingData;
	private Thread mWorkerThread;
	private Handler mHandler;

	// procedura di sincronizzazione itinerari dal e verso il server
	private long syncRoutesInterval = 300000;// 5 minuti
	private Runnable syncRoutesProcedureRunnable;

	// procedura di invio della posizione corrente
	private long sendLatestPositionInterval = 30000;// 30 secondi
	private Runnable sendLatestPositionProcedureRunnable;

	private GPSManager mGPSManager = new GPSManager();
	private ECommuterPosition mLocation;
	private TrackingManager mTrackManager;
	private NotificationManager mNotificationManager;

	public ConnectorService() {
	}

	public void onCreate() {
		mWorkerThread = new Thread(new Runnable() {

			private LiveTrackingEventHandler onLiveTrackingChanged = new LiveTrackingEventHandler() {

				@Override
				public void onEvent(Object sender, LiveTrackingEventArgs args) {
					if (args.isActive()) {
						activateGPS(GPSManager.MAX_GPS_LEVELS);
					} else {
						stopGPS(GPSManager.MAX_GPS_LEVELS);
					}
				}
			};

			private GenericEventHandler onRoutesChanged = new GenericEventHandler() {

				@Override
				public void onEvent(Object sender, EventArgs args) {
					mTrackManager.scheduleLiveTracking();
				}
			};

			public void run() {
				MyApplication.getInstance().ManualLiveTrackingChanged
						.addHandler(onLiveTrackingChanged);
				MyApplication.getInstance().RouteChanged
						.addHandler(onRoutesChanged);

				Looper.prepare();
				mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				mHandler = new Handler();
				mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

				mTrackManager = new TrackingManager(mHandler,
						ConnectorService.this);
				mTrackManager.LiveTrackingEvent
						.addHandler(onLiveTrackingChanged);
				mTrackManager.scheduleLiveTracking();
				syncRoutesProcedure();
				sendLatestPositionProcedure();
				Looper.loop();
				mlocManager.removeUpdates(ConnectorService.this);
				mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);
				MyApplication.getInstance().ManualLiveTrackingChanged
						.removeHandler(onLiveTrackingChanged);
				MyApplication.getInstance().RouteChanged
						.removeHandler(onRoutesChanged);
				mTrackManager.LiveTrackingEvent
						.removeHandler(onLiveTrackingChanged);
				Log.d("ECOMMUTERS", "Worker thread ended");

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
		mWorkerThread.start();

		MyApplication.getInstance().setConnectorService(this);
		super.onCreate();
	}

	private void activateGPS(int level) {
		boolean wasListening = mGPSManager.requestingLocation();
		// se cambio di livello, cambio anche il listener 8cambia la sensibilit'
		// di tracciatura(
		if (mGPSManager.startGPS(level)) {
			// se entro qui dentro è perché il livello è cambiato
			if (!wasListening) {
				// per prima cosa elimino il listener corrente
				mlocManager.removeUpdates(this);
				// solo quando inizio a tracciare mando il messaggio di tracking
				// attivato
				String text = getString(R.string.live_tracking_on) + " " +
						getString(R.string.live_tracking_frequency,
								mGPSManager.getMinTimeSeconds(),
						mGPSManager.getMinDinstanceMeters());
				setNotification(text);
			} else {
				String text = getString(R.string.live_tracking_frequency,
						mGPSManager.getMinTimeSeconds(),
						mGPSManager.getMinDinstanceMeters());
				setNotification(text);
			}
			mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					mGPSManager.getMinTimeSeconds() * 1000,
					mGPSManager.getMinDinstanceMeters(), this);

		}
	}

	private void stopGPS(int level) {
		// se sono arrivato a livello zero, fermo il gps, altrimenti aggiusto la
		// frequenza di aggiornamento
		if (mGPSManager.stopGPS(level)) {
			// se entro qui dentro è perché il livello è cambiato
			// per prima cosa elimino il listener corrente
			mlocManager.removeUpdates(this);
			if (mGPSManager.requestingLocation()) {

				String text = getString(R.string.live_tracking_frequency,
						mGPSManager.getMinTimeSeconds(),
						mGPSManager.getMinDinstanceMeters());
				setNotification(text);

				// poi, se devo continuare a registrare su un livello diverso,
				// mi ri registro
				mlocManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, mGPSManager.getMinTimeSeconds()*1000,
						mGPSManager.getMinDinstanceMeters(), this);
			} else {
				mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);
			}
		}
	}

	public void onLocationChanged(Location location) {
		Credentials currentCredentials = MySettings.CurrentCredentials;
		mLocation = new ECommuterPosition(currentCredentials == null ? 0
				: currentCredentials.getUserId(),
				(int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6),
				(long) (System.currentTimeMillis() / 1E3));
		if (!mGPSManager.requestingLocation())
			return;
		mTrackManager.locationChanged(mLocation);
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

	private void setNotification(String message) {
		Notification notification = new Notification(R.drawable.livetracking,
				message, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		Intent intent = new Intent(this, MyMapActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				message, contentIntent);
		mNotificationManager.notify(Const.TRACKING_NOTIFICATION_ID,
				notification);
	}

	private void sendLatestPosition() {
		if (mLocation == null || !liveTracking()
				|| !Helper.isOnline(ConnectorService.this))
			return;
		String text = getString(R.string.tracking_position);
		setNotification(text);

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
		return mTrackManager.isLiveTracking()
				|| MyApplication.getInstance().isLiveTracking();
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

	public void OnExecuteTask(Task task) {
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

}
