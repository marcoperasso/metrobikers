package com.ecommuters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class RoutesOverlay extends BalloonItemizedOverlay<OverlayItem> {

	enum RouteType {
		RECORDING, NORMAL, FOLLOWED
	}

	private MyMapActivity mContext;
	private Route[] routes;
	// private ImageView mImageView;
	private Paint pnt = new Paint();
	private MyMapView mMap;
	// Bitmap trackBitmap;
	GeoPoint trackRectOrigin;
	int currentZoomLevel = -1;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private ArrayList<ECommuterPosition> mPositions;
	private ArrayList<Route> followedRoutes = new ArrayList<Route>();
	private Hashtable<String, Route> cachedRoutes = new Hashtable<String, Route>();

	public RoutesOverlay(Drawable defaultMarker, MyMapActivity context,
			MyMapView map) {
		super(boundCenterBottom(defaultMarker), map);
		mContext = context;
		// mImageView = new ImageView(mContext);
		pnt.setStyle(Paint.Style.FILL);
		pnt.setStrokeWidth(4);
		pnt.setAntiAlias(true);
		this.mMap = map;
		// map.addView(mImageView);
		// mImageView.setAdjustViewBounds(true);
		// mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		LayoutParams lp = new MapView.LayoutParams(0, 0, new GeoPoint(0, 0),
				MapView.LayoutParams.TOP_LEFT);
		lp.mode = MapView.LayoutParams.MODE_MAP;
		// mImageView.setLayoutParams(lp);

		MapView.LayoutParams mlp = new MapView.LayoutParams(
				MapView.LayoutParams.MATCH_PARENT,
				MapView.LayoutParams.WRAP_CONTENT, 0, 0,
				MapView.LayoutParams.TOP_LEFT);

		setLastFocusedIndex(-1);

		populate();
	}

	@Override
	protected void onBalloonOpen(int index) {
		if (index < mPositions.size()) {
			final ECommuterPosition pos = mPositions.get(index);
			new Thread(new Runnable() {
				public void run() {
					try {
						boolean needRefresh = false;
						for (int id : pos.getRoutes())
							try {
								Route route = null;
								String ids = pos.userId + "-" + id;
								if (cachedRoutes.containsKey(ids))
									route = cachedRoutes.get(ids);
								else
								{
									route = HttpManager.getRoute(pos.userId,id);
									cachedRoutes.put(ids, route);
								}
								synchronized (followedRoutes) {
									followedRoutes.add(route);
								}
								needRefresh = true;
							} catch (Exception e) {
							}
						if (needRefresh) {
							mContext.runOnUiThread(new Runnable() {
								public void run() {
									mMap.invalidate();

								}
							});
						}
					} catch (Exception ex) {
						Log.e(Const.ECOMMUTERS_TAG, Log.getStackTraceString(ex));
					}
				}
			}).start();

		}
		super.onBalloonOpen(index);
	}

	@Override
	protected void onBalloonHide() {
		synchronized (followedRoutes) {
			boolean needRefresh = !followedRoutes.isEmpty();
			followedRoutes.clear();
			if (needRefresh)
				mMap.invalidate();
		}

		super.onBalloonHide();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	public void draw(final Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);

		if (routes == null)
			return;
		if (shadow)
			return;
		final Projection prj = mMap.getProjection();
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		final GeoPoint topLeft = prj.fromPixels(0, 0);
		final GeoPoint bottomRight = prj.fromPixels(width, height);
		final boolean invertX = topLeft.getLatitudeE6() > bottomRight
				.getLatitudeE6();
		final boolean invertY = topLeft.getLongitudeE6() > bottomRight
				.getLongitudeE6();
		for (Route r : routes) {
			if (MySettings.isHiddenRoute(mContext, r.getName()))
				continue;
			drawRoute(RouteType.NORMAL, r, invertX, invertY, topLeft,
					bottomRight, prj, canvas);
		}

		synchronized (followedRoutes) {
			for (Route r : followedRoutes) {
				drawRoute(RouteType.FOLLOWED, r, invertX, invertY, topLeft,
						bottomRight, prj, canvas);
			}
		}
		RecordRouteService recordingService = MyApplication.getInstance()
				.getRecordingService();
		if (recordingService != null) {
			Route route = recordingService.getRoute();
			synchronized (route) {
				drawRoute(RouteType.RECORDING, route, invertX, invertY,
						topLeft, bottomRight, prj, canvas);
			}
		}

	}

	private void drawRoute(RouteType type, Route r, boolean invertX,
			boolean invertY, GeoPoint topLeft, GeoPoint bottomRight,
			Projection prj, Canvas canvas) {
		Point p1 = new Point(), p2 = new Point();
		RoutePoint rp1 = null;
		boolean p1Calculated = false;
		int size = r.getPoints().size();
		for (int i = 0; i < size; i++) {
			RoutePoint pt = r.getPoints().get(i);
			if (type == RouteType.RECORDING
					|| i < r.getTrackingInfo().getLatestIndex())
				pnt.setColor(Color.RED);
			else if (type == RouteType.FOLLOWED) {
				if (pt.followedRouteColor == null) {
					pt.followedRouteColor = ColorProvider.getColor((double) i
							/ (double) size, type);
				}
				pnt.setColor(pt.followedRouteColor);
			} else {
				if (pt.normalColor == null) {
					pt.normalColor = ColorProvider.getColor((double) i
							/ (double) size, type);
				}
				pnt.setColor(pt.normalColor);
			}
			try {
				if (invertX) {
					if (pt.lat < bottomRight.getLatitudeE6()
							|| pt.lat > topLeft.getLatitudeE6())
						continue;
				} else {
					if (pt.lat > bottomRight.getLatitudeE6()
							|| pt.lat < topLeft.getLatitudeE6())
						continue;
				}
				if (invertY) {
					if (pt.lon < bottomRight.getLongitudeE6()
							|| pt.lon > topLeft.getLongitudeE6())
						continue;
				} else {
					if (pt.lon > bottomRight.getLongitudeE6()
							|| pt.lon < topLeft.getLongitudeE6())
						continue;
				}

				if (null != rp1) {
					if (!p1Calculated) {
						prj.toPixels(new GeoPoint(rp1.lat, rp1.lon), p1);
						p1Calculated = true;
					}
					prj.toPixels(new GeoPoint(pt.lat, pt.lon), p2);
					canvas.drawLine(p1.x, p1.y, p2.x, p2.y, pnt);
					p1.x = p2.x;
					p1.y = p2.y;
				}
			} finally {
				rp1 = pt;
			}
		}
	}

	public void setRoutes(Route[] mRoutes) {
		this.routes = mRoutes;
	}

	public void setPositions(ArrayList<ECommuterPosition> positions) {
		mPositions = positions;
		mOverlays.clear();

		for (ECommuterPosition pt : positions) {
			GeoPoint point = new GeoPoint(pt.lat, pt.lon);
			java.text.DateFormat timeFormat = DateFormat
					.getTimeFormat(mContext);
			Date df = new java.util.Date(pt.time * 1000);
			String text = pt.name + " " + pt.surname + " ("
					+ timeFormat.format(df) + ")";
			OverlayItem overlayitem = new OverlayItem(point,
					mContext.getString(R.string.app_name), text);
			mOverlays.add(overlayitem);
		}
		populate();

	}

	public ArrayList<ECommuterPosition> getPositions() {
		return mPositions;
	}

}
