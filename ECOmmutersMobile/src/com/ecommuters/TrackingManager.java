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
	private Hashtable<Route, Integer> mIndexMap = new Hashtable<Route, Integer>();
	// disposizione del
	// gps per
	// connettersi e
	// trovare la
	// posizione

	private Handler mHandler;
	private List<Task> mTasks = new ArrayList<Task>();
	Hashtable<Route, Integer> mRoutesInTimeInterval = new Hashtable<Route, Integer>();
	private ConnectorService mService;
	private boolean liveTracking;
	public LiveTrackingEvent LiveTrackingEvent = new LiveTrackingEvent();
	private Timer mTimer = new Timer(true);

	private TimerTask timerTask = null;

	private int outOfTrackCount;

	private List<Route> trackingRoutes = new ArrayList<Route>();;

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
		trackingRoutes.clear();
		resetIndexMap(mRoutesInTimeInterval);
		for (Route r : mRoutesInTimeInterval.keySet()) {
			boolean tracked = false;
			for (int i = getStartingIndex(r); i < r.getPoints().size(); i++) {
				RoutePoint pt = r.getPoints().get(i);
				if (position.distance(pt) < distanceMeters)// due metri
				{
					trackingRoutes.add(r);
					mIndexMap.put(r, i);
					tracked = true;
					break;
				}
			}
			if (!tracked)
				mIndexMap.remove(r);
		}
		return trackingRoutes;

	}

	private void resetIndexMap(Hashtable<Route, Integer> routesInTimeInterval) {
		for (Route r : mIndexMap.keySet()) {
			Integer integer = routesInTimeInterval.get(r);
			if (integer == 0 || integer == null)
				mIndexMap.put(r, 0);
		}
	}

	private int getStartingIndex(Route r) {
		Integer index = mIndexMap.get(r);
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
			
			long currentTimeMillis = System.currentTimeMillis();
			intervals.add(new TimeInterval(r, currentTimeMillis, 0));
			intervals.add(new TimeInterval(r, currentTimeMillis, 1));
			intervals.add(new TimeInterval(r, currentTimeMillis, 2));
		}
		
		
		return intervals;

	}

	public void OnExecuteTask(Task task) {
		Integer i = mRoutesInTimeInterval.get(task.getRoute());
		if (i == null)
			i = 0;
		switch (task.getType()) {
		case START_TRACKING:
				i++;
			break;
		case STOP_TRACKING:
				i--;
				assert(i>=0);
			break;
		default:
			break;
		}
		if (i == 0)
			mRoutesInTimeInterval.remove(task.getRoute());
		else
			mRoutesInTimeInterval.put(task.getRoute(), i);
		mService.OnExecuteTask(task);
	}
}
