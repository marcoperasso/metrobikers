package com.ecommuters;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RegisterRouteService extends Service {
	public RegisterRouteService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
