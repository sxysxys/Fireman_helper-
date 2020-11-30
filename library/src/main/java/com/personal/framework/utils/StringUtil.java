package com.personal.framework.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class StringUtil {

    public static String removeNull(Object o) {
        if (o == null) {
            return "";
        }
        if (o.toString().equals("null")) {
            return "";
        }
        return o.toString();
    }

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || "NULL".equalsIgnoreCase(str);
    }

    //将两个字符串拼在一起
    public static String jointTwoString(String oneStr, String twoStr, boolean isOneEmptyShow, boolean isTowEmptyShow) {
        String result = "";
        if (!StringUtil.isEmpty(oneStr)) {//oneStr不为空时
            result += oneStr + " ";
        } else {
            if (isOneEmptyShow) {
                result += "---" + " ";
            }
        }
        if (!StringUtil.isEmpty(twoStr)) {//twoStr不为空时
            result += twoStr;
        } else {
            if (isTowEmptyShow) {
                result += "---";
            }
        }
        if (StringUtil.isEmpty(result)) {//oneStr,twoStr都为空时
            result = "---";
        }
        return result;
    }

    //获取SpannableString
    public static SpannableString getSpannabeStr(String str, ForegroundColorSpan colorSpan, AbsoluteSizeSpan sizeSpan, int begin, int end) {
        SpannableString spannableStr = new SpannableString(str);
        if (colorSpan != null) {
            spannableStr.setSpan(colorSpan, begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (sizeSpan != null) {
            spannableStr.setSpan(sizeSpan, begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableStr;
    }

    public static String convertListToString(List list) {
        if (list == null)
            return null;
        int iMax = list.size() - 1;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= iMax; i++) {
            sb.append(list.get(i));
            if (i < iMax)
                sb.append(",");
        }
        return sb.toString();
    }

    public static List<String> convertStringToList(String paraStr) {
        if (!StringUtil.isEmpty(paraStr)) {
            return Arrays.asList(paraStr.split(","));
        } else {
            return null;
        }
    }

    public static int toInt(String string) {
        int i = 0;
        String str = removeNull(string);
        if (str == "") {
            return i;
        } else {
            try {
                i = Integer.valueOf(str);
                return i;
            } catch (Exception e) {
                e.printStackTrace();
                return i;
            }
        }
    }

    public static float toFloat(String string) {
        float i = 0;
        String str = removeNull(string);
        if (str == "") {
            return i;
        } else {
            try {
                i = Float.valueOf(str);
                return i;
            } catch (Exception e) {
                e.printStackTrace();
                return i;
            }
        }
    }

    public static long toLong(String string) {
        long l = 0;
        String str = removeNull(string);
        if (str == "") {
            return l;
        } else {
            try {
                l = Long.valueOf(str);
                return l;
            } catch (Exception e) {
                e.printStackTrace();
                return l;
            }
        }
    }

    public static double toDouble(String string) {
        double d = 0;
        if (!isEmpty(string)) {
            try {
                d = Double.parseDouble(string);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return d;
    }

    public static void populateText(TextView txt, String content, boolean isEmptyShow) {
        if (!isEmpty(content)) {
            txt.setText(content);
        } else {
            if (isEmptyShow) {
                txt.setText("--");
            } else {
                txt.setText("");
            }
        }
    }

    public static String format(Context context, int resId, String str) {
        return String.format(context.getResources().getString(resId), str);
    }

    public static String getTwoSecimals(double value) {
        return String.format(Locale.CHINA, "%.2f", value);
    }

}
