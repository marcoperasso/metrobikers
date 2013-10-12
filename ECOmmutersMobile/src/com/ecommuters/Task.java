package com.ecommuters;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

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

	private Date time;
	private EventType type;
	private int weight;
	private String mRouteName;

	public Task(Date time, EventType type, int weight, String routeName) {
		this.type = type;
		this.weight = weight;
		this.time = time;
		this.mRouteName = routeName;
	}

	public void run() {
		execute();
	}

	public void execute() {
		ConnectorService connectorService = MyApplication.getInstance()
				.getConnectorService();
		if (connectorService == null)
			return;
		connectorService.OnExecuteTask(this);
	}

	public void activate() {
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
		calendar.set(Calendar.MINUTE, time.getMinutes());
		calendar.set(Calendar.SECOND, time.getSeconds());
		Date next = calendar.getTime();
		if (next.getTime() < now.getTime()) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			next = calendar.getTime();
		}

		Intent intent = new Intent(MyApplication.getInstance(),
				GPSTrackerReceiver.class);
		intent.putExtra(TASK, this);
		PendingIntent pIntent = PendingIntent.getBroadcast(
				MyApplication.getInstance(), getAlarmId(), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) MyApplication.getInstance()
				.getSystemService(Context.ALARM_SERVICE);
		alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, next.getTime(), AlarmManager.INTERVAL_DAY,
				pIntent);

		Log.i(Const.ECOMMUTERS_TAG, String.format("Scheduling task %s with weight: %d at %s.", getType().toString(), getWeight(), next.toString()));
		
	}
	
	public void cancel()
	{
		Intent intent = new Intent(MyApplication.getInstance(),
				GPSTrackerReceiver.class);
		PendingIntent pIntent = PendingIntent.getBroadcast(
				MyApplication.getInstance(), getAlarmId(), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) MyApplication.getInstance()
				.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(pIntent);
		
	}
	private int getAlarmId() {
		final int prime = 31;
		int result = 1;
		result = prime * result + time.getHours();
		result = prime * result + time.getMinutes();
		result = prime * result + time.getSeconds();
		result = prime * result + type.hashCode();
		result = prime * result + weight;
		result = prime * result + mRouteName.hashCode();
		return Math.abs(result);
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
