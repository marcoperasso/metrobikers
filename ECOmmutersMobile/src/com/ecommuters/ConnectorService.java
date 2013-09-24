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
	private boolean isSendingData;
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

			public void run() {

				Looper.prepare();
				mHandler = new Handler();
				sendRoutesProcedure();
				checkPositionProcedure();
				sendLatestPositionProcedure();
				Looper.loop();
				mlocManager.removeUpdates(ConnectorService.this);
				getLocationTimerTask = null;
				requestingLocation = false;
				Log.d("ECOMMUTERS", "Worker thread ended");

			}
		});

		sendRoutesProcedureRunnable = new Runnable() {
			public void run() {
				sendRoutesProcedure();
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
		if (mLocation == null || !liveTracking() || !Helper.isOnline(ConnectorService.this))
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

	private void sendRoutesProcedure() {
		try {
			if (Helper.isOnline(this) && !isSendingData)
				sendRoutes();
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
			}
			
		} catch (Exception e) {
			Log.e(CONNECTOR_SERVICE, e.toString());
		} finally {
			mHandler.postDelayed(checkPositionProcedureRunnable,
					checkPositionInterval);
		}
	}

	private Boolean liveTracking() {
		return MyApplication.getInstance().isLiveTracking();
	}

	private void downloadRoutes() throws IOException, JSONException {

		if (!Helper.isOnline(this)) {
			return;
		}
		List<Route> rr = RequestBuilder.getRoutes();
		StringBuilder message = new StringBuilder();
		int saved = 0;
		for (Route r : rr) {
			String routeFile = Helper.getRouteFile(r.getName());
			if (getFileStreamPath(routeFile).exists()) {
				Route existing = Route.readRoute(ConnectorService.this,
						routeFile);
				if (existing != null
						&& existing.getLatestUpdate() >= r.getLatestUpdate()) {
					message.append(String.format(
							getString(R.string.route_already_existing),
							r.getName()));
					continue;
				}
			}
			r.save(ConnectorService.this, routeFile);
			saved++;
		}
		if (saved > 0)
			MyApplication.getInstance().refreshRoutes();

		message.append(String.format(
				getString(R.string.route_succesfully_downloaded), saved));

	}

	private void sendRoutes() {
		if (isSendingData)
			return;
		isSendingData = true;
		final List<File> files = Helper.getFiles(this, Const.TOSENDEXT);

		if (files.size() == 0) {
			isSendingData = false;
			return;
		}

		testCredentials(new OnAsyncResponse() {
			public void response(boolean success, String message) {

				try {
					if (!success)
						return;
					for (File file : files) {
						try {
							Route route = Route.readRoute(
									ConnectorService.this, file.getName());
							if (route != null
									&& RequestBuilder.sendRouteData(route))
								file.delete();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} finally {
					isSendingData = false;
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
