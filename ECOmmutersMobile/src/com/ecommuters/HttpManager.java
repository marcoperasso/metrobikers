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

public class HttpManager {
	private static final boolean debuggingServer = false;
	private static final String host = debuggingServer ? "http://10.0.2.2:8888/" : "http://www.ecommuters.com/";
	private static final String getVersionRequest = host + "mobile/version";
	private static final String getUserRequest = host + "mobile/user";
	private static final String getUserLoggedRequest = host
			+ "mobile/user_logged";
	private static final String sendRouteDataRequest = host
			+ "mobile/save_route";
	private static final String sendPositionDataRequest = host
			+ "mobile/update_position";
	private static final String getRoutesRequest = host + "mobile/get_routes/";
	private static final String getRouteForUserRequest = host + "mobile/get_route_for_user/";
	private static final String getPositionsRequest = host
			+ "mobile/get_positions";
	public static final String HTTP_WWW_ECOMMUTERS_COM_LOGIN = host + "login/domobilelogin/";
	private static final String sendTrackingInfoDataRequest = host
			+ "mobile/save_tracking";

	static JSONObject sendRequestForObject(String reqString)
			throws JSONException, ClientProtocolException, IOException {
		StringBuilder result = sendRequest(reqString);
		return new JSONObject(result.toString());
	}

	static JSONArray sendRequestForArray(String reqString)
			throws JSONException, ClientProtocolException, IOException {
		StringBuilder result = sendRequest(reqString);
		return new JSONArray(result.toString());
	}

	private static StringBuilder sendRequest(String reqString)
			throws IOException, ClientProtocolException {
		StringBuilder result = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();

		HttpGet httpGet = new HttpGet(reqString);
		httpGet.setHeader("Cookie", getCookie());
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

	static JSONArray postRequestForArray(String reqString, IJsonSerializable data) throws ClientProtocolException, JSONException, IOException
	{
		return new JSONArray(postRequest(reqString, getJSONParameters(data)));
	}
	static JSONObject postRequestForObject(String reqString, IJsonSerializable data) throws ClientProtocolException, JSONException, IOException
	{
		return new JSONObject(postRequest(reqString, getJSONParameters(data)));
	}

	private static List<NameValuePair> getJSONParameters(IJsonSerializable data)
			throws JSONException {
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		String json = data.toJson().toString();
		postParameters.add(new BasicNameValuePair("data", json));
		return postParameters;
	}
	private static String postRequest(String reqString, List<NameValuePair> postParameters)
			throws ClientProtocolException, IOException, JSONException {
		StringBuilder result = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();

		HttpPost httpPost = new HttpPost(reqString);
		httpPost.setHeader("Cookie", getCookie());
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
		return result.toString();
	}

	private static String getCookie() {
		StringBuilder cookie = new StringBuilder();
		cookie.append(CookieManager.getInstance().getCookie(
				HTTP_WWW_ECOMMUTERS_COM_LOGIN));
		if (debuggingServer)
			cookie.append(";XDEBUG_SESSION=netbeans-xdebug");
		return cookie.toString();
	}

	static int getProtocolVersion() throws Exception {
		JSONObject obj;
		try {
			obj = sendRequestForObject(getVersionRequest);
			if (obj != null)
				return obj.getInt("version");
		} catch (Exception e) {
			Log.e(Const.ECOMMUTERS_TAG, Log.getStackTraceString(e));
		}
		throw new Exception();
	}

	public static void fillCredentialsData(Credentials c) {

		JSONObject obj;
		try {
			obj = sendRequestForObject(getUserRequest);
			c.setName(obj.getString("name"));
			c.setSurname(obj.getString("surname"));
			c.setUserId(obj.getInt("userid"));
		} catch (Exception e) {
			Log.e(Const.ECOMMUTERS_TAG, Log.getStackTraceString(e));
		}
	}

	public static boolean isLogged() {

		JSONObject obj;
		try {
			obj = sendRequestForObject(getUserLoggedRequest);
			return obj.getBoolean("logged");
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean sendRouteData(Route route) throws JSONException,
			ClientProtocolException, IOException {

		JSONObject response = postRequestForObject(sendRouteDataRequest, route);
		if (response.has("saved") && response.getBoolean("saved")) {

			route.setId(response.getInt("id"));
			return true;
		}
		
		return false;

	}

	public static boolean sendTrackingData(TrackingInfo trackInfo)
			throws JSONException, ClientProtocolException, IOException {

		JSONObject response = postRequestForObject(sendTrackingInfoDataRequest,
				trackInfo);
		return response.has("saved") && response.getBoolean("saved");

	}

	public static List<Route> getRoutes(long latestUpdate)
			throws ClientProtocolException, JSONException, IOException {
		List<Route> routes = new ArrayList<Route>();
		JSONArray response = sendRequestForArray(getRoutesRequest
				+ latestUpdate);
		int length = response.length();
		for (int i = 0; i < length; i++) {
			routes.add(Route.parseJSON(response.getJSONObject(i)));
		}
		return routes;
	}
	
	public static Route getRoute(int userId, int routeId) throws ClientProtocolException, JSONException, IOException {
		JSONObject response = sendRequestForObject(getRouteForUserRequest + userId + "/" + routeId);
		return Route.parseJSON(response);
	}

	public static boolean sendPositionData(ECommuterPosition position)
			throws JSONException, ClientProtocolException, IOException {
		JSONObject response = postRequestForObject(sendPositionDataRequest, position);
		return response.has("saved") && response.getBoolean("saved");

	}

	public static ArrayList<ECommuterPosition> getPositions(int lat1, int lon1, int lat2,
			int lon2) {
		ArrayList<ECommuterPosition> list = new ArrayList<ECommuterPosition>();
		try {
			JSONArray points = sendRequestForArray(getPositionsRequest + "/"
					+ lat2 + "/" + lon1 + "/" + lat1 + "/" + lon2);
			int length = points.length();
			for (int i = 0; i < length; i++)
				list.add(ECommuterPosition.parseJSON(points.getJSONObject(i)));
			return list;
		} catch (Exception e) {
			return list;
		}

	}

	public static boolean login(String email, String pwd, StringBuilder message) {
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("email", email));
		postParameters.add(new BasicNameValuePair("pwd", Helper.md5(pwd)));
		try {
			String postRequest = postRequest(HTTP_WWW_ECOMMUTERS_COM_LOGIN, postParameters);
			JSONObject obj = new JSONObject(postRequest); 
			if (obj.has("message"))
				message.append(obj.getString("message"));
			return obj.has("success") && obj.getBoolean("success");
		} catch (Exception e) {
			message.append(e.getMessage());
			return false;
		}
		
	}
}
