package com.ecommuters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ecommuters.Task.EventType;

/**
 * @author perasso
 * 
 */
public class ConnectorService extends Service implements LocationListener {

	class MyFollowedRoute implements Runnable {
		private Route route;

		public MyFollowedRoute(Route route) {
			this.route = route;
		}

		public void run() {
			setAutomaticLiveTracking(false, route.getId());

		}

	}

	class MyFollowedRouteList extends ArrayList<MyFollowedRoute> {

		/**
	 * 
	 */
		private static final long serialVersionUID = -1708891018273860036L;

		@Override
		public boolean contains(Object object) {
			Route r = castToRoute(object);
			return get(r) != null;
		}

		MyFollowedRoute get(Route r) {
			for (MyFollowedRoute fr : this)
				if (fr.route == r)
					return fr;
			return null;
		}

		private Route castToRoute(Object object) {
			Route r = null;
			if (object instanceof Route)
				r = (Route) object;
			else if (object instanceof MyFollowedRoute)
				r = ((MyFollowedRoute) object).route;
			return r;
		}

	}

	private static final int TIMEOUT = 300000;

	public static final int DISTANCE_METERS = 40;

	private static final int MAX_DISTANCE_FROM_TRACK_METERS = 500;
	private LocationManager mlocManager;
	private Thread mWorkerThread;
	private Handler mHandler;

	private Route[] mRoutes;

	// procedura di invio della posizione corrente
	private long sendLatestPositionInterval = 30000;// 30 secondi
	private Runnable sendLatestPositionProcedureRunnable;

	private ECommuterPosition mLocation;
	private NotificationManager mNotificationManager;
	private GPSManager gpsManager = new GPSManager();

	private MyFollowedRouteList followedRoutes = new MyFollowedRouteList();

	public ConnectorService() {
	}

	public static void resetGPSStatus() {
		ConnectorService connectorService = MyApplication.getInstance()
				.getConnectorService();
		if (connectorService != null)
			connectorService.resetGPS();
	}

