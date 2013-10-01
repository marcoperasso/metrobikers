package com.ecommuters;

import java.util.ArrayList;
import java.util.Date;
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
import android.text.format.DateFormat;
import android.util.Log;

public class ConnectorService extends Service implements LocationListener {

	private static final String CONNECTOR_SERVICE = "ConnectorService";
	private static final long getLocationTimeout = 30000; // 30 secondi tempo a

	private LocationManager mlocManager;
	private boolean isSynchingData;
	private Thread mWorkerThread;
	private Handler mHandler;

	// procedura di sincronizzazione itinerari dal e verso il server
	private long syncRoutesInterval = 300000;// 5 minuti
	private Runnable syncRoutesProcedureRunnable;

	// procedura di controllo della posizione se necessario
	private long checkPositionInterval = 60000;// 60 secondi
	private Runnable checkPositionProcedureRunnable;

	// procedura di invio della posizione corrente
	private long sendLatestPositionInterval = 30000;// 30 secondi
	private Runnable sendLatestPositionProcedureRunnable;

	private Timer requestingLocationTimeout = new Timer(true);
	private TimerTask getLocationTimerTask;
	private Runnable removeUpdatesRunnable;
	private boolean requestingLocation = false;
	private ECommuterPosition mLocation;
	private TrackingManager mTrackManager = new TrackingManager();
	private NotificationManager mNotificationManager;

	public ConnectorService() {
	}

	public void onCreate() {
		mWorkerThread = new Thread(new Runnable() {

			private GenericEvent onLiveTrackingChanged = new GenericEvent() {

				@Override
				public void onEvent(Object sender, EventArgs args) {
					if (liveTracking()) {
						activateGPSIfNeeded();
					} else {
						// se sto ascoltando il GPS, faccio partire il timer
						if (requestingLocation)
							startTimeoutTimer();
					}
				}
			};

			public void run() {
				MyApplication.getInstance().LiveTrackingChanged
						.addHandler(onLiveTrackingChanged);
				Looper.prepare();
				mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				mHandler = new Handler();
				syncRoutesProcedure();
				checkPositionProcedure();
				sendLatestPositionProcedure();
				Looper.loop();
				mlocManager.removeUpdates(ConnectorService.this);
				getLocationTimerTask = null;
				requestingLocation = false;
				mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);
				MyApplication.getInstance().LiveTrackingChanged
						.removeHandler(onLiveTrackingChanged);
				Log.d("ECOMMUTERS", "Worker thread ended");

			}
		});

		syncRoutesProcedureRunnable = new Runnable() {
			public void run() {
				syncRoutesProcedure();
			}
		};
		checkPositionProcedureRunnable = new Runnable() {
			public void run() {
				checkPositionProcedure();
			}
		};
		sendLatestPositionProcedureRunnable = new Runnable() {

			public void run() {
				sendLatestPositionProcedure();

			}

		};
		removeUpdatesRunnable = new Runnable() {
			public void run() {
				if (requestingLocation) {
					mlocManager.removeUpdates(ConnectorService.this);
					requestingLocation = false;
					getLocationTimerTask = null;
					mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);
				}

			}
		};
		mWorkerThread.setDaemon(true);
		mWorkerThread.start();
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		MyApplication.getInstance().setConnectorService(this);
		super.onCreate();
	}

	public void onLocationChanged(Location location) {
		mLocation = new ECommuterPosition(MySettings.CurrentCredentials.getUserId(), (int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6),
				(long) (System.currentTimeMillis() / 1E3));
		if (!requestingLocation)
			return;
		mTrackManager.locationChanged(mLocation);

		if (!liveTracking()) {
			mlocManager.removeUpdates(this);
			if (getLocationTimerTask != null)
				getLocationTimerTask.cancel();
			getLocationTimerTask = null;
			requestingLocation = false;
			mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);
		}
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
		java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);
		Date df = new java.util.Date((long) (mLocation.time * 1e3));
		String text = timeFormat.format(df) + ": "
				+ getString(R.string.tracking_position);
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

	private void checkPositionProcedure() {
		try {
			if (mTrackManager.isTimeForTracking())
				activateGPSIfNeeded();
		} catch (Exception e) {
			Log.e(CONNECTOR_SERVICE, e.toString());
		} finally {
			mHandler.postDelayed(checkPositionProcedureRunnable,
					checkPositionInterval);
		}
	}

	private void activateGPSIfNeeded() {
		if (!requestingLocation
				&& mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					10000/* 10 secondi */, 5/*
											 * due metri
											 */, this);
			requestingLocation = true;
			if (!liveTracking()) {
				startTimeoutTimer();
			}
		}
	}

	private void startTimeoutTimer() {
		if (getLocationTimerTask != null)
			return;
		getLocationTimerTask = new TimerTask() {
			@Override
			public void run() {
				requestingLocationTimeout.purge();
				mHandler.post(removeUpdatesRunnable);
			}
		};
		requestingLocationTimeout.schedule(getLocationTimerTask,
				getLocationTimeout);
	}

	private Boolean liveTracking() {
		return mTrackManager.liveTracking()
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

}
