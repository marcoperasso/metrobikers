package com.ecommuters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (!Helper.isOnline(this))
			Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG)
					.show();
		else {
			if (!Helper.matchProtocolVersion()) {
				Toast t = Toast.makeText(this, R.string.wrong_version,
						Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
				finish();
				return;
			}
			WebView webView = (WebView) findViewById(R.id.webView1);
			class JsObject {
				public String toString() {
					return "Marco";
				}
			}
			webView.addJavascriptInterface(new JsObject(), "injectedObject");
			webView.loadData("", "text/html", null);
			webView.loadUrl("javascript:alert(injectedObject.toString())");
		}
		Button btnRoutes = (Button) findViewById(R.id.btn_routes);
		btnRoutes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(),
						TracksActivity.class);
				startActivity(myIntent);

			}
		});
		// Look up the AdView as a resource and load a request.
		// AdView adView = (com.google.ads.AdView)this.findViewById(R.id.ad);
		// adView.loadAd(new com.google.ads.AdRequest());
	}
}
