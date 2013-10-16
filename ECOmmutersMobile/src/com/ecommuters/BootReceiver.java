package com.ecommuters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	public BootReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		new TaskScheduler().scheduleLiveTracking();
		
		Intent service = new Intent(context, SyncService.class);
		context.startService(service);
	}
}
