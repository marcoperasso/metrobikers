package com.ecommuters;

import java.io.Serializable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LiveTrackingReceiver extends BroadcastReceiver {
	public enum EventType {
		START_TRACKING, STOP_TRACKING
	};
	public static final String ID = "ID";
	public static final String INTERVAL = "INTERVAL";

	public LiveTrackingReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectorService connectorService = MyApplication.getInstance()
				.getConnectorService();
		if (connectorService == null)
			return;
		EventType id = (EventType) intent.getSerializableExtra(ID);
		TimeInterval timeInterval = (TimeInterval) intent.getExtras()
				.getSerializable(INTERVAL);

		//connectorService.updateLiveTrackingData();
	}
}
