package com.ecommuters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.ConnectivityManager;

public class Helper {
	static boolean sendRequest(String reqString, StringBuilder result) {
		result.setLength(0);
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();

		HttpGet httpGet = new HttpGet(reqString);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet, localContext);
		} catch (ClientProtocolException e) {
			result.append(e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			result.append(e.getLocalizedMessage());
			return false;
		}
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.append(line);

			}
			reader.close();
		} catch (IllegalStateException e) {
			result.append(e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			result.append(e.getLocalizedMessage());
			return false;
		}
		boolean ok = result.length() > 0
				&& result.charAt(result.length() - 1) == '1';
		result.delete(result.length() - 1, result.length());
		return ok;
	}

	static boolean isOnline(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch (Exception e) {
			return false;
		}

	}

	static String getProtocolVersion() {
		StringBuilder result = new StringBuilder();
		if (sendRequest(RequestBuilder.getGetVersionRequest(), result))
			return result.toString();
		return "";
	}

	static boolean matchProtocolVersion() {
		return getProtocolVersion().equals(Const.PROTOCOL_VERSION);
	}

}
