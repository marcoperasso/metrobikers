package com.ecommuters;

import java.util.ArrayList;
import java.util.List;

import com.ecommuters.RoutesOverlay.RouteType;

import android.graphics.Color;

public class ColorProvider {
	private static final int SIZE = 255;

	static List<Integer> normalColors = getNormalColorMap();
	static List<Integer> followedColors = getFollowedColorMap();

	public static Integer getColor(double percentage, RouteType type) {
		int index = (int) (percentage * (SIZE - 1));
		return type == RouteType.NORMAL ? normalColors.get(index) : followedColors.get(index);
	}

	private static List<Integer> getNormalColorMap() {
		List<Integer> colors = new ArrayList<Integer>();
		colors.addAll(getGradients(Color.GREEN, Color.BLUE, SIZE));
		return colors;
	}
	private static List<Integer> getFollowedColorMap() {
		List<Integer> colors = new ArrayList<Integer>();
		colors.addAll(getGradients(Color.MAGENTA, Color.BLACK, 255));
		return colors;
	}

	private static List<Integer> getGradients(int start, int end, int steps) {
		int rStep = ((Color.red(end) - Color.red(start)) / (steps - 1));
		int gStep = ((Color.green(end) - Color.green(start)) / (steps - 1));
		int bStep = ((Color.blue(end) - Color.blue(start)) / (steps - 1));
		ArrayList<Integer> cl = new ArrayList<Integer>();
		for (int i = 0; i < steps; i++) {
			cl.add(Color.argb(255, (Color.red(start) + (rStep * i)),
					(Color.green(start) + (gStep * i)),
					(Color.blue(start) + (bStep * i))));
		}
		return cl;
	}

	/*
	 * public static string GetColorString (double ele, double minEle, double
	 * maxEle) { Color c = GetColor(ele, minEle, maxEle); return
	 * string.Format("#{0}{1}{2}", HexCode(c.R), HexCode(c.G), HexCode(c.B)); }
	 * 
	 * private static string HexCode (int code) { return
	 * code.ToString("x").PadLeft(2, '0'); }
	 */

}
