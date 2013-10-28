package com.ecommuters;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.content.Intent;
import android.webkit.CookieSyncManager;

public class MyApplication extends Application {

	private static MyApplication sInstance;

	private ArrayList<Route> mRoutes;
	private RecordRouteService recordingService;
	private ConnectorService connectorService;
	public Event OnRecordingRouteUpdated = new Event();
	public Event RouteChanged = new Event();
	public Event ConnectorServiceChanged = new Event();
	private Object routeSemaphore = new Object();

	@Override
	public void onCreate() {
		super.onCreate();
		
		sInstance = this;
		CookieSyncManager.createInstance(this);
		//controllo se devo mandare degli itinerari al server
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
		Route[] list;
		boolean routeChanged = false;
		synchronized (routeSemaphore) {
			if (mRoutes == null) {
				mRoutes = Route.readAllRoutes(getApplicationContext());
				routeChanged = true;
			}
			list = new Route[mRoutes.size()];
			mRoutes.toArray(list);

		}
		if (routeChanged)
			OnRouteChanged();
		return list;

	}

	private void OnRouteChanged() {
		RouteChanged.fire(this, EventArgs.Empty);
	}

	public void refreshRoutes(boolean schedule) {
		synchronized (routeSemaphore) {
			mRoutes = Route.readAllRoutes(getApplicationContext());
		}
		OnRouteChanged();
		if (schedule)
			new TaskScheduler().scheduleLiveTracking();
	}

	public void deleteRoute(Route route) {
		synchronized (routeSemaphore) {
			String routeFile = Helper.getRouteFile(route.getName());
			final File file = getFileStreamPath(routeFile);
			file.delete();
			mRoutes.remove(route);
		}
		new TaskScheduler().scheduleLiveTracking();
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
	}

	public void setConnectorService(ConnectorService connectorService) {
		this.connectorService = connectorService;
		ConnectorServiceChanged.fire(this, EventArgs.Empty);

	}

	public ConnectorService getConnectorService() {
		return this.connectorService;

	}

}