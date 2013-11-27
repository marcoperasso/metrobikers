package com.ecommuters.test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import android.util.Log;

import com.ecommuters.Credentials;
import com.ecommuters.HttpManager;
import com.ecommuters.MyApplication;
import com.ecommuters.OnAsyncResponse;
import com.ecommuters.Route;
import com.ecommuters.RoutePoint;

public class TestSendingRoute extends TestCase {
	Credentials credentials = new Credentials(0, "info@ecommuters.com", "aaaaaa");
	Route route = createTestRoute();
	@Test
	public void testSendReceive() {
		credentials.testLogin(MyApplication.getInstance(), new OnAsyncResponse(){

			@Override
			public void response(boolean success, String message) {
				Assert.assertTrue("Login fallita: " + message, success);
				if (!success)
					return;
				try {
					sendRoute();
					receiveRoute();

				
				} catch (Exception e) {
					Assert.assertTrue(Log.getStackTraceString(e), false);
					e.printStackTrace();
				}
			}
		} );
		
	}
	private void sendRoute() throws JSONException,
					ClientProtocolException, IOException {
				Assert.assertTrue("Invio itinerario fallito", HttpManager.sendRouteData(route));
				Assert.assertTrue("L'ID dell'itinerario è vuoto", route.getId() != 0);
			}
	
	private void receiveRoute() throws ClientProtocolException, JSONException, IOException {
		Route r = HttpManager.getRoute(credentials.getUserId(), route.getId());
		Assert.assertTrue("Download itinerario fallito", r != null);
		if (r == null)
			return;
		Assert.assertTrue("Gli itinerari non corrispondono", r.toJson().toString().equals(route.toJson().toString()));
		
	}
	

	private Route createTestRoute() {
		final Route r = new Route();
		r.setName("test");
		r.setMinutesBeforeStart(1);
		r.setMinutesAfterStart(17);
		r.setFriday(false);
		long latestUpdate = System.currentTimeMillis() / 1000;
		r.setLatestUpdate(latestUpdate);
		r.getPoints().add(new RoutePoint(1,1, ++latestUpdate));
		r.getPoints().add(new RoutePoint(2,2, ++latestUpdate));
		r.getPoints().add(new RoutePoint(3,3, ++latestUpdate));
		return r;
	}
}
