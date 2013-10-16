package com.ecommuters;

import java.util.Date;

import com.ecommuters.Task.EventType;

public class TaskScheduler {
	int id;

	public TaskScheduler() {
		id = MySettings.getMaxTaskId(MyApplication.getInstance());
	}

	public void scheduleLiveTracking() {
		ConnectorService.resetGPSStatus();

		for (int i = 1; i <= id; i++)
			Task.cancel(i);

		id = 0;

		for (Route r : MyApplication.getInstance().getRoutes()) {
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

		long now = System.currentTimeMillis();
		schedule(new Date(now+30000), 0, EventType.START_TRACKING);
		schedule(new Date(now+60000), 0, EventType.STOP_TRACKING);
		
		MySettings.setMaxTaskId(MyApplication.getInstance(), id);
	}

	private Task schedule(Date time, int weight, EventType type) {
		id++;
		Task task = new Task(time, type, weight);
		task.schedule(id);
		return task;
	}

}
