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
	public static int monday = 1;
	public static int tuesday = 2;
	public static int wednesday = 4;
	public static int thursday = 8;
	public static int friday = 16;
	public static int saturday = 32;
	public static int sunday = 64;
	private static final long serialVersionUID = 8299537479740651539L;
	public static final int MAX_TIMER_INTERVALS = 1;

	private String name;
	private int id;
	private long latestUpdate;
	private int minutesBeforeStart;
	private int minutesAfterStart;
	private int days = monday | tuesday | wednesday | thursday | friday
			| saturday | sunday;
	private List<RoutePoint> points = new ArrayList<RoutePoint>();

	// da non serializzare, servono solo per il motore di tracciatura
	transient private TrackingInfo trackingInfo;
	transient TimeInterval interval = null;
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

	private TrackingInfo getTrackingInfo() {
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
		obj.put("before", getMinutesBeforeStart());
		obj.put("after", getMinutesAfterStart());
		obj.put("days", days);
		JSONArray arPoints = new JSONArray();
		obj.put("points", arPoints);
		for (RoutePoint pt : points)
			arPoints.put(pt.toJson());
		return obj;
	}

	public static Route parseJSON(JSONObject jsonObject) throws JSONException {
		Route r = new Route(jsonObject.getString("name"));
		r.id = jsonObject.getInt("id");
		r.latestUpdate = jsonObject.getLong("latestupdate");
		r.minutesBeforeStart = jsonObject.getInt("before");
		r.minutesAfterStart = jsonObject.getInt("after");
		r.days = jsonObject.getInt("days");
		JSONArray points = jsonObject.getJSONArray("points");
		for (int i = 0; i < points.length(); i++)
			r.getPoints().add(RoutePoint.parseJSON(points.getJSONObject(i)));
		return r;
	}

	public static Route readRoute(Context context, String routeFile) {
		return (Route) Helper.readObject(context, routeFile);
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

	public TimeInterval getInterval() {
		if (interval == null) {
			interval = new TimeInterval(this, getStartingTimeSeconds() * 1000,
					0);
		}
		return interval;
	}

	public long getStartingTimeSeconds() {
		return getPoints().get(0).time;
	}

	public long getEndingTimeSeconds() {
		return getPoints().get(getPoints().size() - 1).time;
	}

	public float getDistanceMetres() {
		if (distance == null)
			distance = calculateDistanceMetres();
		return distance;
	}

	private Float calculateDistanceMetres() {
		float d = 0;
		for (int i = 1; i < getPoints().size(); i++)
			d += getPoints().get(i).distance(getPoints().get(i - 1));

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
		boolean b = getTrackingInfo().isEqualDistribution(points.size())
				&& getTrackingInfo().save();
		getTrackingInfo().reset();
		return b;
	}

	public boolean addTrackingPosition(int index, ECommuterPosition position) {
		getTrackingInfo().addPosition(index, position);
		return getTrackingInfo().isValid(this);
	}

	public boolean isFollowing() {
		return getTrackingInfo().isValid(this);
	}

	public void schedule(boolean executeIfActiveNow) {
		Log.i(Const.ECOMMUTERS_TAG, String.format("Scheduling route %s", name));
		TimeInterval interval = getInterval();

		Task startingTask = schedule(interval.getStart(), interval.getWeight(),
				EventType.START_TRACKING);

		schedule(interval.getEnd(), interval.getWeight(),
				EventType.STOP_TRACKING);

		if (executeIfActiveNow && interval.isActiveNow()) {
			startingTask.execute();
		}

	}

	public void scheduleDebug() {
		Log.i(Const.ECOMMUTERS_TAG, String.format("Scheduling route %s", name));
		TimeInterval interval = getInterval();
		Calendar c = Calendar.getInstance();
		Task startingTask = schedule(c, interval.getWeight(),
				EventType.START_TRACKING);
		c.add(Calendar.MINUTE, 1);
		schedule(c, interval.getWeight(), EventType.STOP_TRACKING);

		startingTask.execute();

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

	public int getMinutesBeforeStart() {
		if (minutesBeforeStart == 0)
			minutesBeforeStart = getDefaultMinutesBeforeStart();
		return minutesBeforeStart;
	}

	private int getDefaultMinutesBeforeStart() {
		return (int) (getTotalTimeSeconds() * 20 / 6000);// 20% della durata
															// totale
	}

	public int getMinutesAfterStart() {
		if (minutesAfterStart == 0)
			minutesAfterStart = getDefaultMinutesAfterStart();

		return minutesAfterStart;
	}

	private int getDefaultMinutesAfterStart() {
		return (int) (getTotalTimeSeconds() * 80 / 6000);// 60% della durata
															// totale
	}

	public void setMinutesBeforeStart(int value) {
		minutesBeforeStart = value;
		interval = null;// forzo il refresh
	}

	public void setMinutesAfterStart(int value) {
		minutesAfterStart = value;
		interval = null;// forzo il refresh
	}

	public void update() throws IOException {
		latestUpdate = System.currentTimeMillis() / 1000;
		save(MyApplication.getInstance(), Helper.getRouteFile(name));
		schedule(true);

	}

	public boolean isMonday() {
		return (days & monday) == monday;
	}

	public boolean isTuesday() {
		return (days & tuesday) == tuesday;
	}

	public boolean isWednesday() {
		return (days & wednesday) == wednesday;
	}

	public boolean isThursday() {
		return (days & thursday) == thursday;
	}

	public boolean isFriday() {
		return (days & friday) == friday;
	}

	public boolean isSaturday() {
		return (days & saturday) == saturday;
	}

	public boolean isSunday() {
		return (days & sunday) == sunday;
	}

	public void setMonday(boolean value) {
		if (value)
			days |= monday;
		else
			days &= ~monday;
	}

	public void setTuesday(boolean value) {
		if (value)
			days |= tuesday;
		else
			days &= ~tuesday;
	}

	public void setWednesday(boolean value) {
		if (value)
			days |= wednesday;
		else
			days &= ~wednesday;
	}

	public void setThursday(boolean value) {
		if (value)
			days |= thursday;
		else
			days &= ~thursday;
	}

	public void setFriday(boolean value) {
		if (value)
			days |= friday;
		else
			days &= ~friday;
	}

	public void setSaturday(boolean value) {
		if (value)
			days |= saturday;
		else
			days &= ~saturday;
	}

	public void setSunday(boolean value) {
		if (value)
			days |= sunday;
		else
			days &= ~sunday;
	}
}
