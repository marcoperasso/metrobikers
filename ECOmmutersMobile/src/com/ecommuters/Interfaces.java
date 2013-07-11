package com.ecommuters;

interface OnAsyncResponse {
	void response(boolean success, String message);
}
interface OnRouteSelected {
	void select (String routeName);
}