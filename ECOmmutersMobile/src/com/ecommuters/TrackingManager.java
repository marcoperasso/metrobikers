package com.ecommuters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ecommuters.Task.EventType;

public class TrackingManager {


	private static final int TIMEOUT = 600000;

	private static final int distanceMeters = 25;

	private static final int MAX_OUT_OF_TRACK_COUNT = 20;
	// disposizione del
	// gps per
	// connettersi e
	// trovare la
	// posizione

	private List<Task> mTasks = new ArrayList<Task>();
	private ConnectorService mService;
	private boolean liveTracking;
	public LiveTrackingEvent LiveTrackingEvent = new LiveTrackingEvent();
	public FollowedRouteEvent FollowedRoutesChanged = new FollowedRouteEvent();
	List<Route> mFollowedRoutes = new ArrayList<Route>();
	private Timer mTimer = new Timer(true);

	private TimerTask timerTask = null;

	private int outOfTrackCount;

	private Route[] mRoutes;

	private int mTrackingCount;

	public TrackingManager(ConnectorService service) {
		mService = service;
	}

	public void locationChanged(ECommuterPosition mLocation) {
		boolean b = calculateRoutesByPosition(mLocation);
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

	private boolean calculateRoutesByPosition(ECommuterPosition position) {
		boolean atLeastOneRoute = false;
		float error = distanceMeters; 
		for (Route r : mRoutes) {
			
			boolean followed = false;
			for (int i = r.latestIndex; i < r.getPoints().size(); i++) {
				RoutePoint pt = r.getPoints().get(i);
				double distance = position.distance(pt);
				if (distance < error) {
					atLeastOneRoute = true;
					r.latestIndex = i;
					followed = true;
					break;
				}
			}
			if (followed)
			{
				if (!mFollowedRoutes.contains(r))
				{
					mFollowedRoutes.add(r);
					FollowedRoutesChanged.fire(this, new FollowedRouteEventArgs(true, r));
				}
			}
			else
			{
				if (mFollowedRoutes.contains(r))
				{
					mFollowedRoutes.remove(r);
					FollowedRoutesChanged.fire(this, new FollowedRouteEventArgs(false, r));
				}
			}
			
		}
		return atLeastOneRoute;
	}

	public void scheduleLiveTracking() {
		clearSchedules();
		Route[] routes = MyApplication.getInstance().getRoutes();
		for (Route r : routes) {
			for (TimeInterval interval : r.getIntervals()) {
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
	}

	void clearSchedules() {
		// prima elimino i pending task
		mRoutes = MyApplication.getInstance().getRoutes();
		for (Route r : mRoutes)
			r.latestIndex = 0;
		for (Task t : mTasks)
			t.cancel();
		mTasks.clear();
		// poi disabilito il GPS
		mService.resetGPS();

	}

	private Task schedule(Date time, Route route, int weight, EventType type) {
		Task task = new Task(time, type, weight, route.getName());
		task.activate();
		mTasks.add(task);
		return task;
	}

	public void OnExecuteTask(Task task) {
		
		switch (task.getType())
		{
			case START_TRACKING :
				mTrackingCount++;
				break;
			case STOP_TRACKING :
				mTrackingCount--;
				if (mTrackingCount == 0)
					for (Route r : mRoutes)
						r.latestIndex = 0;
				break;
			default :
				break;
			
		}
	}

}
