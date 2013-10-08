package com.ecommuters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

import com.ecommuters.Task.EventType;

public class TrackingManager {

	private static final int TIMEOUT = 600000;

	private static final int distanceMeters = 2;

	private static final int MAX_OUT_OF_TRACK_COUNT = 20;
	// disposizione del
	// gps per
	// connettersi e
	// trovare la
	// posizione

	private Handler mHandler;
	private List<Task> mTasks = new ArrayList<Task>();
	private ConnectorService mService;
	private boolean liveTracking;
	public LiveTrackingEvent LiveTrackingEvent = new LiveTrackingEvent();
	private Timer mTimer = new Timer(true);

	private TimerTask timerTask = null;

	private int outOfTrackCount;

	private Route[] mRoutes;
	private List<Route> mRoutesByPosition = new ArrayList<Route>();

	public TrackingManager(Handler handler, ConnectorService service) {
		mHandler = handler;
		mService = service;
	}

	public void locationChanged(ECommuterPosition mLocation) {
		calculateRoutesByPosition(mLocation);

		boolean b = mRoutesByPosition.size() > 0;
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

	private void calculateRoutesByPosition(ECommuterPosition position) {
		mRoutesByPosition.clear();
		for (Route r : mRoutes) {
			boolean tracked = false;
			for (int i = r.latestIndex; i < r.getPoints().size(); i++) {
				RoutePoint pt = r.getPoints().get(i);
				if (position.distance(pt) < distanceMeters)// due metri
				{
					mRoutesByPosition.add(r);
					r.latestIndex = i;
					tracked = true;
					break;
				}
			}
			if (!tracked)
				r.latestIndex = 0;
		}
	}

	
	public void scheduleLiveTracking() {
		clearSchedules();

		ArrayList<TimeInterval> intervals = getLiveTrackingTimeIntervals();
		for (TimeInterval interval : intervals) {

			Task startingTask = schedule(interval.getStart(),
					interval.getRoute(), interval.getWeight(),
					EventType.START_TRACKING);

			schedule(interval.getEnd(), interval.getRoute(),
					interval.getWeight(), EventType.STOP_TRACKING);

			if (interval.isActiveNow()) {
				startingTask.execute();
			}
		}

	}

	private void clearSchedules() {
		// prima elimino i pending task
		mRoutes = MyApplication.getInstance().getRoutes();
		for (Route r : mRoutes)
			r.latestIndex = 0;
		for (Task t : mTasks) {
			mHandler.removeCallbacks(t);

		}
		mTasks.clear();
		// poi disabilito il GPS
		mService.resetGPS();

	}

	private Task schedule(Date time, Route route, int weight, EventType type) {
		Task task = new Task(this, mHandler, time, type, weight,
				route);
		task.activate();
		mTasks.add(task);
		return task;
	}

	private ArrayList<TimeInterval> getLiveTrackingTimeIntervals() {
		ArrayList<TimeInterval> intervals = new ArrayList<TimeInterval>();
		for (Route r : MyApplication.getInstance().getRoutes()) {
			for (int i = 0; i < GPSManager.MAX_GPS_LEVELS; i++)
				intervals.add(new TimeInterval(r,
						r.getPoints().get(0).time * 1000, i));
			// per debug
			//long currentTimeMillis = System.currentTimeMillis();
			//for (int i = 0; i < GPSManager.MAX_GPS_LEVELS; i++)
			//	intervals.add(new TimeInterval(r, currentTimeMillis, i));
		}

		return intervals;

	}

	public void OnExecuteTask(Task task) {
		mService.OnExecuteTask(task);
	}
	
	
}
