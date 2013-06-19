package com.ecommuters;

import android.content.Context;
import android.content.SharedPreferences;

public class MySettings {

	public static boolean getTrackGPSPosition(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		return settings.getBoolean(Const.TRACK_GPS, true);
	}
	
	public static void setTrackGPSPosition(Context context, boolean b) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Const.TRACK_GPS, b);
		editor.commit();
	}

}
