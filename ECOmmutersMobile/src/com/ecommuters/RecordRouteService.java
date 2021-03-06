package com.ecommuters;

import java.io.IOException;

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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class RecordRouteService extends IntentService {

	boolean working = true;
	private LocationManager mlocManager;
	private LocationListener mLocationListener;

	private Route mRoute;
	private boolean routeModified = false;
	private NotificationManager mNotificationManager;
	
	public RecordRouteService() {
		super("RegisterRouteService");
	}

	private int getPoints() {
		synchronized (mRoute) {
			return mRoute.getPoints().size();
		}
	}
	boolean isWorking() {
		return working;
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		setNotification(getString(R.string.recording_detail, getPoints()));
		while (working) {
			try {
				Thread.sleep(5000);
				if (routeModified)
					archiveData();

			} catch (Exception e) {
				Log.e(Const.ECOMMUTERS_TAG, Log.getStackTraceString(e)); 
			} 
		}

	}

	private void setNotification(String message) {
		if (!working)
			return;
		
		Intent intent = new Intent(this, MyMapActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
		.setSmallIcon(R.drawable.record)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(message)
				.setContentIntent(contentIntent);

		Notification notification = mBuilder.build();
		mNotificationManager.notify(Const.RECORDING_NOTIFICATION_ID,
				notification);
	}

	private void archiveData() throws IOException {
		synchronized (mRoute) {
			long latestUpdate = Helper.getCurrentUnixTime();
			mRoute.setLatestUpdate(latestUpdate);
			String routeFile = Const.RECORDING_ROUTE_FILE;
			mRoute.save(this, routeFile);
			routeModified = false;
		}

	}

	@Override
	public void onCreate() {
		mRoute = Route.readRoute(this, Const.RECORDING_ROUTE_FILE);
		if (mRoute == null)
			mRoute = new Route(Const.DEFAULT_ROUTE_NAME);
		MyApplication.getInstance().setRecordingService(this);
		Toast.makeText(this, R.string.recording_started, Toast.LENGTH_LONG)
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
				synchronized (mRoute) {
					long unixTime = Helper.getCurrentUnixTime();
					if (mRoute.getPoints().size() > 0)
					{
						RoutePoint lastPoint = mRoute.getPoints().get(mRoute.getPoints().size() - 1);
						if (lastPoint.time == unixTime)
							return;
					}
					mRoute.getPoints().add(
							new RoutePoint((int) (location
									.getLatitude() * 1E6), (int) (location
									.getLongitude() * 1E6), 
									unixTime));
					routeModified = true;

				}
				setNotification(getString(R.string.recording_detail,
						getPoints()));
				MyApplication.getInstance().OnRecordingRouteUpdated.fire(this,
						EventArgs.Empty);
			}

		};
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				6000/* 6 secondi */, 2/* due metri */, mLocationListener);

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		working = false;
		mlocManager.removeUpdates(mLocationListener);
		Toast.makeText(this, R.string.recording_stopped, Toast.LENGTH_SHORT)
				.show();
		mNotificationManager.cancel(Const.RECORDING_NOTIFICATION_ID);
		MyApplication.getInstance().setRecordingService(null);

		super.onDestroy();
	}

	public Route getRoute() {
		return mRoute;
	}

}
