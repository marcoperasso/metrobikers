package com.ecommuters;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class RoutesOverlay extends ItemizedOverlay<OverlayItem> {
	private MyMapActivity mContext;
	private Route[] routes;
	// private ImageView mImageView;
	private Paint pnt = new Paint();
	private MyMapView mMap;
	// Bitmap trackBitmap;
	GeoPoint trackRectOrigin;
	private TextView mTitleTextView;
	int currentZoomLevel = -1;
	private ArrayList<PositionOverlayItem> mOverlays = new ArrayList<PositionOverlayItem>();

	public RoutesOverlay(Drawable defaultMarker, MyMapActivity context,
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

		setLastFocusedIndex(-1);

		populate();
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		Toast.makeText(mContext, item.getSnippet(), Toast.LENGTH_LONG).show();
		return true;
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
			drawRoute(Color.BLUE, r, invertX, invertY, topLeft, bottomRight,
					prj, canvas);
		}
		RecordRouteService recordingService = MyApplication.getInstance()
				.getRecordingService();
		if (recordingService != null) {
			Route route = recordingService.getRoute();
			synchronized (route) {
				drawRoute(Color.RED, route, invertX, invertY, topLeft,
						bottomRight, prj, canvas);
			}
		}

	}

	private void drawRoute(int color, Route r, boolean invertX,
			boolean invertY, GeoPoint topLeft, GeoPoint bottomRight,
			Projection prj, Canvas canvas) {
		pnt.setColor(color);
		Point p1 = new Point(), p2 = new Point();
		RoutePoint rp1 = null;
		boolean p1Calculated = false;
		for (int i = 0; i < r.getPoints().size(); i++) {
			RoutePoint pt = r.getPoints().get(i);
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

	public void setPositions(List<ECommuterPosition> positions) {
		mOverlays.clear();
		Drawable drawable = mContext.getResources().getDrawable(
				R.drawable.ic_routemarker);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		for (ECommuterPosition pt : positions) {
			GeoPoint point = new GeoPoint(pt.lat, pt.lon);
			PositionOverlayItem overlayitem = new PositionOverlayItem(point,
					mContext.getString(R.string.app_name), pt.name + " "
							+ pt.surname);
			overlayitem.setMarker(drawable);
			mOverlays.add(overlayitem);
		}
		populate();

	}

}
