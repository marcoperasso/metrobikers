package com.ecommuters;

import org.json.JSONException;
import org.json.JSONObject;

interface OnAsyncResponse {
	void response(boolean success, String message);
}
interface OnRouteSelected {
	void select(String routeName);
}
interface IJsonSerializable {
	public JSONObject toJson() throws JSONException;
}
interface OnRoutesAvailable
{
	public void gotRoutes(Route[] routes);
}