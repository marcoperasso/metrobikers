package com.ecommuters;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ConnectorService extends Service {

	private LocationManager mlocManager;
	private LocationListener mLocationListener;
	private long sendRecordingInterval = 30000;// 30 secondi
	private long sendPositionInterval = 30000;// 30 secondi
	private boolean isRequestingLocation = false;
	private long latestSendPositionTime = 0;
	private long latestSendRecordingTime = 0;
	private boolean isSendingData;
	private Thread mWorkerThread;
	private Handler mHandler;
	private Runnable threadProcedureRunnable;

	public ConnectorService() {
	}
	public void onCreate() {
		mWorkerThread = new Thread(new Runnable() {

			public void run() {

				Looper.prepare();
				mHandler = new Handler();
				onThreadProcedure();
				Looper.loop();
				Log.d("ECOMMUTERS", "Worker thread ended");

				mlocManager.removeUpdates(mLocationListener);
				isRequestingLocation = false;

			}
		});

		threadProcedureRunnable = new Runnable() {
			public void run() {
				onThreadProcedure();
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
		mlocManager.removeUpdates(mLocationListener);
		isRequestingLocation = false;
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
		latestSendPositionTime = System.currentTimeMillis();
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

	protected void onThreadProcedure() {
		try {
			if (Helper.isOnline(this)) {
				if (!isSendingData
						&& (System.currentTimeMillis() - latestSendRecordingTime) > sendRecordingInterval) {
					sendLocations();
					latestSendRecordingTime = System.currentTimeMillis();
				}

				if (!isRequestingLocation
						&& (System.currentTimeMillis() - latestSendPositionTime) > sendPositionInterval
						&& mlocManager
								.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					mlocManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 0, 0,
							mLocationListener);
					isRequestingLocation = true;
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);

			mHandler.post(threadProcedureRunnable);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
