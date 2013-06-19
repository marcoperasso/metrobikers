package com.ecommuters;

public class RequestBuilder {

	public static String getGetVersionRequest() {

		return "http://www.ecommuters.com/mobile/version";

	}

	public static String getDownloadTracksRequest(int minlat, int maxlat,
			int minlon, int maxlon) {

		return String
				.format("http://www.ecommuters.com/mobile/gettracks?minLat=%d&maxlat=%d&minlon=%d&maxlon=%d",
						minlat, maxlat, minlon, maxlon);
	}

	public static String getGetTrackDetailRequest(String routeName) {

		return "http://www.ecommuters.com/mobile/action=gettrackdetail?name="
				+ routeName;
	}

	public static String getDownloadTrackRequest(String routeName) {

		return "http://www.ecommuters.com/mobile/gettrackpoints?name="
				+ routeName;
	}
}
