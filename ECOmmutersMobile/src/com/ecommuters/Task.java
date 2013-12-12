package com.ecommuters;

import java.io.Serializable;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Task implements Runnable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7611830460762331987L;
	public static final String TASK = "t";

	public enum EventType {

		START_TRACKING(0), STOP_TRACKING(1);
		@SuppressWarnings("unused")
		private final int value;

		private EventType(int value) {
			this.value = value;
		}
	}

	private Calendar time;
	private EventType type;
	private int weight;
	private int routeId;

	public Task(Calendar time, EventType type, int weight, int routeId) {
		this.type = type;
		this.weight = weight;
		this.time = time;
		this.routeId = routeId;
	}

	public void run() {
		execute();
	}

	public void execute() {
		ConnectorService.executeTask(this);
	}

	public void schedule() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, time.get(Calendar.SECOND));

		Calendar now = Calendar.getInstance();

		if (calendar.getTimeInMillis() < now.getTimeInMillis()) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		Intent intent = new Intent(MyApplication.getInstance(),
				ConnectorService.class);
		intent.putExtra(TASK, this);
		PendingIntent pIntent = PendingIntent.getService(
				MyApplication.getInstance(), getId(), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) MyApplication.getInstance()
				.getSystemService(Context.ALARM_SERVICE);
		alarms.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);

		if (MyApplication.LogEnabled)
			Log.i(Const.ECOMMUTERS_TAG,
				String.format("Scheduling task %s with weight: %d at %s.",
						getType().toString(), getWeight(), calendar.getTime()
								.toString()));

	}

	private int getId() {
		return getId(routeId, type);
	}

	private static int getId(int routeId, EventType type) {
		return routeId * 2 + type.ordinal();
	}

	private static void cancel(Integer id) {
		Intent intent = new Intent(MyApplication.getInstance(),
				ConnectorService.class);
		PendingIntent pIntent = PendingIntent.getService(
				MyApplication.getInstance(), id, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) MyApplication.getInstance()
				.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(pIntent);

	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public int getWeight() {
		return weight;
	}

	public static void cancel(int routeId, EventType startTracking) {
		cancel(getId(routeId, startTracking));

	}

	public int getRouteId() {
		return routeId;
	}

	public boolean canExecuteToday(Route[] mRoutes) {
		for (Route r : mRoutes) {
			if (routeId == r.getId()) {
				int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				switch (day) {
				case Calendar.MONDAY:
					return r.isMonday();
				case Calendar.TUESDAY:
					return r.isTuesday();
				case Calendar.WEDNESDAY:
					return r.isWednesday();
				case Calendar.THURSDAY:
					return r.isThursday();
				case Calendar.FRIDAY:
					return r.isFriday();
				case Calendar.SATURDAY:
					return r.isSaturday();
				case Calendar.SUNDAY:
					return r.isSunday();
				}
				return true;
			}
		}
		return true;
	}

}
