/**
 * Project Name:VeryZhun_Pro
 * File Name:LogUtil.java
 * Package Name:com.feeyo.vz.pro.utils
 * Date:2014-2-12下午11:38:54
 * Copyright (c) 2014, lilong@feeyo.com All Rights Reserved.
 */
package com.personal.framework.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class LogUtil {

    private static final String TAG = "log";


    public static void i(String tag, String msg) {
        if (isDebugable()) {
            Log.i(tag, msg == null ? "null" : msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebugable()) {
            Log.v(tag, msg == null ? "null" : msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebugable()) {
            Log.d(tag, msg == null ? "null" : msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebugable()) {
            Log.w(tag, msg == null ? "null" : msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebugable()) {
            Log.e(tag, msg == null ? "null" : msg);
        }
    }

    public static void fy(String msg) {
        i(TAG, msg);
    }

    public static void println(String msg) {
        if (isDebugable()) {
            System.out.println(msg);
        }
    }

    public static void json(String tag, Object obj) {
        if (isDebugable()) {
            largeLog(tag, 1, json2String(GsonUtils.toJson(obj)));
        }
    }

    public static void json(String tag, String jsonStr) {
        if (isDebugable()) {
            largeLog(tag, 1, json2String(jsonStr));
        }
    }

    private static String json2String(String jsonStr) {
        try {
            if (jsonStr.startsWith("{")) {
                return new JSONObject(jsonStr).toString(2);
            } else if (jsonStr.startsWith("[")) {
                return new JSONArray(jsonStr).toString(2);
            }
        } catch (Exception e) {
        }
        return jsonStr;
    }

    private static final int MAX_LOG_LENGTH = 3500;
    private static final int LIMIT_LENGTH = 4000;

    public static void largeLog(String tag, int index, String msg) {
        if (!isDebugable()) {
            return;
        }
        final String fTag = tag + "_" + index;
        if (msg.length() < MAX_LOG_LENGTH && msg.getBytes().length < LIMIT_LENGTH) {
            Log.i(fTag, msg);
        } else {
            int subLength = getSubLength(msg);
            if (subLength > 0) {
                Log.i(fTag, msg.substring(0, subLength));
                if (subLength < msg.length()) {
                    largeLog(tag, ++index, msg.substring(subLength, msg.length()));
                }
            }
        }
    }

    private static int getSubLength(String msg) {
        String subStr = null;
        for (int i = MAX_LOG_LENGTH; i > 0; i -= 500) {
            subStr = msg.substring(0, i);
            if (subStr.getBytes().length < LIMIT_LENGTH) {
                break;
            }
        }
        if (null == subStr) {
            return 0;
        }
        for (int i = subStr.length() - 1; i >= 0; i--) {
            if (",".equals(subStr.charAt(i) + "")) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * 日志开关
     * @return
     */
    private static boolean isDebugable() {
        return true;
    }

}
