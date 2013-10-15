package com.ecommuters;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class MySettings {
	public static Credentials CurrentCredentials;
	
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
	
	public static Credentials readCredentials(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		CurrentCredentials = new Credentials(settings.getInt(Const.USERID, 0), settings.getString(Const.EMAIL, ""), settings.getString(Const.PASSWORD, ""));
		return CurrentCredentials;
	}
	
	public static void setCredentials(Context context, Credentials c) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(Const.USERID, c.getUserId());
		editor.putString(Const.EMAIL, c.getEmail());
		editor.putString(Const.PASSWORD, c.getPassword());
		editor.commit();
		CurrentCredentials = c;
	}

	public static boolean isHiddenRoute(Context context, String routeName) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		return settings.getBoolean(Const.VISIBLE_ROUTES + routeName, false);
	}
	
	public static void setHiddenRoute(Context context, String routeName, boolean b) {
		SharedPreferences settings = context. getSharedPreferences(Const.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Const.VISIBLE_ROUTES + routeName, b);
		editor.commit();
	}

	public static long getLatestSyncDate(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		return settings.getLong(Const.LATEST_SYNC, 0L);
	}
	
	public static void setLatestSyncDate(Context context, long date) {
		SharedPreferences settings = context.getSharedPreferences(Const.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(Const.LATEST_SYNC, date);
		editor.commit();
	}

	public static List<Task> getScheduledTasks() {
		// TODO Auto-generated method stub
		return null;
	}

}
