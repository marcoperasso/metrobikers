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
	private List<Route> routes;
	// private ImageView mImageView;
	private Paint pnt = new Paint();
	private MyMapView mMap;
	// Bitmap trackBitmap;
	GeoPoint trackRectOrigin;
	private TextView mTitleTextView;
	int currentZoomLevel = -1;
	private String mActiveTrackName = "";
	private Route mRecordingRoute;

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
				r = mRecordingRoute;
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
		return mRecordingRoute != null
				&& r.getName() == mRecordingRoute.getName();
	}
	// public void drawRoutes() {
	// try {
	// mImageView.setImageBitmap(null);
	// recycle();
	//
	// if (routes == null)
	// return;
	// int maxLon = Integer.MIN_VALUE;
	// int minLon = Integer.MAX_VALUE;
	// int maxLat = Integer.MIN_VALUE;
	// int minLat = Integer.MAX_VALUE;
	//
	// for (Route r : routes) {
	// for (GpsPoint pt : r.getPoints()) {
	// if (pt.lat > maxLat)
	// maxLat = pt.lat;
	// if (pt.lon > maxLon)
	// maxLon = pt.lon;
	// if (pt.lat < minLat)
	// minLat = pt.lat;
	// if (pt.lon < minLon)
	// minLon = pt.lon;
	// }
	// }
	//
	// int minDimension = 100;
	// int realWidth = Math.max(maxLon - minLon, minDimension);
	// int realHeight = Math.max(maxLat - minLat, minDimension);
	// final int bmpWidth = 1024;
	// double ratio = (double) bmpWidth / (double) realWidth;
	// trackBitmap = Bitmap.createBitmap(bmpWidth,
	// (int) (realHeight * ratio), Config.ARGB_8888);
	// mImageView.setImageBitmap(trackBitmap);
	//
	// Canvas cnv = new Canvas(trackBitmap);
	// for (Route r : routes) {
	// int top = r.getPoints().size();
	// float x1 = -1, y1 = -1;
	// for (int i = 0; i < top; i++) {
	// GpsPoint pt = r.getPoints().get(i);
	// int offsetLat = maxLat - pt.lat;
	// int offsetLon = pt.lon - minLon;
	// float x2 = (float) ((float) offsetLon * ratio);
	// float y2 = (float) ((float) offsetLat * ratio);
	// if (x1 != -1) {
	// // pnt.setColor(pt.getColor(gpsPoints.minEle,
	// // gpsPoints.maxEle));
	// cnv.drawLine(x1, y1, x2, y2, pnt);
	// }
	// x1 = x2;
	// y1 = y2;
	//
	// }
	// }
	// // resizeBitmap();
	// } catch (Exception e) {
	// manageError(e);
	// } catch (Error e1) {
	// manageError(e1);
	// }
	//
	// }

	// public void recycle() {
	// if (trackBitmap != null) {
	// trackBitmap.recycle();
	// trackBitmap = null;
	// }
	// }

	public String getActiveTrackName() {
		return mActiveTrackName;
	}

	public void setActiveTrackName(String currentTrackName) {
		this.mActiveTrackName = currentTrackName;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	public void setRecordingData(Route savedRoute) {
		mRecordingRoute = savedRoute;

	}

}
