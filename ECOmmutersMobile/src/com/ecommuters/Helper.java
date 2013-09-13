package com.ecommuters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
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

	public static boolean isValidRouteName(String routeName) {
		return !isNullOrEmpty(routeName)
				&& routeName.replaceAll(regExp, "_").equals(routeName);
	}
	public static String getRouteFile(String routeName) {
		return routeName + Const.ROUTEEXT;
	}

	public static String getRouteFileToSend(String routeName) {
		return routeName + Const.TOSENDEXT;
	}
	public static void copyFile(File aSourceFile, File aTargetFile, boolean aAppend) throws IOException {
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			inStream = new FileInputStream(aSourceFile);
			inChannel = inStream.getChannel();
			outStream = new FileOutputStream(aTargetFile, aAppend);
			outChannel = outStream.getChannel();
			long bytesTransferred = 0;
			// defensive loop - there's usually only a single iteration :
			while (bytesTransferred < inChannel.size()) {
				bytesTransferred += inChannel.transferTo(0, inChannel.size(),
						outChannel);
			}
		} finally {
			// being defensive about closing all channels and streams
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
			if (inStream != null)
				inStream.close();
			if (outStream != null)
				outStream.close();
		}
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
