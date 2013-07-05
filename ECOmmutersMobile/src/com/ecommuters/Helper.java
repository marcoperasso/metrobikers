package com.ecommuters;

import android.content.Context;
import android.net.ConnectivityManager;

public class Helper {
	
	static boolean isOnline(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch (Exception e) {
			return false;
		}

	}

	

	static boolean matchProtocolVersion() {
		return Const.PROTOCOL_VERSION.equals(RequestBuilder.getProtocolVersion());
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

}
