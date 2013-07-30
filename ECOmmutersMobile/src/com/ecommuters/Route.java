package com.ecommuters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class Route implements IJsonSerializable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8299537479740651539L;
	private String name;
	private long latestUpdate;
	private List<RoutePoint> points = new ArrayList<RoutePoint>();
	public Route(String name) {
		this.name = name;
	}
	public Route() {
	}
	public String getName() {
		return name;
	}
	public void setName(String routeName) {
		this.name = routeName;
	}
	public List<RoutePoint> getPoints() {
		return points;
	}
	public boolean isEmpty() {
		return points.isEmpty();
	}

	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("latestupdate", latestUpdate);
		JSONArray arPoints = new JSONArray();
		obj.put("points", arPoints);
		for (RoutePoint pt : points)
			arPoints.put(pt.toJson());
		return obj;
	}
	
	public static Route parseJSON(JSONObject jsonObject) throws JSONException {
		Route r = new Route(jsonObject.getString("name"));
		r.setLatestUpdate(jsonObject.getLong("latestupdate"));
		JSONArray points = jsonObject.getJSONArray("points");
		for (int i = 0; i < points.length(); i++)
		r.getPoints().add(RoutePoint.parseJSON(points.getJSONObject(i)));
		return r;
	}
	public static Route readRoute(Context context, String routeFile) {
		File file = context.getFileStreamPath(routeFile);
		if (file.exists()) {
			try {
				FileInputStream fis = context.openFileInput(routeFile);
				ObjectInput in = null;
				try {
					in = new ObjectInputStream(fis);
					try {
						return (Route) in.readObject();
					} catch (Exception ex) {
						Log.e("ec", ex.getMessage(), ex);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					in.close();
					fis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;
	}
	public void save(Context context, String routeFile) throws IOException {
		FileOutputStream fos = context.openFileOutput(routeFile,
				Context.MODE_PRIVATE);
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.flush();
		} finally {
			out.close();
			fos.close();
		}

	}
	public static ArrayList<Route> readAllRoutes(Context context) {

		final List<File> files = Helper.getFiles(context, Const.ROUTEEXT);
		ArrayList<Route> routes = new ArrayList<Route>();
		for (File f : files)
			routes.add(readRoute(context, f.getName()));
		return routes;
	}
	public long getLatestUpdate() {
		return latestUpdate;
	}
	public void setLatestUpdate(long latestUpdate) {
		this.latestUpdate = latestUpdate;
	}
	
}
