package com.ecommuters;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

public class ColorProvider {
	static List<Integer> colors = GetColorMap();

	public static Integer GetColor(double elevation, double minEle,
			double maxEle) {
		double ratio = (elevation - minEle) / (maxEle - minEle);
		int index = (int) (ratio * (colors.size() - 1));
		return colors.get(index);
	}

	private static List<Integer> GetColorMap() {
		List<Integer> colors = new ArrayList<Integer>();
		colors.addAll(GetGradients(Color.RED, Color.argb(255, 255, 165, 0), 255));
		colors.addAll(GetGradients(Color.argb(255, 255, 165, 0), Color.YELLOW,
				255));
		colors.addAll(GetGradients(Color.YELLOW, Color.GREEN, 255));
		colors.addAll(GetGradients(Color.GREEN, Color.BLUE, 255));
		colors.addAll(GetGradients(Color.BLUE, Color.argb(255, 75, 0, 130), 255));
		colors.addAll(GetGradients(Color.argb(255, 75, 0, 130), Color.MAGENTA,
				255));
		return colors;
	}

	private static List<Integer> GetGradients(int start, int end, int steps) {
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
