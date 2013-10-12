package com.ecommuters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GPSTrackerReceiver extends BroadcastReceiver {
	public GPSTrackerReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Task t = (Task) intent.getSerializableExtra(Task.TASK);
		Log.i(Const.ECOMMUTERS_TAG, String.format("Executing task %s with weight: %d." + t.getType().toString(), t.getWeight()));
		t.execute();
	}
}
