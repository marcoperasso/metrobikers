package com.ecommuters;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;

public class TracksOverlay extends ItemizedOverlay<OverlayItem> {
	private RoutesActivity mContext;
	private List<Route> routes;
	private TrackInfo trackInfo;
	private ImageView mImageView;
	private Paint pnt = new Paint();
	private MyMapView mMap;
	Bitmap trackBitmap;
	GeoPoint trackRectOrigin;
	private TextView mTitleTextView;
	int currentZoomLevel = -1;
	private String mActiveTrackName = "";

	public TracksOverlay(Drawable defaultMarker, RoutesActivity context,
			MyMapView map) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		mImageView = new ImageView(mContext);
		pnt.setStyle(Paint.Style.FILL);
		pnt.setStrokeWidth(4);
		pnt.setAntiAlias(true);
		this.mMap = map;
		map.addView(mImageView);
		mImageView.setAdjustViewBounds(true);
		mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		LayoutParams lp = new MapView.LayoutParams(0, 0, new GeoPoint(0, 0),
				MapView.LayoutParams.TOP_LEFT);
		lp.mode = MapView.LayoutParams.MODE_MAP;
		mImageView.setLayoutParams(lp);

		MapView.LayoutParams mlp = new MapView.LayoutParams(
				MapView.LayoutParams.MATCH_PARENT,
				MapView.LayoutParams.WRAP_CONTENT, 0, 0,
				MapView.LayoutParams.TOP_LEFT);
		mTitleTextView = new TextView(mContext);
		mTitleTextView.setVisibility(View.GONE);
		mTitleTextView.setTextColor(Color.YELLOW);
		map.addView(mTitleTextView, mlp);

		map.setOnDrawListener(new MyMapView.OnDrawListener() {

			public void onDraw() {
				if (mMap.getZoomLevel() != currentZoomLevel) {
					currentZoomLevel = mMap.getZoomLevel();
					//resizeBitmap();
				}
				mContext.checkTracks();
			}
		});
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

		/*
		 * GPSTrack points = gpsPoints; if (shadow || points == null ||
		 * suspendLayout) return;
		 * 
		 * int width = canvas.getWidth(); int height = canvas.getHeight();
		 * 
		 * Point p1 = new Point(), p2 = new Point(); int top = points.size();
		 * Projection prj = mMap.getProjection(); for (int i = 0; i < top; i++)
		 * { GpsPoint pt = points.get(i); prj.toPixels(pt, p2);
		 * 
		 * if (i > 0 && p2.x > 0 && p2.x < width && p2.y > 0 && p2.y < height) {
		 * 
		 * pnt.setColor(pt.getColor(points.minEle, points.maxEle));
		 * canvas.drawLine(p1.x, p1.y, p2.x, p2.y, pnt); } p1.x = p2.x; p1.y =
		 * p2.y;
		 * 
		 * }
		 */

	}

	private void drawRoute(Route r) {
		try {
			mImageView.setImageBitmap(null);
			recycle();

			int maxLon = Integer.MIN_VALUE;
			int minLon = Integer.MAX_VALUE;
			int maxLat = Integer.MIN_VALUE;
			int minLat = Integer.MAX_VALUE;
			for (GpsPoint pt : r.getPoints()) {
				if (pt.lat > maxLat)
					maxLat = pt.lat;
				if (pt.lon > maxLon)
					maxLon = pt.lon;
				if (pt.lat < minLat)
					minLat = pt.lat;
				if (pt.lon < minLon)
					minLon = pt.lon;
			}
			int realWidth = maxLon - minLon;
			int realHeight = maxLat - minLat;
			final int bmpWidth = 1024;
			double ratio = (double) bmpWidth / (double) realWidth;
			trackBitmap = Bitmap.createBitmap(bmpWidth,
					(int) (realHeight * ratio), Config.ARGB_8888);
			mImageView.setImageBitmap(trackBitmap);

			Canvas cnv = new Canvas(trackBitmap);

			int top = r.getPoints().size();
			float x1 = -1, y1 = -1;
			for (int i = 0; i < top; i++) {
				GpsPoint pt = r.getPoints().get(i);
				int offsetLat = maxLat - pt.lat;
				int offsetLon = pt.lon - minLon;
				float x2 = (float) ((float) offsetLon * ratio);
				float y2 = (float) ((float) offsetLat * ratio);
				if (x1 != -1) {
					// pnt.setColor(pt.getColor(gpsPoints.minEle,
					// gpsPoints.maxEle));
					cnv.drawLine(x1, y1, x2, y2, pnt);
				}
				x1 = x2;
				y1 = y2;

			}

			//resizeBitmap();
		} catch (Exception e) {
			manageError(e);
		} catch (Error e1) {
			manageError(e1);
		}

	}

	private void manageError(Throwable e) {

		Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
	}

	private void displayTrackInfo() {
		if (trackInfo != null) {

			mTitleTextView.setText(trackInfo.getTitle());
			mTitleTextView.setVisibility(View.VISIBLE);

		} else {
			mTitleTextView.setVisibility(View.GONE);
		}

	}

	public void recycle() {
		if (trackBitmap != null) {
			trackBitmap.recycle();
			trackBitmap = null;
		}
	}

	
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

	public void drawRoutes() {
		if (routes == null)
			return;
		for (Route r : routes)
			drawRoute(r);
	}
}
