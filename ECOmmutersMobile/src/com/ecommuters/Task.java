package com.ecommuters;

import java.util.Calendar;
import java.util.Date;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class Task implements Runnable {
	public enum EventType {
		START_TRACKING, STOP_TRACKING
	}

	private Handler mHandler;
	private Date time;
	private Route mRouteName;
	private TrackingManager mTrackingManager;
	private EventType type;
	private int weight;
	public Task(TrackingManager manager, Handler handler, Date time, EventType type, int weight, Route route) {
		this.type = type;
		this.weight = weight;
		this.mTrackingManager = manager;
		this.mHandler = handler;
		this.time = time;
		this.mRouteName = route;
	}

	public void run() {
		
		execute();
		activate();

	}

	public void execute() {
		mTrackingManager.OnExecuteTask(this);
		
	}

	public void activate() {
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
		calendar.set(Calendar.MINUTE, time.getMinutes());
		calendar.set(Calendar.SECOND, time.getSeconds());
		Date next = calendar.getTime();
		if (next.getTime() < now.getTime())
		{
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			next = calendar.getTime();
		}
		long delta = System.currentTimeMillis()-SystemClock.uptimeMillis();
		mHandler.postAtTime(this, next.getTime() - delta);
		
		Log.d("SCHEDULER", mRouteName + " - " + next.toLocaleString());
		
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Route getRoute() {
		return mRouteName;
	}

	public void setRoute(Route route) {
		this.mRouteName = route;
	}

	public int getWeight() {
		return weight;
	}

	

}
