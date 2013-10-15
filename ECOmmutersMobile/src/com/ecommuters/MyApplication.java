package com.ecommuters;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.webkit.CookieSyncManager;

public class MyApplication extends Application {

	
	private static MyApplication sInstance;

	private ArrayList<Route> mRoutes;
	private RecordRouteService recordingService;
	private ConnectorService connectorService;
	public Event OnRecordingRouteUpdated = new Event();
	public Event RouteChanged = new Event();
	private Object routeSemaphore = new Object();
	@Override
	public void onCreate() {
		super.onCreate();
		//final UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		//Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
		//	public void uncaughtException(Thread thread, Throwable ex)  {
		//		Log.e(Const.ECOMMUTERS_TAG, ex.toString());
		//		for (StackTraceElement el : ex.getStackTrace())
		//			Log.e(Const.ECOMMUTERS_TAG, el.toString());
		//		defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
				
		//	}
		//});
		sInstance = this; 
		CookieSyncManager.createInstance (this);
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

	}
	public ConnectorService getConnectorService() {
		return this.connectorService;

	}
	
	
}