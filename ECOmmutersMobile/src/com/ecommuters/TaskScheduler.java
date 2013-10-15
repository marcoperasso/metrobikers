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

		MySettings.setMaxTaskId(MyApplication.getInstance(), id);
	}

	private Task schedule(Date time, int weight, EventType type) {
		id++;
		Task task = new Task(time, type, weight);
		task.activate(id);
		return task;
	}

}
