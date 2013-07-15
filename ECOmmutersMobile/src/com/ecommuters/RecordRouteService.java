package com.ecommuters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RecordRouteService extends IntentService {

	boolean working = true;
	private LocationManager mlocManager;
	private LocationListener mLocationListener;

	// acceduto da diversi thread: va sincronizzato
	// sono i punti registrati ma non ancora salvati
	private List<RegisteredPoint> mRecorderLocations = new ArrayList<RegisteredPoint>();

	// liste accedute dal solo worker thread del servizio, non vanno
	// sincronizzate
	// lista dei punti da salvare
	private List<RegisteredPoint> mLocationsToSave = new ArrayList<RegisteredPoint>();
	// lista dei punti salvati
	private List<RegisteredPoint> mSavedLocations = new ArrayList<RegisteredPoint>();
	private String mRouteName;
	private int latestFileToSendIndex;
	private NotificationManager mNotificationManager;

	public RecordRouteService() {
		super("RegisterRouteService");
	}

	private int getPoints() {
		return mRecorderLocations.size() + mLocationsToSave.size()
				+ mSavedLocations.size();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mRouteName = intent.getExtras().getString(Const.ROUTE_NAME);
		
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		String routeFile = Helper.getRouteFile(mRouteName);
		File file = getFileStreamPath(routeFile);
		if (file.exists()) {
			try {
				FileInputStream fis = openFileInput(routeFile);
				ObjectInput in = null;
				try {
					in = new ObjectInputStream(fis);
					while (fis.available() > 0) {
						try {
							RegisteredPoint pt = (RegisteredPoint) in
									.readObject();
							mSavedLocations.add(pt);
						} catch (Exception ex) {
							Log.e("ec", ex.getMessage(), ex);
							break;
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					in.close();
					fis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		setNotification(getString(R.string.recording_details, mRouteName, getPoints()));
		
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
		Notification notification = new Notification(R.drawable.ic_launcher, getString(R.string.recording), System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		Intent intent = new Intent(this, ManageServiceActivity.class);
		intent.putExtra(Const.ServiceMessage, message);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT );
		notification.setLatestEventInfo(this, getString(R.string.ecommuters), message, contentIntent);
		mNotificationManager.notify(Const.RECORDING_NOTIFICATION_ID, notification);
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
		FileOutputStream fos = openFileOutput(Helper.getRouteFile(mRouteName),
				Context.MODE_PRIVATE);
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(fos);
			for (RegisteredPoint l : mSavedLocations)
				out.writeObject(l);
			for (RegisteredPoint l : mLocationsToSave)
				out.writeObject(l);
			out.flush();
		} finally {
			out.close();
			fos.close();
		}

		mSavedLocations.addAll(mLocationsToSave);
		saveFileToSend();

		mLocationsToSave.clear();

	}
	private void saveFileToSend() {
		File file;
		do {
			file = getFileStreamPath(Helper
					.getFileToSend(++latestFileToSendIndex));
		} while (file.exists());

		try {
			RegisteredRoute route = new RegisteredRoute(mRouteName);
			route.getPoints().addAll(mLocationsToSave);

			FileOutputStream fos = openFileOutput(file.getName(),
					Context.MODE_PRIVATE);
			ObjectOutput out = null;
			try {
				out = new ObjectOutputStream(fos);
				out.writeObject(route);
				out.flush();
			} finally {
				out.close();
				fos.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onCreate() {

		Toast.makeText(this, R.string.recording_started, Toast.LENGTH_SHORT)
				.show();
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				switch (status) {
		            case LocationProvider.AVAILABLE:
		            	setNotification(getString(R.string.gps_available));
		                break;
		            case LocationProvider.OUT_OF_SERVICE:
		            	setNotification(getString(R.string.gps_out_of_service));
		                break;
		            case LocationProvider.TEMPORARILY_UNAVAILABLE:
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
					mRecorderLocations.add(new RegisteredPoint(getPoints(),
							(int) (location.getLatitude() * 1E6),
							(int) (location.getLongitude() * 1E6), location
									.getAltitude(), (long) (System
									.currentTimeMillis() / 1E3)));
					setNotification(getString(R.string.recording_details, mRouteName, getPoints()));
				}
			}

		};
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				6000/* 6 secondi */, 1/* un metro */, mLocationListener);

			
		super.onCreate();
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
}
