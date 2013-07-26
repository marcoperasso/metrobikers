package com.ecommuters;

import java.io.File;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;

public class ConnectorService extends IntentService {
	private boolean working;

	public ConnectorService() {
		super("ConnectorService");
	}
	@Override
	public void onCreate() {
		working = true;
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		working = false;
		super.onDestroy();
	}
	protected void onHandleIntent(Intent intent) {
		while (working) {
			try {
				Thread.sleep(10000);
				try {
					if (Helper.isOnline(this)) {
						sendLocations();
					}
				} catch (Exception e) {

					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void sendLocations() {
		final List<File> files = Helper.getFiles(this, Const.TOSENDEXT);

		if (files.size() == 0)
			return;
		

		testCredentials(new OnAsyncResponse() {

			public void response(boolean success, String message) {
				if (!success)
					return;
				for (File file : files) {
					try {
						Route route = Route.readRoute(ConnectorService.this, file.getName());
						if (route != null
								&& RequestBuilder.sendRouteData(route))
							file.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
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
