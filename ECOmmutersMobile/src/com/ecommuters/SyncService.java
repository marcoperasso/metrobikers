package com.ecommuters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

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

	private void sendRoutes(final List<Route> newRoutes) throws JSONException,
			ClientProtocolException, IOException, Exception {
		if (newRoutes.size() == 0)
			return;

		for (Route r : newRoutes) {
			int id = r.getId();
			if (!HttpManager.sendRouteData(r))
				throw new Exception("Cannot send route to server: "
						+ r.getName());
			if (r.getId() != id)
				r.save(this, Helper.getRouteFile(r.getName()));
		}

		if (MyApplication.LogEnabled)
			Log.i(Const.ECOMMUTERS_TAG, String.format(
					"Successfully sent %d new routes to www.ecommuters.com.",
					newRoutes.size()));
	}

	private void scheduleRetrial() {
		// riprovo fra dieci minuti
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 10);

		Intent intent = new Intent(MyApplication.getInstance(),
				SyncService.class);
		PendingIntent pIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) MyApplication.getInstance()
				.getSystemService(Context.ALARM_SERVICE);
		alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

		if (MyApplication.LogEnabled)
			Log.i(Const.ECOMMUTERS_TAG, String.format(
					"Scheduling sync routes task %s.",
					new Date(calendar.getTimeInMillis()).toString()));

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
		try {
			Log.i(Const.ECOMMUTERS_TAG, "Starting synchronization");
			final long latestUpdate = MySettings
					.getLatestSyncDate(SyncService.this);

			final List<Route> newRoutes = getNewRoutes(latestUpdate);
			final List<File> files = Helper.getFiles(this, Const.TRACKINGEXT);
			if (newRoutes.size() == 0 && files.size() == 0) {
				return;
			}
			Credentials.testCredentials(this, new OnAsyncResponse() {
				public void response(boolean success, String message) {

					boolean allSent = false;
					try {
						if (!success) {
							Log.e(Const.ECOMMUTERS_TAG, message);
							return;
						}
						sendTrackings(files);
						sendRoutes(newRoutes);
						allSent = true;
						MySettings.setLatestSyncDate(SyncService.this,
								(long) (System.currentTimeMillis() / 1e3));
					} catch (Exception e) {
						Log.e(Const.ECOMMUTERS_TAG, Log.getStackTraceString(e));
					} finally {
						if (!allSent)
							scheduleRetrial();
					}
				}

			});
		} finally {
			Log.i(Const.ECOMMUTERS_TAG, "Synchronization finished");

		}

	}

	private void sendTrackings(List<File> files)
			throws ClientProtocolException, JSONException, IOException {
		int sent = 0;
		for (File f : files) {
			TrackingInfo info = TrackingInfo
					.readTrackingInfo(this, f.getName());
			if (info == null) {
				f.delete();
				continue;
			}

			if (!HttpManager.sendTrackingData(info))
				throw new RuntimeException(
						"Cannot send tracking dats to server");
			sent++;
			f.delete();
		}
		if (MyApplication.LogEnabled)
			if (sent > 0)
				Log.i(Const.ECOMMUTERS_TAG,
						String.format(
								"Successfully sent %d tracking data packets to www.ecommuters.com.",
								sent));
	}
}