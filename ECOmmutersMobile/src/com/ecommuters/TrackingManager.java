package com.ecommuters;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

import com.ecommuters.Task.EventType;

public class TrackingManager {

	private static final int TIMEOUT = 600000;

	private static final int distanceMeters = 2;

	private static final int MAX_OUT_OF_TRACK_COUNT = 20;
	private Hashtable<String, Integer> mIndexMap = new Hashtable<String, Integer>();
	// disposizione del
	// gps per
	// connettersi e
	// trovare la
	// posizione

	private Handler mHandler;
	private List<Task> mTasks = new ArrayList<Task>();
	List<Route> mRoutesInTimeInterval = new ArrayList<Route>();
	private ConnectorService mService;
	private boolean liveTracking;
	public LiveTrackingEvent LiveTrackingEvent = new LiveTrackingEvent();
	private Timer mTimer = new Timer(true);

	private TimerTask timerTask = null;

	private int outOfTrackCount;

	public TrackingManager(Handler handler, ConnectorService service) {
		mHandler = handler;
		mService = service;
	}

	public void locationChanged(ECommuterPosition mLocation) {
		List<Route> trackingRoutes = getRoutesByPosition(mLocation);

		boolean b = trackingRoutes.size() > 0;
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		if (b) {
			timerTask = new TimerTask() {

				@Override
				public void run() {
					setLiveTracking(false);
				}
			};
			mTimer.schedule(timerTask, TIMEOUT);
		}
		if (b == liveTracking)
			return;
		// se non sono più sulla traccia, non mi metto subito fuori dal live
		// tracking, aspetto un po',
		// magari ci rientro... dopo MAX_OUT_OF_TRACK_COUNT che entro qui
		// dentro, allora mi considero fuori traccia
		if (!b) {
			outOfTrackCount++;
			if (outOfTrackCount < MAX_OUT_OF_TRACK_COUNT)
				return;
		}
		setLiveTracking(b);
	}

	private void setLiveTracking(boolean b) {
		outOfTrackCount = 0;
		liveTracking = b;
		LiveTrackingEvent.fire(this, new LiveTrackingEventArgs(liveTracking));
	}

	public boolean isLiveTracking() {
		return liveTracking;
	}

	private List<Route> getRoutesByPosition(ECommuterPosition position) {
		List<Route> trackingRoutes = new ArrayList<Route>();
		resetIndexMap(mRoutesInTimeInterval);
		for (Route r : mRoutesInTimeInterval) {
			boolean tracked = false;
			for (int i = getStartingIndex(r); i < r.getPoints().size(); i++) {
				RoutePoint pt = r.getPoints().get(i);
				if (position.distance(pt) < distanceMeters)// due metri
				{
					trackingRoutes.add(r);
					addToIndexMap(r, i);
					tracked = true;
					break;
				}
			}
			if (!tracked)
				removeFromIndexMap(r);
		}
		return trackingRoutes;

	}

	private void resetIndexMap(List<Route> trackingRoutesByTime) {
		for (String name : mIndexMap.keySet()) {
			if (!contains(trackingRoutesByTime, name))
				mIndexMap.put(name, 0);
		}
	}

	private boolean contains(List<Route> trackingRoutesByTime, String name) {
		for (Route r : trackingRoutesByTime)
			if (r.getName().equals(name))
				return true;
		return false;
	}

	private void removeFromIndexMap(Route r) {
		mIndexMap.remove(r.getName());
	}

	private void addToIndexMap(Route r, int i) {
		mIndexMap.put(r.getName(), i);
	}

	private int getStartingIndex(Route r) {
		Integer index = mIndexMap.get(r.getName());
		return index == null ? 0 : index;
	}

	public void scheduleLiveTracking() {
		clearSchedules();

		ArrayList<TimeInterval> intervals = getLiveTrackingTimeIntervals();
		for (TimeInterval interval : intervals) {

			Task startingTask = schedule(interval.getStart(),
					interval.getRoute(), interval.getWeight(), EventType.START_TRACKING);

			schedule(interval.getEnd(), interval.getRoute(),
					interval.getWeight(),EventType.STOP_TRACKING);

			if (interval.isActiveNow()) {
				startingTask.execute();
			}
		}

	}

	private void clearSchedules() {
		mRoutesInTimeInterval.clear();
		for (Task t : mTasks) {
			mHandler.removeCallbacks(t);

		}
		mTasks.clear();
	}

	private Task schedule(Date time, Route route, int weight, EventType type) {
		Task task = new Task(this, mHandler, time, type, weight, route);
		task.activate();
		mTasks.add(task);
		return task;
	}

	private ArrayList<TimeInterval> getLiveTrackingTimeIntervals() {
		ArrayList<TimeInterval> intervals = new ArrayList<TimeInterval>();
		for (Route r : MyApplication.getInstance().getRoutes()) {
			intervals.add(new TimeInterval(r,
					(long) (r.getPoints().get(0).time * 1e3), 0));
		}
		return intervals;

	}

	public void OnExecuteTask(Task task) {
		switch (task.getType()) {
		case START_TRACKING:
			if (!mRoutesInTimeInterval.contains(task.getRoute()))
				mRoutesInTimeInterval.add(task.getRoute());
			break;
		case STOP_TRACKING:
			if (mRoutesInTimeInterval.contains(task.getRoute()))
				mRoutesInTimeInterval.remove(task.getRoute());
			break;
		default:
			break;
		}
		mService.OnExecuteTask(task);
	}
}
