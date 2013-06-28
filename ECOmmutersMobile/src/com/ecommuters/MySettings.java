package com.ecommuters;

import android.content.Context;
import android.content.SharedPreferences;

public class MySettings {

	public static boolean getTrackGPSPosition(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		return settings.getBoolean(Const.TRACK_GPS, true);
	}
	
	public static void setTrackGPSPosition(Context context, boolean b) {
		SharedPreferences settings = context. getSharedPreferences(Const.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Const.TRACK_GPS, b);
		editor.commit();
	}
	
	public static Credentials getCredentials(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		Credentials c = new Credentials();
		c.setEmail(settings.getString(Const.EMAIL, ""));
		c.setPassword(settings.getString(Const.PASSWORD, ""));
		return c;
	}
	
	public static void setCredentials(Context context, Credentials c) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Const.EMAIL, c.getEmail());
		editor.putString(Const.PASSWORD, c.getPassword());
		editor.commit();
	}
	

}
