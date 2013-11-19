package com.ecommuters;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ecommuters.Task.EventType;

public class Route implements IJsonSerializable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8299537479740651539L;
	private String name;
	private int id;
	private long latestUpdate;
	private List<RoutePoint> points = new ArrayList<RoutePoint>();

	// da non serializzare, servono solo per il motore di tracciatura
	transient private TrackingInfo trackingInfo;
	transient TimeInterval[] intervals = null;
	transient private Float distance;
	
	@Override
	public String toString() {
		return name;
	}

	public Route(String name) {
		this.name = name;
		
	}

	public Route() {
	}

	private TrackingInfo getTrackingInfo()
	{
		if (trackingInfo == null)
			trackingInfo = new TrackingInfo(this);
		return trackingInfo;
	}
	public String getName() {
		return name;
	}

	public void setName(String routeName) {
		this.name = routeName;
	}

	public List<RoutePoint> getPoints() {
		return points;
	}

	public boolean isEmpty() {
		return points.isEmpty();
	}

	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("name", name);
		obj.put("latestupdate", latestUpdate);
		JSONArray arPoints = new JSONArray();
		obj.put("points", arPoints);
		for (RoutePoint pt : points)
			arPoints.put(pt.toJson());
		return obj;
	}

	public static Route parseJSON(JSONObject jsonObject) throws JSONException {
		Route r = new Route(jsonObject.getString("name"));
		r.id = jsonObject.getInt("id");
		r.setLatestUpdate(jsonObject.getLong("latestupdate"));
		JSONArray points = jsonObject.getJSONArray("points");
		for (int i = 0; i < points.length(); i++)
			r.getPoints().add(RoutePoint.parseJSON(points.getJSONObject(i)));
		return r;
	}

	public static Route readRoute(Context context, String routeFile) {
		return (Route)Helper.readObject(context, routeFile);
	}

	public void save(Context context, String routeFile) throws IOException {
		Helper.saveObject(context, routeFile, this);
	}

	public static ArrayList<Route> readAllRoutes(Context context) {

		final List<File> files = Helper.getFiles(context, Const.ROUTEEXT);
		ArrayList<Route> routes = new ArrayList<Route>();
		for (File f : files) {
			Route readRoute = readRoute(context, f.getName());
			if (readRoute != null)
				routes.add(readRoute);
		}
		return routes;
	}

	public long getLatestUpdate() {
		return latestUpdate;
	}

	public void setLatestUpdate(long latestUpdate) {
		this.latestUpdate = latestUpdate;
	}

	public TimeInterval[] getIntervals() {
		if (intervals == null) {
			intervals = new TimeInterval[GPSStatus.MAX_TIMER_INTERVALS];
			for (int i = 0; i < GPSStatus.MAX_TIMER_INTERVALS; i++)
				intervals[i] = new TimeInterval(this,
						getStartingTimeSeconds()*1000, i);
		}
		return intervals;
	}

	public long getStartingTimeSeconds() {
		return getPoints().get(0).time;
	}
	public long getEndingTimeSeconds() {
		return getPoints().get(getPoints().size()-1).time;
	}

	public float getDistanceMetres() {
		if (distance == null)
			distance = calculateDistanceMetres();
		return distance;
	}

	private Float calculateDistanceMetres() {
		float d = 0;
		for (int i = 1; i < getPoints().size(); i++)
			d += getPoints().get(i).distance(getPoints().get(i-1));
			
		return d;
	}

	public long getTotalTimeSeconds() {
		return getEndingTimeSeconds() - getStartingTimeSeconds();
	}

	public float getAverageSpeed() {
		return getDistanceMetres() * 3.6f / getTotalTimeSeconds();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLatestTrackedIndex() {
		return getTrackingInfo().getLatestIndex();
	}

	public boolean saveTrackingInfo() {
		boolean b = getTrackingInfo().isEqualDistribution(points.size()) && getTrackingInfo().save();
		getTrackingInfo().reset();
		return b;
	}

	public boolean addTrackingPosition(int index, ECommuterPosition position) {
		getTrackingInfo().addPosition(index, position);
		return getTrackingInfo().isValid(this);
	}
	public boolean isFollowing(){return getTrackingInfo().isValid(this);}

	public void schedule(boolean executeIfActiveNow) {
		Log.i(Const.ECOMMUTERS_TAG,
				String.format("Scheduling route %s", name));
		for (TimeInterval interval : getIntervals()) {
			
			Task startingTask = schedule(interval.getStart(),
					interval.getWeight(), EventType.START_TRACKING);

			schedule(interval.getEnd(), interval.getWeight(),
					EventType.STOP_TRACKING);

			if (executeIfActiveNow && interval.isActiveNow()) {
				startingTask.execute();
			}
		}
		
	}
	public void scheduleDebug() {
		Log.i(Const.ECOMMUTERS_TAG,
				String.format("Scheduling route %s", name));
		for (TimeInterval interval : getIntervals()) {
			Calendar c = Calendar.getInstance();
			Task startingTask = schedule(c,
					interval.getWeight(), EventType.START_TRACKING);
			c.add(Calendar.MINUTE, 1);
			schedule(c, interval.getWeight(),
					EventType.STOP_TRACKING);

				startingTask.execute();
		}
		
	}
	
	private Task schedule(Calendar time, int weight, EventType type) {
		Task task = new Task(time, type, weight, id);
		task.schedule();
		return task;
	}

	public void cancelScheduling() {
		Task.cancel(id, EventType.START_TRACKING);
		Task.cancel(id, EventType.STOP_TRACKING);
		
	}
}
