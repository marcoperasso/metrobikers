package com.ecommuters;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

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
		START_TRACKING, STOP_TRACKING
	}

	private Calendar time;
	private EventType type;
	private int weight;

	public Task(Calendar time, EventType type, int weight) {
		this.type = type;
		this.weight = weight;
		this.time = time;
	}

	public void run() {
		execute();
	}

	public void execute() {
		ConnectorService.executeTask(this);
	}

	
	public void schedule(int id) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
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
		PendingIntent pIntent = PendingIntent.getService(MyApplication.getInstance(), id, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) MyApplication.getInstance()
				.getSystemService(Context.ALARM_SERVICE);
		alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pIntent);

		Log.i(Const.ECOMMUTERS_TAG, String.format(
				"Scheduling task %s with weight: %d at %s.", getType()
						.toString(), getWeight(), calendar.getTime().toString()));

	}

	public static void cancel(Integer id) {
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

	

}
