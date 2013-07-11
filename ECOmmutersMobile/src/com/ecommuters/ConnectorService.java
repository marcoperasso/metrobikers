package com.ecommuters;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
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
		File dir = getFilesDir();
		final List<File> files = new ArrayList<File>();
		File[] subFiles = dir.listFiles();
		if (subFiles != null) {
			for (File file : subFiles) {
				if (file.isFile() && file.getName().endsWith(Const.TOSENDEXT)) {
					files.add(file);
				}
			}
		}

		if (files.size() == 0)
			return;

		testCredentials(new OnAsyncResponse() {

			public void response(boolean success, String message) {
				if (!success)
					return;
				for (File file : files) {
					try {
						RegisteredRoute route = null;
						FileInputStream fis = openFileInput(file.getName());
						ObjectInput in = null;
						try {
							in = new ObjectInputStream(fis);
							route = (RegisteredRoute) in.readObject();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							in.close();
							fis.close();
						}
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
