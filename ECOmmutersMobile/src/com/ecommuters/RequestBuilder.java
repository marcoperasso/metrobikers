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

import com.ecommuters.PositionList;

import android.util.Log;
import android.webkit.CookieManager;

public class RequestBuilder {
	private static final String JSON = "json";
	// private static final String host = "http://10.0.2.2:8888/ecommuters/";
	private static final String host = "http://www.ecommuters.com/";
	private static final String getVersionRequest = host + "mobile/version";
	private static final String getUserRequest = host + "mobile/user";
	private static final String getUserLoggedRequest = host
			+ "mobile/user_logged";
	private static final String sendRouteDataRequest = host
			+ "mobile/save_route";
	private static final String sendPositionDataRequest = host
			+ "mobile/update_position";
	private static final String getRoutesRequest = host + "mobile/get_routes/";
	private static final String getPositionsRequest = host
			+ "mobile/get_positions";
	public static final String HTTP_WWW_ECOMMUTERS_COM_LOGIN = host + "login";
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

	static JSONObject postRequest(String reqString, IJsonSerializable data)
			throws ClientProtocolException, IOException, JSONException {
		StringBuilder result = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();

		HttpPost httpPost = new HttpPost(reqString);
		httpPost.setHeader("Cookie", getCookie());
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		String json = data.toJson().toString();
		postParameters.add(new BasicNameValuePair("data", json));
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

	private static String getCookie() {
		StringBuilder cookie = new StringBuilder();
		cookie.append(CookieManager.getInstance().getCookie(
				HTTP_WWW_ECOMMUTERS_COM_LOGIN));
		// cookie.append(";XDEBUG_SESSION=netbeans-xdebug");
		return cookie.toString();
	}

	static int getProtocolVersion() throws Exception {
		JSONObject obj;
		try {
			obj = sendRequestForObject(getVersionRequest);
			if (obj != null)
				return obj.getInt("version");
		} catch (Exception e) {
			Log.e(JSON, e.toString());
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
		} catch (ClientProtocolException e) {
			Log.e(JSON, e.toString());
		} catch (JSONException e) {
			Log.e(JSON, e.toString());
		} catch (IOException e) {
			Log.e(JSON, e.toString());
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

		JSONObject response = postRequest(sendRouteDataRequest, route);
		return response.has("saved") && response.getBoolean("saved");

	}

	public static boolean sendTrackingData(TrackingInfo trackInfo)
			throws JSONException, ClientProtocolException, IOException {

		JSONObject response = postRequest(sendTrackingInfoDataRequest, trackInfo);
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

	public static boolean sendPositionData(ECommuterPosition position)
			throws JSONException, ClientProtocolException, IOException {
		JSONObject response = postRequest(sendPositionDataRequest, position);
		return response.has("saved") && response.getBoolean("saved");

	}

	public static PositionList getPositions(int lat1, int lon1, int lat2,
			int lon2) {
		PositionList list = new PositionList();
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
}
