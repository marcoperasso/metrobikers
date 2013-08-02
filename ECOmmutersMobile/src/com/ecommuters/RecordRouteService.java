package com.ecommuters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class RecordRouteService extends IntentService {

	boolean working = true;
	private LocationManager mlocManager;
	private LocationListener mLocationListener;

	// acceduto da diversi thread: va sincronizzato
	// sono i punti registrati ma non ancora salvati
	private List<RoutePoint> mRecorderLocations = new ArrayList<RoutePoint>();

	// liste accedute dal solo worker thread del servizio, non vanno
	// sincronizzate
	// lista dei punti da salvare
	private List<RoutePoint> mLocationsToSave = new ArrayList<RoutePoint>();
	// lista dei punti salvati
	private Route mSavedRoute;
	private String mRouteName;
	private int latestFileToSendIndex;
	private NotificationManager mNotificationManager;
	private RecordRouteBinder mBinder;

	public RecordRouteService() {
		super("RegisterRouteService");
	}

	private int getPoints() {
		return mRecorderLocations.size() + mLocationsToSave.size()
				+ mSavedRoute.getPoints().size();
	}
	boolean isWorking() {
		return working;
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		mRouteName = intent.getExtras().getString(Const.ROUTE_NAME);

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		String routeFile = Helper.getRouteFile(mRouteName);

		mSavedRoute = Route.readRoute(this, routeFile);
		if (mSavedRoute == null)
			mSavedRoute = new Route(mRouteName);
		setNotification(getString(R.string.recording_details, mRouteName,
				getPoints()));

		while (working) {
			try {
				Thread.sleep(5000);
				archiveData();

			} catch (InterruptedException e) {
				Log.e(Const.LogTag, e.getMessage(), e);
			}
		}

	}

	private void setNotification(String message) {
		Notification notification = new Notification(R.drawable.ic_launcher,
				getString(R.string.recording), System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				message, contentIntent);
		mNotificationManager.notify(Const.RECORDING_NOTIFICATION_ID,
				notification);
	}

	private void archiveData() {
		if (mLocationsToSave.isEmpty()) {
			synchronized (mRecorderLocations) {
				mLocationsToSave.addAll(mRecorderLocations);
				mRecorderLocations.clear();
			}
		}

		if (!mLocationsToSave.isEmpty()) {
			try {
				saveLocations();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	private void saveLocations() throws IOException {
		Route r = new Route(mRouteName);
		r.getPoints().addAll(mSavedRoute.getPoints());
		r.getPoints().addAll(mLocationsToSave);
		long latestUpdate = (long) (System.currentTimeMillis() / 1E3);
		r.setLatestUpdate(latestUpdate);
		r.save(this, Helper.getRouteFile(mRouteName));

		mSavedRoute = r;
		saveFileToSend(latestUpdate);

		mLocationsToSave.clear();

	}
	private void saveFileToSend(long latestUpdate) {
		File file;
		do {
			file = getFileStreamPath(Helper
					.getFileToSend(++latestFileToSendIndex));
		} while (file.exists());

		try {
			Route route = new Route(mRouteName);
			route.setLatestUpdate(latestUpdate);
			route.getPoints().addAll(mLocationsToSave);
			route.save(this, file.getName());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onCreate() {
		mBinder = new RecordRouteBinder();
		Toast.makeText(this, R.string.recording_started, Toast.LENGTH_SHORT)
				.show();
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				switch (status) {
					case LocationProvider.AVAILABLE :
						setNotification(getString(R.string.gps_available));
						break;
					case LocationProvider.OUT_OF_SERVICE :
						setNotification(getString(R.string.gps_out_of_service));
						break;
					case LocationProvider.TEMPORARILY_UNAVAILABLE :
						setNotification(getString(R.string.gps_temporarily_unavailable));
						break;
				}
			}

			public void onProviderEnabled(String provider) {
				setNotification(getString(R.string.gps_enabled));
			}

			public void onProviderDisabled(String provider) {
				setNotification(getString(R.string.gps_disabled));
			}

			public void onLocationChanged(Location location) {
				synchronized (mRecorderLocations) {
					mRecorderLocations.add(new RoutePoint(getPoints(),
							(int) (location.getLatitude() * 1E6),
							(int) (location.getLongitude() * 1E6), location
									.getAltitude(), (long) (System
									.currentTimeMillis() / 1E3)));
					setNotification(getString(R.string.recording_details,
							mRouteName, getPoints()));
				}
			}

		};
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				6000/* 6 secondi */, 1/* un metro */, mLocationListener);

		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	@Override
	public void onDestroy() {
		working = false;
		mlocManager.removeUpdates(mLocationListener);
		Toast.makeText(this, R.string.recording_stopped, Toast.LENGTH_SHORT)
				.show();
		mNotificationManager.cancel(Const.RECORDING_NOTIFICATION_ID);
		super.onDestroy();
	}

	class RecordRouteBinder extends Binder {
		RecordRouteService getService() {
			return RecordRouteService.this;
		}
	}

	public Route getSavedRoute() {
		return mSavedRoute;
	}
}
