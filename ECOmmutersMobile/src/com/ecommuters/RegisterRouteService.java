package com.ecommuters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class RegisterRouteService extends IntentService {

	boolean working = true;
	private LocationManager mlocManager;
	private LocationListener mLocationListener;
	private List<Location> mRecorderLocations = new ArrayList<Location>();
	private List<Location> mLocationsToSend = new ArrayList<Location>();
	private List<Location> mSentLocations = new ArrayList<Location>();

	public RegisterRouteService() {
		super("RegisterRouteService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		while (working) {
			try {
				Thread.sleep(5000);
				sendToServer();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void sendToServer() {
		if (!Helper.isOnline(this))
			return;
		if (mLocationsToSend.isEmpty()) {
			synchronized (mRecorderLocations) {
				mLocationsToSend.addAll(mRecorderLocations);
				mRecorderLocations.clear();
			}
		}
		
		if (!mLocationsToSend.isEmpty())
		{
			saveLocations();
		}
	}

	private void saveLocations() {
		//TODO save
		mSentLocations.addAll(mLocationsToSend);
		mLocationsToSend.clear();
		
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "Started recording", Toast.LENGTH_SHORT).show();
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
					mRecorderLocations.add(location);
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
		Toast.makeText(this, "Stopped recording", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
}
