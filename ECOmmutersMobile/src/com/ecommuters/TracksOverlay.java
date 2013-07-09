package com.ecommuters;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class TracksOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<TrackOverlayItem> mOverlays = new ArrayList<TrackOverlayItem>();
	private TracksActivity mContext;
	private GPSTrack gpsPoints;
	private TrackInfo trackInfo;
	private ImageView mImageView;
	private Paint pnt = new Paint();
	private MyMapView mMap;
	Bitmap trackBitmap;
	GeoPoint trackRectOrigin;
	private TextView mTitleTextView;
	int currentZoomLevel = -1;
	private String mActiveTrackName = "";

	public TracksOverlay(Drawable defaultMarker, TracksActivity context,
			MyMapView map) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		mImageView = new ImageView(mContext);
		gpsPoints = null;
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
					resizeBitmap();
				}
				mContext.checkTracks();
			}
		});
		setLastFocusedIndex(-1);
		populate();
	}

	public void addTracksOverlay(ArrayList<Track> tracks) {
		mOverlays.clear();

		for (Track r : tracks) {
			GeoPoint point = new GeoPoint(r.getLat(), r.getLon());
			TrackOverlayItem overlayitem = new TrackOverlayItem(point, "", "",
					r);
			if (r.getName().equals(mActiveTrackName))
				setActiveMarker(overlayitem);
			mOverlays.add(overlayitem);
		}
		setLastFocusedIndex(-1);
		populate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0, MapView arg1) {
		switch (arg0.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
			mContext.checkTracks();
			break;
		default:

		}

		return super.onTouchEvent(arg0, arg1);
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

	private void drawTrack() {
		try {
			mImageView.setImageBitmap(null);
			recycle();

			if (gpsPoints == null)
				return;

			int realWidth = gpsPoints.maxLon - gpsPoints.minLon;
			int realHeight = gpsPoints.maxLat - gpsPoints.minLat;
			final int bmpWidth = 1024;
			double ratio = (double) bmpWidth / (double) realWidth;
			trackBitmap = Bitmap.createBitmap(bmpWidth,
					(int) (realHeight * ratio), Config.ARGB_8888);
			mImageView.setImageBitmap(trackBitmap);

			Canvas cnv = new Canvas(trackBitmap);

			int top = gpsPoints.size();
			float x1 = -1, y1 = -1;
			for (int i = 0; i < top; i++) {
				GpsPoint pt = gpsPoints.get(i);
				int offsetLat = gpsPoints.maxLat - pt.lat;
				int offsetLon = pt.lon - gpsPoints.minLon;
				float x2 = (float) ((float) offsetLon * ratio);
				float y2 = (float) ((float) offsetLat * ratio);
				if (x1 != -1) {
					//pnt.setColor(pt.getColor(gpsPoints.minEle, gpsPoints.maxEle));
					cnv.drawLine(x1, y1, x2, y2, pnt);
				}
				x1 = x2;
				y1 = y2;

			}

			resizeBitmap();
		} catch (Exception e) {
			manageError(e);
		} catch (Error e1) {
			manageError(e1);
		}

	}

	private void manageError(Throwable e) {

		Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
	}

	public void resizeBitmap() {
		try {
			if (trackBitmap == null)
				return;
			Projection projection = mMap.getProjection();

			GeoPoint topLeft = gpsPoints.getTopLeft();
			Point pxTopLeft = new Point();
			projection.toPixels(topLeft, pxTopLeft);

			GeoPoint bottomRight = gpsPoints.getBottomRight();
			Point pxBottomRight = new Point();
			projection.toPixels(bottomRight, pxBottomRight);

			int pxWidth = pxBottomRight.x - pxTopLeft.x;
			int pxHeight = pxBottomRight.y - pxTopLeft.y;

			MapView.LayoutParams params = (LayoutParams) mImageView
					.getLayoutParams();
			params.width = pxWidth;
			params.height = pxHeight;
			params.point = topLeft;
		} catch (Exception e) {
			manageError(e);
		} catch (Error e1) {
			manageError(e1);
		}

	}

	@Override
	protected boolean onTap(int index) {

		try {
			Vibrator v = (Vibrator) mContext
					.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(100);
			TrackOverlayItem item = mOverlays.get(index);
			TrackOverlayItem activeItem = getActiveItem();
			if (activeItem == item) {
				
			} else {
				if (activeItem != null)
					activeItem.setMarker(null);
				downloadTrack(item);
				setActiveMarker(item);
				mActiveTrackName = item.getName();
			}
		} catch (Exception e) {
			manageError(e);
		}

		return true;
	}

	private TrackOverlayItem getActiveItem() {
		for (TrackOverlayItem i : mOverlays)
			if (i.getName().equals(mActiveTrackName))
				return i;
		return null;
	}

	private void setActiveMarker(TrackOverlayItem item) {
		item.setMarker(boundCenterBottom(mContext.getResources().getDrawable(
				R.drawable.ic_activeroutemarker)));
	}

	private void downloadTrack(TrackOverlayItem item) {

		class DownloadPointsTask extends AsyncTask<String, Void, GPSTrack> {
			protected void onPostExecute(GPSTrack pts) {
				gpsPoints = pts;
				setLastFocusedIndex(-1);
				mMap.invalidate();
				mMap.getController().zoomToSpan(
						gpsPoints.maxLat - gpsPoints.minLat,
						gpsPoints.maxLon - gpsPoints.minLon);
				drawTrack();
				mContext.notifyMessage(-1);

			}

			@Override
			protected GPSTrack doInBackground(String... params) {
//				GPSTrack pts = new GPSTrack();
//				String reqString = RequestBuilder
//						.getDownloadTrackRequest(params[0]);
//				StringBuilder result = new StringBuilder();
//				if (!Helper.sendRequest(reqString, result))
//					return null;
//				String[] tokens = result.toString().split("-");
//				try {
//					for (int i = 0; i < tokens.length; i += 3) {
//
//						GpsPoint pt = new GpsPoint(Integer.parseInt(tokens[i]),
//								Integer.parseInt(tokens[i + 1]),
//								Double.parseDouble(tokens[i + 2]));
//						pts.add(pt);
//					}
//					return pts;
//				} catch (Exception e) {
//					manageError(e);
//				}
				return null;
			}

		}

		class DownloadDataTask extends AsyncTask<String, Void, TrackInfo> {

			protected void onPostExecute(TrackInfo info) {
				trackInfo = info;

				displayTrackInfo();
			}

			@Override
			protected TrackInfo doInBackground(String... params) {
//				String reqString = RequestBuilder
//						.getGetTrackDetailRequest(params[0]);
//				StringBuilder result = new StringBuilder();
//				if (!Helper.sendRequest(reqString, result))
//					return null;
//				try {
//					JSONObject obj = new JSONObject(result.toString());
//
//					TrackInfo info = new TrackInfo();
//					info.setTitle(obj.getString("title"));
//					info.setLength(obj.getDouble("length"));
//					info.setCycling(obj.getInt("cycling"));
//					info.setDifficulty(obj.getString("difficulty"));
//					info.setMaxHeight(obj.getDouble("maxHeight"));
//					info.setMinHeight(obj.getDouble("minHeight"));
//					info.setRating(obj.getDouble("rank"));
//					return info;
//
//				} catch (Exception e) {
//					manageError(e);
//				}
				return null;
			}

		}

		mMap.getController().animateTo(item.getPoint());
		mContext.notifyMessage(R.string.retrieving_info);
		DownloadDataTask task1 = new DownloadDataTask();
		task1.execute(item.getName());
		DownloadPointsTask task2 = new DownloadPointsTask();
		task2.execute(item.getName());

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

	void setCurrentTrack(GPSTrack gpsPoints, TrackInfo trackInfo) {
		this.gpsPoints = gpsPoints;
		this.trackInfo = trackInfo;
		drawTrack();
		displayTrackInfo();
	}

	public Serializable getGpsPoints() {
		return gpsPoints;
	}

	public TrackInfo getTrackInfo() {
		return trackInfo;
	}

	public String getActiveTrackName() {
		return mActiveTrackName;
	}

	public void setActiveTrackName(String currentTrackName) {
		this.mActiveTrackName = currentTrackName;
	}
}