	public static void executeTask(Task t) {
		// se ricevo un comando di stop e il servizio � gi� stoppato, non
		// faccio
		// nulla
		if (t.getType() == EventType.STOP_TRACKING
				&& MyApplication.getInstance().getConnectorService() == null)
			return;

		Intent intent = new Intent(MyApplication.getInstance(),
				ConnectorService.class);
		intent.putExtra(Task.TASK, t);
		MyApplication.getInstance().startService(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null || intent.getExtras() == null) {
			Log.d(Const.ECOMMUTERS_TAG,
					"Receiving connector service start command with no data, the service will be stopped");
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		Serializable obj = intent.getExtras().getSerializable(Task.TASK);
		if (obj == null) {
			Log.d(Const.ECOMMUTERS_TAG,
					"Receiving connector service start command with no data, the service will be stopped");
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		Log.d(Const.ECOMMUTERS_TAG, "Receiving connector service start command");

		final Task task = (Task) obj;

		if (!task.canExecuteToday(mRoutes)) {
			Log.d(Const.ECOMMUTERS_TAG,
					"This task cannot be executed today, the service will be stopped");
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
						onExecuteTask(task);
					Looper.loop();
					boolean saved = false;
					for (Route r : mRoutes) {
						if (r.saveTrackingInfo())
							saved = true;

					}
					if (saved) {
						// faccio partire il servizio che lo manda al server
						Intent service = new Intent(ConnectorService.this,
								SyncService.class);
						startService(service);
					}
					mlocManager.removeUpdates(ConnectorService.this);
					mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);
					mNotificationManager
							.cancel(Const.SENDING_POSITION_NOTIFICATION_ID);

					Log.i(Const.ECOMMUTERS_TAG,
							"Finished connector service worker thread");

				}
			});
			mWorkerThread.setDaemon(true);
			mWorkerThread.setName("Connector Service Worker");
			mWorkerThread.start();
		} else {
			mHandler.post(new Runnable() {
				public void run() {
					onExecuteTask(task);
				}
			});
		}
		return super.onStartCommand(intent, flags, startId);
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

	// -1: traccia terminata; 0: sulla traccia; numero positivo: fuori traccia,
	// distanza minima dalla traccia
	private void calculateRoutesByPosition(ECommuterPosition position) {
		for (Route r : mRoutes) {
			double minRouteDistance = Double.MAX_VALUE;
			int index = -1;
			for (int i = r.getLatestTrackedIndex(); i < r.getPoints().size(); i++) {
				RoutePoint pt = r.getPoints().get(i);
				double distance = position.distance(pt);
				if (distance < minRouteDistance) {
					index = i;
					minRouteDistance = distance;
				}
			}

			// posticipo i timeout
			for (MyFollowedRoute run : followedRoutes) {
				mHandler.removeCallbacks(run);
				mHandler.postDelayed(run, TIMEOUT);
			}

			if (minRouteDistance < DISTANCE_METERS) {// sono sulla traccia
				Log.d(Const.ECOMMUTERS_TAG,
						String.format("DENTRO l'itinerario %s; distanza: %f",
								r.getName(), minRouteDistance));
				if (r.addTrackingPosition(index, position)
						&& !followedRoutes.contains(r)) {
					MyFollowedRoute myFollowedRoute = new MyFollowedRoute(r);
					followedRoutes.add(myFollowedRoute);
					mHandler.postDelayed(myFollowedRoute, TIMEOUT);
					setAutomaticLiveTracking(true, r.getId());
					Log.i(Const.ECOMMUTERS_TAG,
							String.format("You entered route %s", r.getName()));
				}
			} else if (minRouteDistance > MAX_DISTANCE_FROM_TRACK_METERS) { // mi
																			// sono
																			// allontanato
																			// molto
																			// dalla
																			// traccia
																			// (sono
																			// uscito)
				Log.d(Const.ECOMMUTERS_TAG, String.format(
						"FUORI dall'itinerario %s; distanza: %f", r.getName(),
						minRouteDistance));
				if (followedRoutes.contains(r)) {
					MyFollowedRoute mfr = followedRoutes.get(r);
					followedRoutes.remove(mfr);
					mHandler.removeCallbacks(mfr);
					setAutomaticLiveTracking(false, r.getId());
					Log.i(Const.ECOMMUTERS_TAG,
							String.format("You exited route %s", r.getName()));
				}

			} else {
				Log.d(Const.ECOMMUTERS_TAG, String.format(
						"QUASI FUORI dall'itinerario %s; distanza: %f",
						r.getName(), minRouteDistance));
			}
		}

		if (followedRoutes.size() == 0)
			return;
		for (MyFollowedRoute fr : followedRoutes) {
			Route r = fr.route;
			position.addRoute(r.getId());
			if (r.getLatestTrackedIndex() == r.getPoints().size() - 1) {
				setAutomaticLiveTracking(false, r.getId());
				Log.i(Const.ECOMMUTERS_TAG,
						String.format("You reached the end of the route %s",
								r.getName()));
			}
		}
	}

	private void activateGPS(final int level, final int routeId) {
		mHandler.post(new Runnable() {
			public void run() {
				boolean wasListening = gpsManager.requestingLocation();
				// se cambio di livello, cambio anche il listener 8cambia la
				// sensibilit'
				// di tracciatura(
				if (gpsManager.startGPS(level, routeId)) {
					// se entro qui dentro � perch� il livello � cambiato
					if (!wasListening) {
						setGPSOnNotification();
					} else {
						// per prima cosa elimino il listener corrente
						mlocManager.removeUpdates(ConnectorService.this);
						String text = String
								.format("Position updated every %1$d seconds and %2$d meters",
										gpsManager.getMinTimeSeconds(),
										gpsManager.getMinDinstanceMeters());
						Log.i(Const.ECOMMUTERS_TAG, text);
					}
					mlocManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							gpsManager.getMinTimeSeconds() * 1000,
							gpsManager.getMinDinstanceMeters(),
							ConnectorService.this);

				}

			}
		});

	}

	private void stopGPS(final int level, final int routeId) {
		mHandler.post(new Runnable() {

			public void run() {
				// se sono arrivato a livello zero, fermo il gps, altrimenti
				// aggiusto la
				// frequenza di aggiornamento
				gpsManager.stopGPS(level, routeId);
				// per prima cosa elimino il listener corrente
				mlocManager.removeUpdates(ConnectorService.this);
				if (gpsManager.requestingLocation()) {

					String text = String
							.format("Position updated every %1$d seconds and %2$d meters",
									gpsManager.getMinTimeSeconds(),
									gpsManager.getMinDinstanceMeters());
					Log.i(Const.ECOMMUTERS_TAG, text);

					// poi, se devo continuare a registrare su un livello
					// diverso,
					// mi ri registro
					mlocManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							gpsManager.getMinTimeSeconds() * 1000,
							gpsManager.getMinDinstanceMeters(),
							ConnectorService.this);
				} else {
					stopSelf();
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
				gpsManager.resetLevels();
				if (!gpsManager.requestingLocation()) {
					stopSelf();
				}

			}
		});

	}

	public void onLocationChanged(Location location) {
		if (!gpsManager.requestingLocation())
			return;
		Credentials currentCredentials = MySettings.CurrentCredentials;
		ECommuterPosition loc = new ECommuterPosition(
				currentCredentials == null ? 0 : currentCredentials.getUserId(),
				(int) (location.getLatitude() * 1E6), (int) (location
						.getLongitude() * 1E6), location.getAccuracy(),
				(long) (System.currentTimeMillis() / 1E3));
		calculateRoutesByPosition(loc);
		mLocation = loc;
	}

	public void onProviderEnabled(String provider) {
		Log.i(Const.ECOMMUTERS_TAG, getString(R.string.gps_enabled));
	}

	public void onProviderDisabled(String provider) {
		Log.i(Const.ECOMMUTERS_TAG, getString(R.string.gps_disabled));
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status) {
		case LocationProvider.AVAILABLE:
			break;
		case LocationProvider.OUT_OF_SERVICE:
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			break;
		}
	}

	private void sendLatestPositionProcedure() {
		sendLatestPosition();
		mHandler.postDelayed(sendLatestPositionProcedureRunnable,
				sendLatestPositionInterval);

	}

	private void setGPSOnNotification() {
		String message = getString(R.string.live_tracking_on);
		Intent intent = new Intent(this, ConnectorService.class);
		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, // add
																				// this
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.livetracking)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(message).setContentIntent(contentIntent);

		Notification notification = mBuilder.build();
		notification.defaults |= Notification.DEFAULT_ALL;
		mNotificationManager.notify(Const.TRACKING_NOTIFICATION_ID,
				notification);
	}

	private void setSendingPositionNotification() {
		String message = getString(R.string.tracking_position);
		Intent intent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_routemarker)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(message).setContentIntent(contentIntent);

		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(Const.SENDING_POSITION_NOTIFICATION_ID,
				notification);
	}

	private void sendLatestPosition() {
		if (mLocation == null || !Helper.isOnline(ConnectorService.this))
			return;

		// mando la posizione solo se sono su un itinerario, oppure sono in
		// stato di live tracking manuale
		if (mLocation.getRoutes().isEmpty() && !gpsManager.isManualTracking())
			return;

		String text = getString(R.string.tracking_position);
		Log.i(Const.ECOMMUTERS_TAG, text);
		setSendingPositionNotification();
		final ECommuterPosition loc = mLocation;
		new Thread(){
			@Override
			public void run() {
				try {
					if (HttpManager.sendPositionData(loc))
						mLocation = null;
				} catch (Exception e) {
					Log.e(Const.ECOMMUTERS_TAG, e.toString());
				}
				super.run();
			}
		}.start();
		
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
		try {
			if (mWorkerThread != null)
				mWorkerThread.join();
		} catch (InterruptedException e) {
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onExecuteTask(final Task task) {
		Log.i(Const.ECOMMUTERS_TAG, String.format(
				"Executing task %s with weight: %d.",
				task.getType().toString(), task.getWeight()));

		switch (task.getType()) {
		case START_TRACKING:
			activateGPS(task.getWeight(), task.getRouteId());
			return;
		case STOP_TRACKING:
			stopGPS(task.getWeight(), task.getRouteId());
			return;
		default:
			return;
		}

	}

	public static void setManualLiveTracking(boolean b) {
		executeTask(new Task(Calendar.getInstance(),
				b ? EventType.START_TRACKING : EventType.STOP_TRACKING,
				GPSStatus.MANUAL_TRACKING, 0));

	}

	private void setAutomaticLiveTracking(boolean b, int routeId) {
		if (b) {
			activateGPS(GPSStatus.AUTOMATIC_TRACKING, routeId);
		} else {
			stopGPS(GPSStatus.AUTOMATIC_TRACKING, routeId);
		}
	}

	public boolean isManualLiveTracking() {
		return gpsManager.isManualTracking();
	}
}
