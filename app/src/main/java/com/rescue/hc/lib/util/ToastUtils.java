package com.rescue.hc.lib.util;

import android.text.TextUtils;
import android.widget.Toast;

import com.rescue.hc.app.AppApplication;

import es.dmoral.toasty.Toasty;

/**
 * @author DevCheng
 * @description 对第三方库Toasy的封装，防止Toast重复弹出
 * @date 2018/07/11
 */

public class ToastUtils {

	private static Toast mToast;

	public static void error(String message, boolean withIcon) {
		if (TextUtils.isEmpty(message)) {
			return;
		}
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toasty.error(AppApplication.getApp().getApplicationContext(), message, Toast.LENGTH_SHORT, withIcon);
		mToast.show();
	}

	public static void success(String message, boolean withIcon) {
		if (TextUtils.isEmpty(message)) {
			return;
		}
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toasty.success(AppApplication.getApp().getApplicationContext(), message, Toast.LENGTH_SHORT, withIcon);
		mToast.show();
	}

	public static void info(String message, boolean withIcon) {
		if (TextUtils.isEmpty(message)) {
			return;
		}
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toasty.info(AppApplication.getApp().getApplicationContext(), message, Toast.LENGTH_SHORT, withIcon);
		mToast.show();
	}

	public static void warning(String message, boolean withIcon) {
		if (TextUtils.isEmpty(message)) {
			return;
		}
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toasty.warning(AppApplication.getApp().getApplicationContext(), message, Toast.LENGTH_SHORT, withIcon);
		mToast.show();
	}

	public static void normal(String message) {
		if (TextUtils.isEmpty(message)) {
			return;
		}
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toasty.normal(AppApplication.getApp().getApplicationContext(), message, Toast.LENGTH_SHORT);
		mToast.show();
	}
}
