package com.personal.framework.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class PixelUtil {
	public static int dp2px(Context context, int value) {
		float scale = context.getResources().getDisplayMetrics().densityDpi;
		return (int) ((scale / 160) * value + 0.5f);
	}


	public static float dp2px(Context context, float value) {
		float scale = context.getResources().getDisplayMetrics().densityDpi;
		return (scale / 160) * value + 0.5f;
	}

	public static int px2dp(Context context, int value) {
		float scale = context.getResources().getDisplayMetrics().densityDpi;
		return (int) ((160 / scale) * value + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 获取屏幕宽度（该宽度为可用宽度）
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static int getScreenWidth(Context context) {
		int width = 0;
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point point = new Point();
			display.getSize(point);
			width = point.x;
		} else {
			width = display.getWidth();
		}
		return width;
	}
}
