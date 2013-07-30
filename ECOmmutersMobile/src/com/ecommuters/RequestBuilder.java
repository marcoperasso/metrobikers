package com.ecommuters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.webkit.CookieManager;

public class RequestBuilder {
	// private static final String host = "http://10.0.2.2:8888/ecommuters/";
	private static final String host = "http://www.ecommuters.com/";
	private static final String getGetVersionRequest = host + "mobile/version";
	private static final String getUserRequest = host + "mobile/user";
	private static final String getUserLoggedRequest = host
			+ "mobile/user_logged";
	private static final String getSendRouteDataRequest = host
			+ "mobile/save_route";
	private static final String getGetRoutesRequest = host
			+ "mobile/get_routes";
	public static final String HTTP_WWW_ECOMMUTERS_COM_LOGIN = host + "login";

	static JSONObject sendRequestForObject(String reqString, Boolean useSession)
			throws JSONException, ClientProtocolException, IOException {
		StringBuilder result = sendRequest(reqString, useSession);
		return new JSONObject(result.toString());
	}
	static JSONArray sendRequestForArray(String reqString, Boolean useSession)
			throws JSONException, ClientProtocolException, IOException {
		StringBuilder result = sendRequest(reqString, useSession);
		return new JSONArray(result.toString());
	}
	private static StringBuilder sendRequest(String reqString,
			Boolean useSession) throws IOException, ClientProtocolException {
		StringBuilder result = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();

		HttpGet httpGet = new HttpGet(reqString);
		httpGet.setHeader("Cookie", getCookie(useSession));
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
		return result;
	}

	static JSONObject postRequest(String reqString, IJsonSerializable data,
			Boolean useSession) throws ClientProtocolException, IOException,
			JSONException {
		StringBuilder result = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();

		HttpPost httpPost = new HttpPost(reqString);
		httpPost.setHeader("Cookie", getCookie(useSession));
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		String json = data.toJson().toString();
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

	private static String getCookie(Boolean useSession) {
		StringBuilder cookie = new StringBuilder();
		if (useSession) {
			cookie.append(CookieManager.getInstance().getCookie(
					HTTP_WWW_ECOMMUTERS_COM_LOGIN));
		}
		cookie.append(";XDEBUG_SESSION=netbeans-xdebug");
		return cookie.toString();
	}

	static int getProtocolVersion() {
		JSONObject obj;
		try {
			obj = sendRequestForObject(getGetVersionRequest, false);
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

		JSONObject obj;
		try {
			obj = sendRequestForObject(getUserRequest, true);
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

	public static boolean isLogged() {

		JSONObject obj;
		try {
			obj = sendRequestForObject(getUserLoggedRequest, true);
			return obj.getBoolean("logged");
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean sendRouteData(Route route) throws JSONException,
			ClientProtocolException, IOException {

		JSONObject response = postRequest(getSendRouteDataRequest, route, true);
		return response.has("saved") && response.getBoolean("saved");

	}

	public static List<Route> getRoutes() throws ClientProtocolException,
			JSONException, IOException {
		List<Route> routes = new ArrayList<Route>();
		JSONArray response = sendRequestForArray(getGetRoutesRequest, true);
		for (int i = 0; i < response.length(); i++) {
			routes.add(Route.parseJSON(response.getJSONObject(i)));
		}
		return routes;
	}

}
