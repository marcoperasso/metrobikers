package com.ecommuters;

interface OnAsyncResponse {
	void response(boolean success, String message);
}
interface OnRouteSelected {
	void select(String routeName);
}

interface TEvent<TSender, TArgs> {
	public void fire(TSender sender, TArgs args);
}

interface GenericEvent extends TEvent<Object, EventArgs> {
}