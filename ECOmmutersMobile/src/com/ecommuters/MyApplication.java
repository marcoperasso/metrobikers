package com.ecommuters;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;

public class MyApplication extends Application {

	private static MyApplication sInstance;

	private ArrayList<Route> mRoutes;
	public EventHandler RouteChanged = new EventHandler();

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		mRoutes = Route.readAllRoutes(getApplicationContext());
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
		synchronized (mRoutes) {
			Route[] list = new Route[mRoutes.size()];
			mRoutes.toArray(list);
			return list;
		}
		
	}
	public void refreshRoutes() {
		mRoutes = Route.readAllRoutes(getApplicationContext());
		RouteChanged.fire(this, EventArgs.Empty);
	}
	public void removeRoute(Route route) {
		synchronized (mRoutes) {
			String routeFile = Helper.getRouteFile(route.getName());
			final File file = getFileStreamPath(routeFile);
			file.delete();
			mRoutes.remove(route);
			for (File f : Helper.getRoutePacketFiles(this, route.getName()))
				f.delete();
		}
		RouteChanged.fire(this, EventArgs.Empty);

	}
}