package com.ecommuters;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class ConnectorService extends IntentService {

	private boolean working = false;
	private LocationManager mlocManager;
	private LocationListener mLocationListener;
	private RoutePoint routePoint;

	public ConnectorService() {
		super("ConnectorService");
	}
	@Override
	public void onCreate() {
		working = true;
		
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener() {

			
			public void onLocationChanged(Location location) {
				routePoint = new RoutePoint(0, (int) (location
						.getLatitude() * 1E6), (int) (location
						.getLongitude() * 1E6), location
						.getAltitude(), (long) (System
						.currentTimeMillis() / 1E3));
				try {
					RequestBuilder.sendPositionData(routePoint);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}};
		//mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		//		15000/* 15 secondi */, 2/* due metri */, mLocationListener);
		
		MyApplication.getInstance().setConnectorService(this);
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		working = false;
		MyApplication.getInstance().setConnectorService(null);
		//mlocManager.removeUpdates(mLocationListener);
		super.onDestroy();
	}
	protected void onHandleIntent(Intent intent) {
		while (working ) {
			try {
				if (Helper.isOnline(this)) {
					sendLocations();
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		if (MyApplication.getInstance().isSendingData())
			return;
		MyApplication.getInstance().setSendingData(true);
		final List<File> files = Helper.getFiles(this, Const.TOSENDEXT);

		if (files.size() == 0) {
			MyApplication.getInstance().setSendingData(false);
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
							// TODO refresh dei soli frammenti da mandare
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} finally {

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
}
