package com.rescue.hc.bean.model;

import com.rescue.hc.app.AppApplication;
import com.rescue.hc.lib.component.SpTag;
import com.personal.framework.utils.PreferenceUtils;

/**
 * @author szc
 * @date 2018/11/08
 * @describe 添加描述
 */

public class LoginInfoModel {

	public static void saveIMEI(String userId) {
		PreferenceUtils.put(AppApplication.getApp(), SpTag.IMEI, userId);
	}

	public static String getIMEI() {
		return (String) PreferenceUtils.get(AppApplication.getApp(), SpTag.IMEI, "");
	}

	public static void saveUserName(String userName) {
		PreferenceUtils.put(AppApplication.getApp(), SpTag.USER_NAME, userName);
	}

	public static void savePassword(String password) {
		PreferenceUtils.put(AppApplication.getApp(), SpTag.PASSWORD, password);
	}

	public static String getUserName() {
		return (String) PreferenceUtils.get(AppApplication.getApp(), SpTag.USER_NAME, "");
	}

	public static String getPassword() {
		return (String) PreferenceUtils.get(AppApplication.getApp(), SpTag.PASSWORD, "");
	}

	public static void setCbmember(boolean status) {
		PreferenceUtils.put(AppApplication.getApp(), SpTag.REMEMBER_PASSWORD, status);
	}

	public static boolean isRememberPassword() {
		return (boolean) PreferenceUtils.get(AppApplication.getApp(), SpTag.REMEMBER_PASSWORD, false);
	}

}
