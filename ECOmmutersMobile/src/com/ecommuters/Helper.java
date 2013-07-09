package com.ecommuters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
		return Const.PROTOCOL_VERSION.equals(RequestBuilder
				.getProtocolVersion());
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static void dialogMessage(final Activity context, String message,
			String title, DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener) {
		new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert).setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.yes, okListener)
				.setNegativeButton(android.R.string.no, cancelListener).show();

	}

	public static void dialogMessage(final Activity context, int message,
			int title, DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener) {
		dialogMessage(context, context.getString(message),
				context.getString(title), okListener, cancelListener);

	}

	public static String getRouteFile(String routeName) {
		return routeName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".route";
	}

}
