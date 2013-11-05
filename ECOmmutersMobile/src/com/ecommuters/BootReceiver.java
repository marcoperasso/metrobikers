package com.ecommuters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	public BootReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(Const.ECOMMUTERS_TAG,
				"Performing boot operations.");
		new TaskScheduler().scheduleLiveTracking();
		
		Intent service = new Intent(context, SyncService.class);
		context.startService(service);
	}
}
