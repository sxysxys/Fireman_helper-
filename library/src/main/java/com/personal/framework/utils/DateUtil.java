/**
 * Project Name:VeryZhun_Pro
 * File Name:DateUtil.java
 * Package Name:com.feeyo.vz.pro.utils
 * Date:2014-3-30下午11:32:01
 * Copyright (c) 2014, lilong@feeyo.com All Rights Reserved.
 */
package com.personal.framework.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYYMMDD_HH_MM_SS_SSS = "yyyyMMdd HH:mm:ss.SSS";
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_MM_DD = "MM-dd";
    public static final String FORMAT_YYYY_MM = "yyyy-MM";
    public static final String FORMAT_HH_MM = "HH:mm";
    public static final String FORMAT_HH_MM_DD_MM = "HH:mm dd/MM";
    public static final String FORMAT_MM_DD_HH_MM = "MM/dd  HH:mm";
    public static final String FORMAT_HH = "HH";
    public static final String FORMAT_HH_MM_SS = "HH:mm:ss";
    public static final String BeiJing_TIMEZONE = "28800";

    public static final long PEK_TIME_ZONE = 28800;

    public static final String format(long milliseconds) {
        return format(DEFAULT_FORMAT, milliseconds);
    }

    /**
     * 使用默认的时区格式化时间
     **/
    public static String format(String format, long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.format(new Date(milliseconds));
    }

    public static String format(String format, String milliseconds) {
        return format(format, strToMills(milliseconds));
    }


    /**
     * 使用原有时区格式化时间
     **/
    public static final String formatWithDefaultZone(String format, long milliseconds) {
        milliseconds = strToMills(milliseconds + "");
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(milliseconds));
    }

    public static final long parseWithDefaultZone(String pattern, String value) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(value).getTime();
    }

    public static final long parse(String pattern, String value) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.parse(value).getTime();
    }

    //获取当前时间的北京时间戳(其实是标准时间戳+当前时区与北京时区的差值)
    public static long getPekTimestamp() {
        long current_timestamp = System.currentTimeMillis() / 1000;
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = cal.getTimeZone();
        return current_timestamp + timeZone.getRawOffset() / 1000 - 28800;
    }

    //获取减去当前时区偏移量的时间值(s)
    public static long getPlusTimeZoneTimestamp() {
        long current_timestamp = System.currentTimeMillis() / 1000;
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = cal.getTimeZone();
        return current_timestamp - timeZone.getRawOffset() / 1000;
    }

    //	/**获取指定时区的时间毫秒数**/
    //	public static final String getZoneTime(String format, long gmtMillis, float zone) {
    //		//转换成指定时区的时间
    //		long zoneMillis = getZoneMillis(gmtMillis, zone);
    //		return formatZoneTime(format, zoneMillis, zone);
    //	}

    /**
     * 将gmt+0时间戳转换成指定时区的时间戳
     **/
    public static final long getZoneMillis(long gmtMillis, float zone) {
        long millis = gmtMillis + (long) (zone * 60 * 60 * 1000);//小时偏移量
        return millis;
    }

    /**
     * 将GMT+0时间戳格式化成pattern格式的指定时区时间
     **/
    public static final String formatZoneTime(String pattern, long gmtMillis, float zone) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(getTimeZone(zone));
        return sdf.format(new Date(gmtMillis));
    }

    /**
     * 获取zone时区的TimeZone对象
     **/
    public static final TimeZone getTimeZone(float zone) {
        int i = (int) zone;
        int j = Math.abs((int) ((zone - i) * 60));
        return TimeZone.getTimeZone(String.format("GMT%1$+d:%2$02d", i, j));
    }

    /**
     * 获取zone时区的TimeZone对象
     **/
    public static final String getTimeZone(long offset) {
        String id = "UTC";
        if (offset >= 0) {
            id += "+" + offset / 3600;
        } else {
            id += offset / 3600;
        }
        return id;
    }

    public static final int getMillsOffset(int zone) {
        return (28800 - zone) * 1000;
    }

    /*
    * sourceOffset,long,源数据的时区偏移量，秒数
    * targetOffset，long,目标数据的时区偏移量，秒数
    * sourceSeconds，long,源数据的毫秒数
    * */
    public static long changeOtherZoneToPek(long sourcemillies, long sourceOffset, long gmt8Offset) {
        long targetTime = 0;
        targetTime = sourcemillies - sourceOffset * 1000 + gmt8Offset * 1000;
        return targetTime;
    }

    public static final String getFlightDepTimeString(String format, long gmtDep, float zone) {
        return formatZoneTime(format, gmtDep, zone);
    }

    /**
     * 考虑跨天的情况，获取到达时间显示字符串,默认格式为HH:mmj,如果有跨天的话格式为(x日)HH:mm
     **/


    /**
     * unix时间戳格式化
     *
     * @param pattern
     * @param lg
     * @return
     */
    public static final String formatUnixTimeStamp(String pattern, long lg) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.format(new Date(lg * 1000));
    }

    /**
     * unix时间戳转换为Date
     *
     * @param lg
     * @return
     */
    public static final Date unixTimeStampToDate(long lg) {

        return new Date(lg * 1000);
    }

    public static final boolean isToday(Date date) {
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String todayStr = formatter.format(today);
        String dateStr = formatter.format(date);
        return todayStr.equals(dateStr);
    }

    public static final String convertForStatistics(long lg) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Date date = unixTimeStampToDate(lg);
        if (isToday(date)) {
            return "今日";
        } else {
            return sdf.format(date);
        }
    }

    /**
     * 获取当前时间
     */
    public static final String getCurrentTimeString(String format) {
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return formatter.format(today);
    }

    public static final String getCurrentTimeZoneTimeString(String format) {
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(today);
    }

    /**
     * 获取当前月份
     */
    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取某月第一天
     */
    public static Calendar getTheFirstDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    /**
     * 获取向前几个月第一天
     */
    public static String getAfterTheFirstDayOfMonthStr(int cha) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, cha);
        calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return format(FORMAT_YYYY_MM_DD, calendar.getTimeInMillis());
    }

    /**
     * 获取某月第一天
     */
    public static String getTheFirstDayOfMonthStr(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return format(FORMAT_YYYY_MM_DD, calendar.getTimeInMillis());
    }

    /**
     * 获取某月最后一天
     */
    public static Calendar getTheLastDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        return calendar;
    }

    /**
     * 获取某月最后一天
     */
    public static String getTheLastDayOfMonthStr(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        return format(FORMAT_YYYY_MM_DD, calendar.getTimeInMillis());
    }

    /**
     * 获取日历视图某月显示的所有日期
     * 例如获取2015年9月的日历展示的所有日期数据，9月1日为周二，所以要补充8月最后两天，9月30日为周三，所以要补充10月的头3天
     */
    public static List<String> getCalendarMonthDays(int month) {
        List<String> dateList = new ArrayList();
        Calendar firstDayOfMonth = getTheFirstDayOfMonth(month);
        int firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);
        int previousNeedAddDay = firstDayOfWeek - Calendar.SUNDAY;

        Calendar lastDayOfMonth = getTheLastDayOfMonth(month);
        int lastDayOfWeek = lastDayOfMonth.get(Calendar.DAY_OF_WEEK);
        int nextNeedAddDays = Calendar.SATURDAY - lastDayOfWeek;

        final int days = getMonthTotalDays(month);

        int dayOfYear_Of_FirstDayOfMonth = firstDayOfMonth.get(Calendar.DAY_OF_YEAR);
        for (int i = -previousNeedAddDay; i < days + nextNeedAddDays; i++) {
            Calendar c = firstDayOfMonth;
            c.set(Calendar.DAY_OF_YEAR, dayOfYear_Of_FirstDayOfMonth + i);
            dateList.add(format(FORMAT_YYYY_MM_DD, c.getTimeInMillis()));
        }
        return dateList;
    }

    // 获取当月总天数
    public static int getMonthTotalDays(int month) {
        int firstDayOfYear = getTheFirstDayOfMonth(month).get(Calendar.DAY_OF_YEAR);
        int lastDayOfYear = getTheLastDayOfMonth(month).get(Calendar.DAY_OF_YEAR);
        int totalDays = lastDayOfYear - firstDayOfYear + 1;
        return totalDays;
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date_str 字符串日期
     * @param format   如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //时间戳字符串转化为数字
    public static long strToMills(String strMills) {
        long time = 0;
        if (!TextUtils.isEmpty(strMills)) {
            time = StringUtil.toLong(strMills);
            if (strMills.length() == 10) {
                time = time * 1000;
            }
        }
        return time;
    }


    //获取两天之间相隔多少天数 2017/8/2 Josie修改
    public static int getIntervalOfTwoDays(long startMills, long endMills) {
        int count = 0;
        long startTime = 0;
        long endTime = 0;
        if (endMills >= startMills) {
            startTime = startMills;
            endTime = endMills;
        } else {
            startTime = endMills;
            endTime = startMills;
        }
        Calendar startDay = Calendar.getInstance();
        startDay.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        startDay.setTimeInMillis(startTime);
        Calendar endDay = Calendar.getInstance();
        endDay.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        endDay.setTimeInMillis(endTime);
        //跨年
        if (endDay.get(Calendar.YEAR) != startDay.get(Calendar.YEAR)) {
            count = startDay.getActualMaximum(Calendar.DAY_OF_YEAR) - startDay.get(Calendar.DAY_OF_YEAR)
                    + endDay.get(Calendar.DAY_OF_YEAR) - endDay.getActualMinimum(Calendar.DAY_OF_YEAR) + 1;
        } else {
            count = Math.abs(endDay.get(Calendar.DAY_OF_YEAR) - startDay.get(Calendar.DAY_OF_YEAR));
        }
        return count;
    }

    //获得当天0点时间
    public static String getZeroTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return "" + (int) (cal.getTimeInMillis() / 1000);
    }

    //获得当天24点时间
    public static String getNight24Time() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return "" + (int) (cal.getTimeInMillis() / 1000);
    }

    public static Date getCurrentDate(long time) {
        time = strToMills(String.valueOf(time));
        return new Date(time);
    }

    /*
    * 获取两个日期之间间隔几个月
    * 2017/6/6 josie
    * */
    public static int getMonthBetweenDates(String dateStr1, String dateStr2, String format) {
        int monthValue = 0;

        long timeMillis1 = date2TimeStamp(dateStr1, format);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(timeMillis1);
        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);

        long timeMillis2 = date2TimeStamp(dateStr2, format);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(timeMillis2);
        int year2 = calendar2.get(Calendar.YEAR);
        int month2 = calendar2.get(Calendar.MONTH);

        if (year1 == year2) {
            monthValue = Math.abs(month1 - month2);
        } else {
            if (year1 > year2) {
                monthValue = (month1 + 1) + (11 - month2);
            } else {
                monthValue = (month2 + 1) + (11 - month1);
            }
        }

        return monthValue;
    }

    /*
    * 将整秒数转换为时分格式（HH : mm）
    * 2017/6/14 josie
    * */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                timeStr = "00" + ":" + unitFormat(minute);
            } else {
                hour = minute / 60;
                minute = minute % 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute);
            }
        }
        return timeStr;
    }

    /*
    * 将整秒数转换为时分格式（xx小时xx分钟）
    * 2017/7/27 josie
    * */
    public static List<Integer> secToTimelist(int time) {
        List<Integer> timelist = new ArrayList<>();
        int hour = 0;
        int minute = 0;
        if (time > 0) {
            minute = time / 60;
            if (minute >= 60) {
                hour = minute / 60;
                minute = minute % 60;
            }
        }
        timelist.add(hour);
        timelist.add(minute);
        return timelist;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static long getLastDayOfMonth(long timeMills) {
        timeMills = strToMills(String.valueOf(timeMills));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMills);
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, days);
        return calendar.getTimeInMillis();
    }

    /**
     * Description:获取当前日期指定样式字符串
     *
     * @param formatType 指定的日期样式
     * @return 当前日期指定样式字符串
     */
    public static String getCurrentTimeSpecifyFormat(String formatType) {
        Date date = new Date();

        return formatDate2String(date, formatType);
    }

    /**
     * @param date   日期对象
     * @param format 日期字符串样式
     * @return 日期字符串
     * @author: zhaguitao
     * @Title: formatDate2String
     * @Description: 将日期对象转换成日期字符串
     * @date: 2013-5-22 下午2:12:17
     */
    public static String formatDate2String(Date date, String format) {
        if (date == null) {
            return "";
        }

        try {
            SimpleDateFormat formatPattern = new SimpleDateFormat(format, Locale.getDefault());
            return formatPattern.format(date);
        } catch (Exception e) {
            return "";
        }
    }

}
