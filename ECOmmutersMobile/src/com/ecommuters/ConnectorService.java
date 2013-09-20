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

public class ConnectorService extends Service {

	private static final long getLocationTimeout = 10000; // 10 secondi
	private LocationManager mlocManager;
	private LocationListener mLocationListener;
	private long sendRoutesInterval = 30000;// 30 secondi
	private long sendPositionInterval = 60000;// 60 secondi
	private boolean isSendingData;
	private Thread mWorkerThread;
	private Handler mHandler;
	private Runnable sendRoutesProcedureRunnable;
	private Runnable sendPositionProcedureRunnable;
	private Timer requestingLocationTimeout = new Timer(true);
	private TimerTask getLocationTimerTask;
	private Runnable removeUpdatesRunnable;
	private boolean requestingLocation = false;
	private boolean liveTracking = false;

	public ConnectorService() {
	}
	public void onCreate() {
		mWorkerThread = new Thread(new Runnable() {

			public void run() {

				Looper.prepare();
				mHandler = new Handler();
				sendRoutesProcedure();
				sendPositionProcedure();
				Looper.loop();
				mlocManager.removeUpdates(mLocationListener);
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
		sendPositionProcedureRunnable = new Runnable() {
			public void run() {
				sendPositionProcedure();
			}
		};

		removeUpdatesRunnable = new Runnable() {
			public void run() {
				if (requestingLocation) {
					mlocManager.removeUpdates(mLocationListener);
					requestingLocation = false;
					getLocationTimerTask = null;
				}

			}
		};
		mWorkerThread.setDaemon(true);
		mWorkerThread.start();
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener() {

			public void onLocationChanged(Location location) {

				recordLocation(location);
			}

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		};

		MyApplication.getInstance().setConnectorService(this);
		super.onCreate();
	}

	private void recordLocation(Location location) {
		RoutePoint routePoint = new RoutePoint(0,
				(int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6),
				(long) (System.currentTimeMillis() / 1E3));
		if (!liveTracking) {
			mlocManager.removeUpdates(mLocationListener);
			if (getLocationTimerTask != null)
				getLocationTimerTask.cancel();
			getLocationTimerTask = null;
			requestingLocation = false;
		}
		sendMyPosition(routePoint);
	}
	private void sendMyPosition(final RoutePoint routePoint) {
		if (!Helper.isOnline(ConnectorService.this))
			return;

		testCredentials(new OnAsyncResponse() {
			public void response(boolean success, String message) {

				if (!success)
					return;
				try {
					RequestBuilder.sendPositionData(routePoint);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				sendLocations();
		} finally {
			mHandler.postDelayed(sendRoutesProcedureRunnable,
					sendRoutesInterval);
		}
	}
	private void sendPositionProcedure() {
		try {
			if (Helper.isOnline(this)
					&& !requestingLocation
					&& mlocManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mlocManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 10000/* 10 secondi */, 2/*
																		 * due
																		 * metri
																		 */,
						mLocationListener);
				requestingLocation = true;
				if (!liveTracking) {
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
		} finally {
			mHandler.postDelayed(sendPositionProcedureRunnable,
					sendPositionInterval);
		}
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

	private void sendLocations() {
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
	public boolean isLiveTracking() {
		return liveTracking;
	}
	public void setLiveTracking(boolean b)
	{
		this.liveTracking = b;
		if (!b)
			mHandler.post(removeUpdatesRunnable);
	}
	
}
