package com.ecommuters;

import java.io.File;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;

public class ConnectorService extends IntentService {

	public ConnectorService() {
		super("ConnectorService");
	}
	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	protected void onHandleIntent(Intent intent) {
		try {
			if (Helper.isOnline(this)) {
				sendLocations();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void sendLocations() {
		if (MyApplication.getInstance().isSendingData())
			return;
		MyApplication.getInstance().setSendingData(true);
		final List<File> files = Helper.getFiles(this, Const.TOSENDEXT);

		if (files.size() == 0) {
			MyApplication.getInstance().setSendingData(false);
			return;
		}

		testCredentials(new OnAsyncResponse() {
			public void response(boolean success, String message) {

				try {
					if (!success)
						return;
					for (File file : files) {
						try {
							Route route = Route.readRoute(
									ConnectorService.this, file.getName());
							if (route != null
									&& RequestBuilder.sendRouteData(route))
								file.delete();
							// TODO refresh dei soli frammenti da mandare
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} finally {

				}

			}
		});

	}

	private void testCredentials(OnAsyncResponse testResponse) {
		if (RequestBuilder.isLogged()) {
			testResponse.response(true, "");
			return;
		}
		Credentials credential = MySettings.readCredentials(this);
		if (credential.isEmpty()) {
			testResponse.response(false, "");
			return;
		}
		credential.testLogin(this, testResponse);
	}
}
