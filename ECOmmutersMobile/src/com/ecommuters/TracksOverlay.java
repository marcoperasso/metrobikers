package com.ecommuters;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class TracksOverlay extends ItemizedOverlay<OverlayItem> {
	private MyMapActivity mContext;
	private Route[] routes;
	// private ImageView mImageView;
	private Paint pnt = new Paint();
	private MyMapView mMap;
	// Bitmap trackBitmap;
	GeoPoint trackRectOrigin;
	private TextView mTitleTextView;
	int currentZoomLevel = -1;
	private String mActiveTrackName = "";
	private RecordRouteService mRecordService;

	public TracksOverlay(Drawable defaultMarker, MyMapActivity context,
			MyMapView map) {
		super(boundCenterBottom(defaultMarker));
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
		mTitleTextView = new TextView(mContext);
		mTitleTextView.setVisibility(View.GONE);
		mTitleTextView.setTextColor(Color.YELLOW);
		map.addView(mTitleTextView, mlp);

		/*
		 * map.setOnDrawListener(new MyMapView.OnDrawListener() {
		 * 
		 * public void onDraw() { if (mMap.getZoomLevel() != currentZoomLevel) {
		 * currentZoomLevel = mMap.getZoomLevel(); // resizeBitmap(); }
		 * mContext.checkTracks(); } });
		 */
		setLastFocusedIndex(-1);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);

		if (routes == null)
			return;
		if (shadow)
			return;
		Projection prj = mMap.getProjection();
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		GeoPoint topLeft = prj.fromPixels(0, 0);
		GeoPoint bottomRight = prj.fromPixels(width, height);
		boolean invertX = topLeft.getLatitudeE6() > bottomRight.getLatitudeE6();
		boolean invertY = topLeft.getLongitudeE6() > bottomRight
				.getLongitudeE6();
		boolean draw = false;
		Point p1 = new Point(), p2 = new Point();
		for (Route r : routes) {
			if (MySettings.isHiddenRoute(mContext, r.getName()))
				continue;
			if (isRecordingRoute(r))
			{
				r = mRecordService.getSavedRoute();
				pnt.setColor(Color.RED);
			}
			else
			{
				pnt.setColor(Color.BLUE);
			}
			for (int i = 0; i < r.getPoints().size(); i++) {
				RoutePoint pt = r.getPoints().get(i);
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
				GeoPoint gp = new GeoPoint(pt.lat, pt.lon);
				prj.toPixels(gp, p2);

				if (draw) {
					// pnt.setColor(pt.getColor(points.minEle, points.maxEle));
					canvas.drawLine(p1.x, p1.y, p2.x, p2.y, pnt);
				}
				p1.x = p2.x;
				p1.y = p2.y;
				draw = true;
			}
		}

	}

	public boolean isRecordingRoute(Route r) {
		if (mRecordService == null)
			return false;
		Route savedRoute = mRecordService.getSavedRoute();
		return savedRoute != null
				&& r.getName().equals(savedRoute.getName());
	}


	public Route[] getRoutes() {
		return routes;
	}

	public void setRoutes(Route[] mRoutes) {
		this.routes = mRoutes;
	}

	public void setRecordService(RecordRouteService service) {
		mRecordService = service;

	}

}
