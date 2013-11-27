package com.ecommuters;

import org.json.JSONException;
import org.json.JSONObject;


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