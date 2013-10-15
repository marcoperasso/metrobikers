package com.ecommuters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.ecommuters.Task.EventType;

public class TaskScheduler {

	private List<Integer> mTaskIds;
	private int id;

	public TaskScheduler() {
		readScheduledTaskIds();
		if (mTaskIds == null)
			mTaskIds = new ArrayList<Integer>();
	}

	public void scheduleLiveTracking() {
		ConnectorService.stopService();
		for (Integer i : mTaskIds)
			Task.cancel(i);
		mTaskIds.clear();
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
		
		writeScheduledTaskIds();
	}

	public void writeScheduledTaskIds() {
		try
		{
		FileOutputStream fos = MyApplication.getInstance().openFileOutput("TASKS.bin",
				Context.MODE_PRIVATE);
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.flush();
		} finally {
			out.close();
			fos.close();
		}
		}
		catch (Exception e)
		{
			Log.e(Const.ECOMMUTERS_TAG, e.toString());
		}

	}
	public void readScheduledTaskIds() {
		File file = MyApplication.getInstance().getFileStreamPath("TASKS.bin");
		if (file.exists()) {
			try {
				FileInputStream fis = MyApplication.getInstance().openFileInput("TASKS.bin");
				ObjectInput in = null;
				try {
					in = new ObjectInputStream(fis);
					try {
						mTaskIds = (ArrayList<Integer>) in.readObject();
					} catch (Exception ex) {
						Log.e(Const.ECOMMUTERS_TAG, ex.toString(), ex);
					}
				} catch (Exception e) {
					Log.e(Const.ECOMMUTERS_TAG, e.toString(), e);
				} finally {
					in.close();
					fis.close();
				}
			} catch (Exception e) {
				Log.e(Const.ECOMMUTERS_TAG, e.toString(), e);
			}

		}
	}
	
	
	private Task schedule(Date time, int weight, EventType type) {
		Task task = new Task(time, type, weight, id++);
		task.activate();
		mTaskIds.add(task.getId());
		return task;
	}


}
