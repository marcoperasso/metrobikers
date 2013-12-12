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
		if (MyApplication.LogEnabled)
			Log.i(Const.ECOMMUTERS_TAG,
				"Performing boot operations.");
		for (Route r : MyApplication.getInstance().getRoutes())
			r.schedule(true);
		
		Intent service = new Intent(context, SyncService.class);
		context.startService(service);
	}
}
