package com.ecommuters;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

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
	private static final long getLocationTimeout = 10000; // 10 secondi
	private LocationManager mlocManager;
	private long sendRoutesInterval = 30000;// 30 secondi
	private long checkPositionInterval = 60000;// 60 secondi
	private long sendLatestPositionInterval = 5000;// 5 secondi
	private boolean isSynchingData;
	private Thread mWorkerThread;
	private Handler mHandler;
	private Runnable sendRoutesProcedureRunnable;
	private Runnable checkPositionProcedureRunnable;
	private Timer requestingLocationTimeout = new Timer(true);
	private TimerTask getLocationTimerTask;
	private Runnable removeUpdatesRunnable;
	private Runnable sendLatestPositionProcedureRunnable;
	private boolean requestingLocation = false;
	private ECommuterPosition mLocation;

	public ConnectorService() {
	}

	public void onCreate() {
		mWorkerThread = new Thread(new Runnable() {

			private GenericEvent onLiveTrackingChanged = new GenericEvent() {

				@Override
				public void onEvent(Object sender, EventArgs args) {
					if (liveTracking()) {
						if (!requestingLocation)
							checkPositionProcedure();
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
				mHandler = new Handler();
				syncRoutesProcedure();
				checkPositionProcedure();
				sendLatestPositionProcedure();
				Looper.loop();
				mlocManager.removeUpdates(ConnectorService.this);
				getLocationTimerTask = null;
				requestingLocation = false;
				MyApplication.getInstance().LiveTrackingChanged
						.removeHandler(onLiveTrackingChanged);
				Log.d("ECOMMUTERS", "Worker thread ended");

			}
		});

		sendRoutesProcedureRunnable = new Runnable() {
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
		mLocation = new ECommuterPosition((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6),
				(long) (System.currentTimeMillis() / 1E3));
		if (!liveTracking()) {
			mlocManager.removeUpdates(this);
			if (getLocationTimerTask != null)
				getLocationTimerTask.cancel();
			getLocationTimerTask = null;
			requestingLocation = false;
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

	private void sendLatestPosition() {
		if (mLocation == null || !liveTracking()
				|| !Helper.isOnline(ConnectorService.this))
			return;

		testCredentials(new OnAsyncResponse() {
			public void response(boolean success, String message) {

				if (!success)
					return;
				try {
					if (RequestBuilder.sendPositionData(mLocation))
						mLocation = null;
				} catch (Exception e) {
					Log.e(CONNECTOR_SERVICE, e.toString());
				}

			}
		});
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
			mHandler.postDelayed(sendRoutesProcedureRunnable,
					sendRoutesInterval);
		}
	}

	private void checkPositionProcedure() {
		try {
			if (!requestingLocation
					&& mlocManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mlocManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 10000/* 10 secondi */,
						5/*
						 * due metri
						 */, this);
				requestingLocation = true;
				if (!liveTracking()) {
					startTimeoutTimer();
				}
			}

		} catch (Exception e) {
			Log.e(CONNECTOR_SERVICE, e.toString());
		} finally {
			mHandler.postDelayed(checkPositionProcedureRunnable,
					checkPositionInterval);
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
		return MyApplication.getInstance().isLiveTracking();
	}

	private void syncRoutes() {
		if (isSynchingData)
			return;
		isSynchingData = true;

		testCredentials(new OnAsyncResponse() {
			public void response(boolean success, String message) {

				try {
					if (!success)
						return;

					long latestUpdate = MySettings
							.getLatestSyncDate(ConnectorService.this);

					for (Route r : MyApplication.getInstance().getRoutes()) {
						if (r.getLatestUpdate() > latestUpdate
								&& !RequestBuilder.sendRouteData(r))
							throw new Exception("Cannot send route to server: " + r.getName());

					}

					List<Route> rr = RequestBuilder.getRoutes(latestUpdate);
					boolean saved = false;
					for (Route r : rr) {
						String routeFile = Helper.getRouteFile(r.getName());
						r.save(ConnectorService.this, routeFile);
						saved = true;
					}

					if (saved)
						MyApplication.getInstance().refreshRoutes();

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

	private void testCredentials(OnAsyncResponse testResponse) {
		if (RequestBuilder.isLogged()) {
			testResponse.response(true, "");
			return;
		}
		Credentials credential = MySettings.readCredentials(this);
		if (credential.isEmpty()) {
			testResponse.response(false, "");
			return;
		}
		credential.testLogin(this, testResponse);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
