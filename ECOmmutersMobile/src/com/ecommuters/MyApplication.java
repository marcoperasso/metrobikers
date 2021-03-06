package com.ecommuters;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;


public class MyApplication extends Application {

	public static final boolean LogEnabled = false;

	private static MyApplication sInstance;

	private ArrayList<Route> mRoutes;
	private RecordRouteService recordingService;
	private ConnectorService connectorService;
	public Event OnRecordingRouteUpdated = new Event();
	public Event RouteChanged = new Event();
	public Event ConnectorServiceChanged = new Event();
	public Event RecordingServiceChanged = new Event();
	private Object routeSemaphore = new Object();

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		
		// controllo se devo mandare degli itinerari al server
		Intent service = new Intent(this, SyncService.class);
		this.startService(service);
	}

	@Override
	public void onTerminate() {
		sInstance = null;
		super.onTerminate();
	}

	public static MyApplication getInstance() {
		return sInstance;
	}

	Route[] getRoutes() {
		initRoutes();
		synchronized (routeSemaphore) {
			Route[] list = new Route[mRoutes.size()];
			mRoutes.toArray(list);
			return list;
		}
	}

	private void initRoutes() {
		boolean routeChanged = false;
		synchronized (routeSemaphore) {
			if (mRoutes == null) {
				mRoutes = Route.readAllRoutes(this);
				routeChanged = true;
			}
		}
		if (routeChanged)
			OnRouteChanged();
	}

	private void OnRouteChanged() {
		RouteChanged.fire(this, EventArgs.Empty);
	}

	public void refreshRoutes() {
		ConnectorService.resetGPSStatus();
		synchronized (routeSemaphore) {
			if (mRoutes != null) {
				for (Route r : mRoutes)
					r.cancelScheduling();
			}
			mRoutes = Route.readAllRoutes(getApplicationContext());
			if (mRoutes != null) {
				for (Route r : mRoutes)
					r.schedule(true);
			}
		}
		OnRouteChanged();
	}

	public void deleteRoute(Route route) {
		synchronized (routeSemaphore) {
			String routeFile = Helper.getRouteFile(route.getName());
			final File file = getFileStreamPath(routeFile);
			file.delete();
			mRoutes.remove(route);
		}
		route.cancelScheduling();
		OnRouteChanged();

	}

	public void addRoute(Route route, boolean executeIfActiveNow) {
		initRoutes();
		synchronized (routeSemaphore) {
			boolean updated = false;
			for (int i = 0; i < mRoutes.size(); i++) {
				if (mRoutes.get(i).getName().equalsIgnoreCase(route.getName())) {
					mRoutes.set(i, route);
					updated = true;
					break;
				}
			}
			if (!updated)
				mRoutes.add(route);
		}
		route.schedule(executeIfActiveNow);
		OnRouteChanged();

	}

	public boolean isRecording() {
		return recordingService != null;
	}

	public RecordRouteService getRecordingService() {
		return recordingService;
	}

	public void setRecordingService(RecordRouteService recordingService) {
		this.recordingService = recordingService;
		RecordingServiceChanged.fire(this, EventArgs.Empty);
	}

	public void setConnectorService(ConnectorService connectorService) {
		this.connectorService = connectorService;
		ConnectorServiceChanged.fire(this, EventArgs.Empty);

	}

	public ConnectorService getConnectorService() {
		return this.connectorService;

	}

	public void requestRoutes(final Activity activity,
			final OnRoutesAvailable onRoutesAvailable) {
		new Thread(new Runnable() {

			public void run() {
				final Route[] routes = getRoutes();
				activity.runOnUiThread(new Runnable() {

					public void run() {
						onRoutesAvailable.gotRoutes(routes);

					}
				});

			}
		}).start();

	}

}