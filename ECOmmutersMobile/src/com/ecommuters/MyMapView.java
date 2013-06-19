package com.ecommuters;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.google.android.maps.MapView;

public class MyMapView extends MapView {
	
	public interface OnDrawListener {
	    public void onDraw();
	};
	
	private OnDrawListener mDrawListener;
	
	public MyMapView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
			if (mDrawListener != null)
				mDrawListener.onDraw();		
		}

	public void setOnDrawListener(OnDrawListener drawListener) {
		this.mDrawListener = drawListener;
	}
}
