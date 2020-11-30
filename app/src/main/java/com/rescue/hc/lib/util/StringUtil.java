package com.rescue.hc.lib.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by chenxiong on 2016/6/17.
 */
public class StringUtil {
	public static String bytesToMd5String(byte[] bytes) {
		String resultString;
		try {
			final MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(bytes);
			resultString = bytesToHexString(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			resultString = String.valueOf(bytes.hashCode());
		}

		return resultString;
	}

	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static Date stringToDate(String pattern, String in) {
		SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
		simpleDateFormat.applyPattern(pattern);

		Date retDate = null;
		try {
			retDate = simpleDateFormat.parse(in);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return retDate;
	}

	public static String getFriendlyDateString(Date date, boolean showDayOfWeek) {
		if (date == null) {
			if (showDayOfWeek)
				return "--.--. 周--";
			else
				return "--.--.";
		}

		GregorianCalendar nowCalender = new GregorianCalendar();
		GregorianCalendar dstCalender = new GregorianCalendar();
		dstCalender.setTime(date);

		int now = nowCalender.get(GregorianCalendar.DAY_OF_YEAR);
		int dst = dstCalender.get(GregorianCalendar.DAY_OF_YEAR);
		int dstDayOfWeek = dstCalender.get(GregorianCalendar.DAY_OF_WEEK);
		String dstDayOfWeekString = "周--";
		if (showDayOfWeek) {
			switch (dstDayOfWeek) {
				case 1: {
					dstDayOfWeekString = "周日";
				}
				break;
				case 2: {
					dstDayOfWeekString = "周一";
				}
				break;
				case 3: {
					dstDayOfWeekString = "周二";
				}
				break;
				case 4: {
					dstDayOfWeekString = "周三";
				}
				break;
				case 5: {
					dstDayOfWeekString = "周四";
				}
				break;
				case 6: {
					dstDayOfWeekString = "周五";
				}
				break;
				case 7: {
					dstDayOfWeekString = "周六";
				}
				break;
			}
		}

		if (dst - now == 0) {
			if (showDayOfWeek)
				return "今天" + " " + dstDayOfWeekString;
			else
				return "今天";
		} else if (dst - now == 1) {
			if (showDayOfWeek)
				return "明天" + " " + dstDayOfWeekString;
			else
				return "明天";
		} else if (dst - now == 2) {
			if (showDayOfWeek)
				return "后天" + " " + dstDayOfWeekString;
			else
				return "后天";
		} else if (dst - now == -1) {
			if (showDayOfWeek)
				return "昨天" + " " + dstDayOfWeekString;
			else
				return "昨天";
		} else if (dst - now == -2) {
			if (showDayOfWeek)
				return "前天" + " " + dstDayOfWeekString;
			else
				return "前天";
		} else {
			SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
			if (showDayOfWeek)
				simpleDateFormat.applyPattern("M.d EE");
			else
				simpleDateFormat.applyPattern("M.d");
			return simpleDateFormat.format(date);
		}
	}

	public static String getCurrentDateTime(String pattern) {
		GregorianCalendar currentCalendar = new GregorianCalendar();
		SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
		simpleDateFormat.applyPattern(pattern);

		return simpleDateFormat.format(currentCalendar.getTime());
	}

	public static String formatDateTime(Date date) {
		if (date == null) {
			return "---";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		SimpleDateFormat format1 = new SimpleDateFormat("HH:mm", Locale.CHINA);
		return format.format(date) + "/" + format1.format(date);
	}

	public static byte[] toByteArray(String hexString) {
		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}
}
