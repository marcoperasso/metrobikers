package com.ecommuters;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MyApplication extends Application {

	private static MyApplication sInstance;

	private ArrayList<Route> mRoutes;
	private boolean connectorActivated;
	private RecordRouteService recordingService;
	private ConnectorService connectorService;
	private boolean liveTracking = false;
	public Event OnRecordingRouteUpdated = new Event();
	public Event RouteChanged = new Event();
	public LiveTrackingEvent ManualLiveTrackingChanged = new LiveTrackingEvent();
	private Object routeSemaphore = new Object();
	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;

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

	public void refreshRoutes() {
		synchronized (routeSemaphore) {
			mRoutes = Route.readAllRoutes(getApplicationContext());
		}
		OnRouteChanged();

	}
	public void removeRoute(Route route) {
		synchronized (routeSemaphore) {
			String routeFile = Helper.getRouteFile(route.getName());
			final File file = getFileStreamPath(routeFile);
			file.delete();
			mRoutes.remove(route);
		}
		OnRouteChanged();

	}
	public void activateConnector() {
		if (connectorActivated)
			return;
		Calendar cal = Calendar.getInstance();

		Intent myIntent = new Intent(this, ConnectorService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, myIntent, 0);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// Start every 30 seconds
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				30 * 1000, pintent);
		connectorActivated = true;
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

	}
	public ConnectorService getConnectorService() {
		return this.connectorService;

	}
	public Boolean isLiveTracking() {
		return liveTracking;
	}

	public void setLiveTracking(boolean b) {
		liveTracking = b;
		ManualLiveTrackingChanged.fire(this, new LiveTrackingEventArgs(liveTracking));
	}
}