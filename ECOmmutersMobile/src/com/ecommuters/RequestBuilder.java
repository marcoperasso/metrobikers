package com.ecommuters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.webkit.CookieManager;

public class RequestBuilder {

	private static final String getGetVersionRequest = "http://www.ecommuters.com/mobile/version";
	private static final String getUserRequest = "http://www.ecommuters.com/mobile/user";
	private static final String getSendRouteDataRequest = "http://www.ecommuters.com/mobile/save_route";

	static JSONObject sendRequest(String reqString)
			throws ClientProtocolException, JSONException, IOException {
		return sendRequest(reqString, "");
	}

	static JSONObject sendRequest(String reqString, String cookie)
			throws JSONException, ClientProtocolException, IOException {
		StringBuilder result = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();

		HttpGet httpGet = new HttpGet(reqString);
		httpGet.setHeader("Cookie", cookie);
		HttpResponse response = null;
		response = httpClient.execute(httpGet, localContext);
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(response.getEntity()
				.getContent()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			result.append(line);

		}
		reader.close();
		return new JSONObject(result.toString());
	}

	static JSONObject postRequest(String reqString, Object data, String cookie)
			throws ClientProtocolException, IOException, JSONException {
		StringBuilder result = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();

		HttpPost httpPost = new HttpPost(reqString);
		httpPost.setHeader("Cookie", cookie);
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		JSONObject jsonData = new JSONObject();
		jsonData.put("route", data);
		String json = jsonData.toString(); 
		postParameters.add(new BasicNameValuePair("route", json));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
		httpPost.setEntity(entity);
		HttpResponse response = null;
		response = httpClient.execute(httpPost, localContext);
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(response.getEntity()
				.getContent()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			result.append(line);

		}
		reader.close();
		return new JSONObject(result.toString());
	}

	static int getProtocolVersion() {
		JSONObject obj;
		try {
			obj = sendRequest(getGetVersionRequest);
			return obj == null ? -1 : obj.getInt("version");
		} catch (ClientProtocolException e) {
			Log.e("json", e.toString());
		} catch (JSONException e) {
			Log.e("json", e.toString());
		} catch (IOException e) {
			Log.e("json", e.toString());
		}
		return -1;
	}

	public static void fillCredentialsData(Credentials c) {
		CookieManager cookieManager = CookieManager.getInstance();
		final String cookie = cookieManager
				.getCookie(Const.HTTP_WWW_ECOMMUTERS_COM_LOGIN);
		JSONObject obj;
		try {
			obj = sendRequest(getUserRequest, cookie);
			c.setName(obj.getString("name"));
			c.setSurname(obj.getString("surname"));
		} catch (ClientProtocolException e) {
			Log.e("json", e.toString());
		} catch (JSONException e) {
			Log.e("json", e.toString());
		} catch (IOException e) {
			Log.e("json", e.toString());
		}
	}

	public static void sendRouteData(RegisteredPoints mPointsToSend)
			throws JSONException, ClientProtocolException, IOException {
		
		CookieManager cookieManager = CookieManager.getInstance();
		final String cookie = cookieManager
				.getCookie(Const.HTTP_WWW_ECOMMUTERS_COM_LOGIN);
		postRequest(getSendRouteDataRequest, mPointsToSend, cookie);
		JSONObject obj = new JSONObject();
		obj.put("route", mPointsToSend);

	}

}
