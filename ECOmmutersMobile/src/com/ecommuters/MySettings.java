package com.ecommuters;
import android.content.Context;
import android.content.SharedPreferences;

public class MySettings {
	public static final String PREFS_NAME = "i";
	public static final String TRACK_GPS = "l";
	public static final String MAX_TASK_ID = "m";
	public static final String VISIBLE_ROUTES = "r";
	public static final String LATEST_SYNC = "q";
	public static final String EMAIL = "o";
	public static final String PASSWORD = "p";
	public static final String USERID = "uid";
	
	public static Credentials CurrentCredentials;
	
	public static boolean getTrackGPSPosition(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean(TRACK_GPS, true);
	}
	
	public static void setTrackGPSPosition(Context context, boolean b) {
		SharedPreferences settings = context. getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(TRACK_GPS, b);
		editor.commit();
	}
	
	public static int getMaxTaskId(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getInt(MAX_TASK_ID, 0);
	}
	
	public static void setMaxTaskId(Context context, int id) {
		SharedPreferences settings = context. getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(MAX_TASK_ID, id);
		editor.commit();
	}
	
	public static Credentials readCredentials(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		CurrentCredentials = new Credentials(settings.getInt(USERID, 0), settings.getString(EMAIL, ""), settings.getString(PASSWORD, ""));
		return CurrentCredentials;
	}
	
	public static void setCredentials(Context context, Credentials c) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(USERID, c.getUserId());
		editor.putString(EMAIL, c.getEmail());
		editor.putString(PASSWORD, c.getPassword());
		editor.commit();
		CurrentCredentials = c;
	}

	public static boolean isHiddenRoute(Context context, String routeName) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean(VISIBLE_ROUTES + routeName, false);
	}
	
	public static void setHiddenRoute(Context context, String routeName, boolean b) {
		SharedPreferences settings = context. getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(VISIBLE_ROUTES + routeName, b);
		editor.commit();
	}

	public static long getLatestSyncDate(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getLong(LATEST_SYNC, 0L);
	}
	
	public static void setLatestSyncDate(Context context, long date) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(LATEST_SYNC, date);
		editor.commit();
	}

}
