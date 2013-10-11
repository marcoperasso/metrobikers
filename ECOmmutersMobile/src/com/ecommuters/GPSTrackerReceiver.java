package com.ecommuters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSTrackerReceiver extends BroadcastReceiver {
	public GPSTrackerReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Task t = (Task) intent.getSerializableExtra(Task.TASK);
		t.execute();
	}
}
