package com.ecommuters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncService extends IntentService {
	public SyncService() {
		super("SyncService");

	}

	private void syncRoutes() {
		final long latestUpdate = MySettings
				.getLatestSyncDate(SyncService.this);

		final List<Route> newRoutes = getNewRoutes(latestUpdate);
		if (newRoutes.size() == 0) {
			return;
		}
		Credentials.testCredentials(this, new OnAsyncResponse() {
			public void response(boolean success, String message) {

				boolean sent = false;
				try {
					if (!success)
						return;
					for (Route r : newRoutes) {
						if (!RequestBuilder.sendRouteData(r))
							throw new Exception("Cannot send route to server: "
									+ r.getName());
					}
					sent = true;
					MySettings.setLatestSyncDate(SyncService.this,
							(long) (System.currentTimeMillis() / 1e3));
				} catch (Exception e) {
					Log.e(Const.ECOMMUTERS_TAG, e.toString());
				} finally {
					if (!sent)
						scheduleRetrial();
				}
			}

		});

	}

	private void scheduleRetrial() {
		//riprovo fra dieci minuti
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 10);
		
		Intent intent = new Intent(MyApplication.getInstance(),
				SyncService.class);
		PendingIntent pIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) MyApplication.getInstance()
				.getSystemService(Context.ALARM_SERVICE);
		alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pIntent);

		Log.i(Const.ECOMMUTERS_TAG, String.format(
				"Scheduling sync routes task %s.", new Date(calendar.getTimeInMillis()).toString()));

	}

	private List<Route> getNewRoutes(long latestUpdate) {
		List<Route> newRoutes = new ArrayList<Route>();
		for (Route r : MyApplication.getInstance().getRoutes()) {
			if (r.getLatestUpdate() > latestUpdate)
				newRoutes.add(r);
		}
		return newRoutes;
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		syncRoutes();

	}
}