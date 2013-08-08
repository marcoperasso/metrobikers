package com.ecommuters;


public class GlobalData {
	private static GlobalData mGlobalData;

	public EventHandler RouteChanged = new EventHandler();
	
	public static GlobalData get(){
		if (mGlobalData == null)
			mGlobalData = new GlobalData();
		return mGlobalData;
	}

}
