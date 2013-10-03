package com.ecommuters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ecommuters.LiveTrackingReceiver.EventType;

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

	private Runnable stopGPSRunnable;
	private int requestingLocation = 0;
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
						activateGPS();
					} else {
						stopGPS();
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
				sendLatestPositionProcedure();
				Looper.loop();
				stopGPS();
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

		sendLatestPositionProcedureRunnable = new Runnable() {
			public void run() {
				sendLatestPositionProcedure();
			}
		};
		stopGPSRunnable = new Runnable() {
			public void run() {
				stopGPS();
			}
		};
		mWorkerThread.setDaemon(true);
		mWorkerThread.start();
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		MyApplication.getInstance().setConnectorService(this);
		super.onCreate();
	}

	private void activateGPS() {
		if (requestingLocation == 0) {
			String text = getTimeString(System.currentTimeMillis()) + ": "
					+ getString(R.string.live_tracking_on);
			setNotification(text);
			mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					10000/* 10 secondi */, 5/*
											 * due metri
											 */, this);
			
		}
		requestingLocation++;
	}

	private void stopGPS() {
		requestingLocation--;
		if (requestingLocation == 0) {
			mlocManager.removeUpdates(ConnectorService.this);
			mNotificationManager.cancel(Const.TRACKING_NOTIFICATION_ID);
		}
	}

	public void onLocationChanged(Location location) {
		mLocation = new ECommuterPosition(
				MySettings.CurrentCredentials.getUserId(),
				(int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6),
				(long) (System.currentTimeMillis() / 1E3));
		if (requestingLocation == 0)
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
		String text = getTimeString((long) (mLocation.time * 1e3)) + ": "
				+ getString(R.string.tracking_position);
		setNotification(text);

		try {
			if (RequestBuilder.sendPositionData(mLocation))
				mLocation = null;
		} catch (Exception e) {
			Log.e(CONNECTOR_SERVICE, e.toString());
		}
	}

	private String getTimeString(long time) {
		java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);
		Date df = new java.util.Date(time);
		String format = timeFormat.format(df);
		return format;
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

	private void internalUpdateLiveTrackingData(EventType id, String routeName) {
		switch (id) {
		case START_TRACKING:
			activateGPS();
			break;
		case STOP_TRACKING:
			stopGPS();
			break;
		default:
			break;
		}

	}

	public void updateLiveTrackingData(final EventType id,
			final String routeName) {
		mHandler.post(new Runnable() {

			public void run() {
				internalUpdateLiveTrackingData(id, routeName);
			}

		});

	}

}
