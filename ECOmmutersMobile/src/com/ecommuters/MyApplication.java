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
	public Event LiveTrackingChanged = new Event();
	public Object routeSemaphore = new Object();
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
			if (mRoutes == null)
			{
				mRoutes = Route.readAllRoutes(getApplicationContext());
				routeChanged = true;
			}
			list = new Route[mRoutes.size()];
			mRoutes.toArray(list);
			
		}
		if (routeChanged)
			RouteChanged.fire(this, EventArgs.Empty);
		return list;

	}
	public void refreshRoutes() {
		synchronized (routeSemaphore) {
			mRoutes = Route.readAllRoutes(getApplicationContext());
		}
		RouteChanged.fire(this, EventArgs.Empty);

	}
	public void removeRoute(Route route) {
		synchronized (routeSemaphore) {
			String routeFile = Helper.getRouteFile(route.getName());
			final File file = getFileStreamPath(routeFile);
			file.delete();
			mRoutes.remove(route);
		}
		RouteChanged.fire(this, EventArgs.Empty);

	}
	public void activateConnector(Context context) {
		if (connectorActivated)
			return;
		Calendar cal = Calendar.getInstance();

		Intent myIntent = new Intent(context, ConnectorService.class);
		PendingIntent pintent = PendingIntent.getService(context, 0, myIntent,
				0);

		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
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
		LiveTrackingChanged.fire(this, EventArgs.Empty);
	}
}