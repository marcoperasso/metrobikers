package com.ecommuters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RegisterRouteService extends IntentService {

	boolean working = true;
	String routeName;
	private LocationManager mlocManager;
	private LocationListener mLocationListener;

	// acceduto da diversi thread: va sincronizzato
	private List<GpsPoint> mRecorderLocations = new ArrayList<GpsPoint>();

	// liste accedute dal solo worker thread del servizio
	private List<GpsPoint> mLocationsToSave = new ArrayList<GpsPoint>();
	private List<GpsPoint> mSavedLocations = new ArrayList<GpsPoint>();

	public RegisterRouteService() {
		super("RegisterRouteService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		routeName = intent.getExtras().getString(Const.ROUTE_NAME);

		String routeFile = Helper.getRouteFile(routeName);
		File file = getFileStreamPath(routeFile);
		if (file.exists()) {
			try {
				FileInputStream fis = openFileInput(routeFile);
				ObjectInput in = null;
				try {
					in = new ObjectInputStream(fis);
					while (fis.available() > 0) {
						try {
							GpsPoint pt = (GpsPoint) in.readObject();
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
		while (working) {
			try {
				Thread.sleep(5000);
				archiveData();

			} catch (InterruptedException e) {
				Log.e(Const.LogTag, e.getMessage(), e);
			}
		}

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
		FileOutputStream fos = openFileOutput(Helper.getRouteFile(routeName), Context.MODE_PRIVATE);
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(fos);
			for (GpsPoint l : mSavedLocations)
				out.writeObject(l);
			for (GpsPoint l : mLocationsToSave)
				out.writeObject(l);
			out.flush();
		} finally {
			out.close();
			fos.close();
		}

		mSavedLocations.addAll(mLocationsToSave);
		mLocationsToSave.clear();

	}

	@Override
	public void onCreate() {

		Toast.makeText(this, R.string.recording_started, Toast.LENGTH_SHORT)
				.show();
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			public void onProviderEnabled(String provider) {

			}

			public void onProviderDisabled(String provider) {

			}

			public void onLocationChanged(Location location) {
				synchronized (mRecorderLocations) {
					mRecorderLocations.add(new GpsPoint((int) (location
							.getLatitude() * 1E6), (int) (location
							.getLongitude() * 1E6), location.getAltitude(),
							new Date()));
				}
			}
		};
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mLocationListener);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		working = false;
		mlocManager.removeUpdates(mLocationListener);
		Toast.makeText(this, R.string.recording_stopped, Toast.LENGTH_SHORT)
				.show();
		super.onDestroy();
	}
}
