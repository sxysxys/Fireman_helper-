package com.rescue.hc.http;

import com.rescue.hc.BuildConfig;

/**
 * Created by Administrator on 2018/1/22.
 */

public class UrlConstant {
	public static String BASE_URL;

	static {
		if (BuildConfig.DEBUG) {
			BASE_URL = "http://bosy315.com:8000";
//			BASE_URL = "http://" + HOST + ":8090";
		} else {
			BASE_URL = "http://bosy315.com:8000";
//			BASE_URL = "http://" + HOST + ":8090";
		}
	}

	public static final String login = "/watch/pad/padUser";
	public static final String bindPad = "/watch/pad/watchBindPad";
	public static final String unbindPad = "/watch/pad/watchUnbindPad";
	public static final String getHistory = "/watch/pad";

	public static final String getHeart = "/watch/heart";
	public static final String getSteps = "/watch/step";
	public static final String uploadTask = "/watch/train/result";

	public static final String[] WATCH_NAME_ID = new String[]{"110-唐传高", "111-李少华", "112-杨伟强", "113-范金乐", "114-潘树飞", "115-胡成", "116-左伟"};
}
