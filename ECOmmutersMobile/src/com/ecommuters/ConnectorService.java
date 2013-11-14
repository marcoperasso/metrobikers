package com.ecommuters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ecommuters.Task.EventType;

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

/**
 * @author perasso
 * 
 */
public class ConnectorService extends Service implements LocationListener {

	private static final int TIMEOUT = 300000;

	public static final int DISTANCE_METERS = 40;

	private static final int MAX_DISTANCE_FROM_TRACK_METERS = 500;
	private LocationManager mlocManager;
	private Thread mWorkerThread;
	private Handler mHandler;
	private boolean automaticTracking;

	private Route[] mRoutes;

	// procedura di invio della posizione corrente
	private long sendLatestPositionInterval = 30000;// 30 secondi
	private Runnable sendLatestPositionProcedureRunnable;

	private GPSManager mGPSManager = new GPSManager();
	private ECommuterPosition mLocation;
	private NotificationManager mNotificationManager;
	private Timer mTimer = new Timer(true);
	private TimerTask timerTask = null;

	private List<Route> followedRoutes = new ArrayList<Route>();

	public ConnectorService() {
	}

	public static void resetGPSStatus() {
		ConnectorService connectorService = MyApplication.getInstance()
				.getConnectorService();
		if (connectorService != null)
			connectorService.resetGPS();
	}

	public static void executeTask(Task t) {
		//se ricevo un comando di stop e il servizio è già stoppato, non faccio nulla
		if (t.getType() == EventType.STOP_TRACKING && MyApplication.getInstance().getConnectorService()==null)
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

					Log.i(Const.ECOMMUTERS_TAG, "Stopping connector service");

				}
			});
			mWorkerThread.setDaemon(true);
			mWorkerThread.start();
		} else {
			onExecuteTask(task);
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
		// calcolo lo stato della mia posizione relativamente alle rotte
		// presenti
		double status = calculateRoutesByPosition(location);
		// -1: traccia teminata
		// 0: sono su una traccia
		// >0: distanza minima dalla traccia più vicina

		// cancello il timer perché la posizione è arrivata
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}

		if (status == -1)// traccia terminata
		{
			if (automaticTracking) {
				setAutomaticLiveTracking(false);
				Log.i(Const.ECOMMUTERS_TAG,
						"Stop automatic tracking: reached end of route.");
			}
			return;
		}

		boolean b = status == 0;// sulla traccia

		// se sono sulla traccia, faccio partire il timer per prevenire l
		// amancanza di segnale
		if (b)
			startTimeoutTimer();

		// se sono già nello stato di listening o non listening, non faccio
		// altro
		if (b == automaticTracking)
			return;

		// se non sono più sulla traccia, non mi metto subito fuori dal live
		// tracking, aspetto un po',
		// magari ci rientro... solo se mi allontano più
		// di 500 metri esco
		if (!b) {
			if (status < MAX_DISTANCE_FROM_TRACK_METERS) {
				startTimeoutTimer();
				return;
			}
			Log.i(Const.ECOMMUTERS_TAG,
					"Stop automatic tracking: out of route.");
		}

		setAutomaticLiveTracking(b);
	}

	private void startTimeoutTimer() {
		timerTask = new TimerTask() {

			@Override
			public void run() {
				Log.i(Const.ECOMMUTERS_TAG,
						"Stop automatic tracking: time out waiting for GPS.");

				setAutomaticLiveTracking(false);
				timerTask = null;
			}
		};
		mTimer.schedule(timerTask, TIMEOUT);
	}

	// -1: traccia terminata; 0: sulla traccia; numero positivo: fuori traccia,
	// distanza minima dalla traccia
	private double calculateRoutesByPosition(ECommuterPosition position) {
		double minRoutesDistance = Double.MAX_VALUE;

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

			minRoutesDistance = Math.min(minRoutesDistance, minRouteDistance);

			if (minRouteDistance < DISTANCE_METERS) {
				if (r.addTrackingPosition(index, position) && !followedRoutes.contains(r)) {
					followedRoutes.add(r);
					Log.i(Const.ECOMMUTERS_TAG,
							String.format("You are following route %s",
									r.getName()));
				}
			} else {
				if (followedRoutes.contains(r)) {
					followedRoutes.remove(r);
					Log.i(Const.ECOMMUTERS_TAG,
							String.format("You ended following route %s",
									r.getName()));
				}

			}
		}

		if (followedRoutes.size() == 0)
			return minRoutesDistance;
		boolean end = true;
		for (Route r : followedRoutes) {
			position.addRoute(r.getId());
			if (r.getLatestTrackedIndex() != r.getPoints().size() - 1) {
				end = false;
				break;
			}
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
						setGPSOnNotification();
					} else {
						// per prima cosa elimino il listener corrente
						mlocManager.removeUpdates(ConnectorService.this);
						String text = String
								.format("Position updated every %1$d seconds and %2$d meters",
										mGPSManager.getMinTimeSeconds(),
										mGPSManager.getMinDinstanceMeters());
						Log.i(Const.ECOMMUTERS_TAG, text);
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

						String text = String
								.format("Position updated every %1$d seconds and %2$d meters",
										mGPSManager.getMinTimeSeconds(),
										mGPSManager.getMinDinstanceMeters());
						Log.i(Const.ECOMMUTERS_TAG, text);

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
		if (!mGPSManager.requestingLocation())
			return;
		Credentials currentCredentials = MySettings.CurrentCredentials;
		ECommuterPosition loc = new ECommuterPosition(
				currentCredentials == null ? 0 : currentCredentials.getUserId(),
				(int) (location.getLatitude() * 1E6), (int) (location
						.getLongitude() * 1E6), location.getAccuracy(),
				(long) (System.currentTimeMillis() / 1E3));
		locationChanged(loc);
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
		intent.putExtra(Task.TASK, new Task(Calendar.getInstance(),
				EventType.STOP_TRACKING, GPSManager.MANUAL_TRACKING));
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
		if (mLocation.getRoutes().isEmpty() && !isManualTracking())
			return;

		String text = getString(R.string.tracking_position);
		Log.i(Const.ECOMMUTERS_TAG, text);
		setSendingPositionNotification();
		try {
			if (HttpManager.sendPositionData(mLocation))
				mLocation = null;
		} catch (Exception e) {

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
			activateGPS(task.getWeight());
			break;
		case STOP_TRACKING:
			stopGPS(task.getWeight());
			break;
		default:
			break;
		}

	}

	public static void setManualLiveTracking(boolean b) {
		executeTask(new Task(Calendar.getInstance(),
				b ? EventType.START_TRACKING : EventType.STOP_TRACKING,
				GPSManager.MANUAL_TRACKING));

	}

	private void setAutomaticLiveTracking(boolean b) {
		automaticTracking = b;
		if (automaticTracking) {
			activateGPS(GPSManager.AUTOMATIC_TRACKING);
		} else {
			stopGPS(GPSManager.AUTOMATIC_TRACKING);
		}
	}
}
