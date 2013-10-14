package com.ecommuters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ecommuters.Task.EventType;

public class TaskScheduler {

	// disposizione del
	// gps per
	// connettersi e
	// trovare la
	// posizione

	private List<Task> mTasks = new ArrayList<Task>();
	// public LiveTrackingEvent LiveTrackingEvent = new LiveTrackingEvent();
	// public FollowedRouteEvent FollowedRoutesChanged = new
	// FollowedRouteEvent();
	private Route[] routes;
	private int id;

	public TaskScheduler() {

	}

	public void scheduleLiveTracking() {
		for (Route r : routes) {
			for (TimeInterval interval : r.getIntervals()) {
				Task startingTask = schedule(interval.getStart(),
						interval.getWeight(), EventType.START_TRACKING);

				schedule(interval.getEnd(), interval.getWeight(),
						EventType.STOP_TRACKING);

				if (interval.isActiveNow()) {
					startingTask.execute();
				}
			}
		}
	}

	void clearSchedules() {
		if (routes != null)
			for (Route r : routes)
				r.latestIndex = 0;
		for (Task t : mTasks)
			t.cancel();
		mTasks.clear();
		id = 0;
		// // poi disabilito il GPS
		// mService.resetGPS();

	}

	private Task schedule(Date time, int weight, EventType type) {
		Task task = new Task(time, type, weight, id++);
		task.activate();
		mTasks.add(task);
		return task;
	}

	public Route[] getRoutes() {
		return routes;
	}

	public void setRoutes(Route[] routes) {
		this.routes = routes;
	}

}
