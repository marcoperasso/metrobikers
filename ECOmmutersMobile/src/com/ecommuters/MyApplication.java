package com.ecommuters;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.ecommuters.LiveTrackingReceiver.EventType;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
	private Object routeSemaphore = new Object();
	private int intentCode = 0;
	
	private ArrayList<PendingIntent> mPendingIntents = new ArrayList<PendingIntent>();
	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;

	}
	@Override
	public void onTerminate() {
		sInstance = null;
		clearPendingIntents();
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
		scheduleLiveTracking();
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
	private void scheduleLiveTracking() {
		clearPendingIntents();
		
		//debug();
		
		ArrayList<TimeInterval> intervals = getLiveTrackingTimeIntervals();
		for (TimeInterval interval : intervals) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, interval.getStart().getHours());
			calendar.set(Calendar.MINUTE, interval.getStart().getMinutes());
			calendar.set(Calendar.SECOND, interval.getStart().getSeconds());

			schedule(calendar, interval.getRouteName(), EventType.START_TRACKING);

			calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, interval.getEnd().getHours());
			calendar.set(Calendar.MINUTE, interval.getEnd().getMinutes());
			calendar.set(Calendar.SECOND, interval.getEnd().getSeconds());

			schedule(calendar, interval.getRouteName(), EventType.STOP_TRACKING);

			if (interval.isActiveNow()) {
				Intent intent = getIntent(interval.getRouteName(), EventType.START_TRACKING);
				
				sendBroadcast(intent);
			}
		}

	}
	private void debug() {
		Calendar qq = Calendar.getInstance();
		Date now = new Date();
		qq.set(Calendar.HOUR_OF_DAY, now.getHours());
		qq.set(Calendar.MINUTE, now.getMinutes());
		qq.set(Calendar.SECOND, now.getSeconds()+30);

		schedule(qq, "a", EventType.START_TRACKING);
		qq.add(Calendar.SECOND, 30);
		schedule(qq, "a", EventType.STOP_TRACKING);
	}
	private void clearPendingIntents() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		for (PendingIntent pi : mPendingIntents) {
			am.cancel(pi);
		}
		mPendingIntents.clear();
	}
	private void schedule(Calendar calendar, String routeName, EventType id) {
		PendingIntent pi = PendingIntent.getBroadcast(this, ++intentCode, getIntent(routeName, id),
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pi);
		mPendingIntents.add(pi);
		Log.d("SCHEDULER", "Scheduling " +id.toString() + " event for " + calendar.getTime().toLocaleString());
	}
	private Intent getIntent(String routeName, EventType id) {
		Intent intent =  new Intent(
				this, LiveTrackingReceiver.class);
		intent.putExtra(LiveTrackingReceiver.ID, id);
		intent.putExtra(LiveTrackingReceiver.ROUTENAME, routeName);
		return intent;
	}
	private ArrayList<TimeInterval> getLiveTrackingTimeIntervals() {
		ArrayList<TimeInterval> intervals = new ArrayList<TimeInterval>();
		synchronized (routeSemaphore) {
			for (Route r : mRoutes) {
				intervals.add(new TimeInterval(r, (long) (r.getPoints().get(0).time * 1e3)));
			}
		}
		return intervals;

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