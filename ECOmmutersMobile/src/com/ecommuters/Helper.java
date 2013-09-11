package com.ecommuters;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;

public class Helper {

	private static final String regExp = "[^a-zA-Z0-9.-]";

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
		return Const.PROTOCOL_VERSION.equals(RequestBuilder
				.getProtocolVersion());
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static void dialogMessage(final Context context, String message,
			String title, DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener) {
		new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert).setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.yes, okListener)
				.setNegativeButton(android.R.string.no, cancelListener).show();

	}

	public static void dialogMessage(final Context context, int message,
			int title, DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener) {
		dialogMessage(context, context.getString(message),
				context.getString(title), okListener, cancelListener);

	}

	public static List<File> getFiles(Context context, String ext) {
		File dir = context.getFilesDir();
		final List<File> files = new ArrayList<File>();
		File[] subFiles = dir.listFiles();
		if (subFiles != null) {
			for (File file : subFiles) {
				if (file.isFile() && file.getName().endsWith(ext)) {
					files.add(file);
				}
			}
		}
		return files;
	}
	
	public static List<File> getRoutePacketFiles(Context context, String routeName) {
		final List<File> files = new ArrayList<File>();
		List<File> subFiles = getFiles(context, Const.TOSENDEXT);
			for (File file : subFiles) {
				if (getRouteNameFromFileToSend(file.getName()).equals(routeName)) {
					files.add(file);
				}
			}
		return files;
	}
	public static boolean isValidRouteName(String routeName) {
		return !isNullOrEmpty(routeName) && routeName.replaceAll(regExp, "_").equals(routeName);
	}
	public static String getRouteFile(String routeName) {
		return routeName.replaceAll(regExp, "_") + Const.ROUTEEXT;
	}

	public static String getFileToSend(String routeName, int index) {
		return routeName + '.' + index + Const.TOSENDEXT;
	}
	public static String getRouteNameFromFileToSend(String file) {
		int endIdx = file.indexOf('.');
		return file.substring(0, endIdx);
	}
	public static boolean isRecordingServiceRunning(Context context) {
		return isServiceRunning(context, RecordRouteService.class);
	}

	private static boolean isServiceRunning(Context context,
			@SuppressWarnings("rawtypes") Class serviceClass) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	public static final String md5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
}
