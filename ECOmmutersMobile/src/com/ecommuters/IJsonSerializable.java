package com.ecommuters;

import org.json.JSONException;
import org.json.JSONObject;

public interface IJsonSerializable {
	public JSONObject toJson() throws JSONException;
}
