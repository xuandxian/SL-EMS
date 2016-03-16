package com.overtech.ems.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期工具类
 * 
 */
public class DateTools {
	// 常用日期输出格式
	public final static String YYYYMMDD = "yyyyMMdd";
	public final static String YYYY_MM_DD = "yyyy-MM-dd";
	public final static String YYYY_M_D = "yyyy-M-d";
	public final static String YYYYMMDDHHMM = "yyyyMMddHHmm";
	public final static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	public final static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public final static String HHMMSS = "HHmmss";
	public final static String HH_MM_SS = "HH:mm:ss";
	public final static String MM_SS = "MM月dd日";
	public final static String YYYY_MM_DD_EE = "yyyy-MM-dd EE";
	public final static String YYYY_MM_DD_EEEE = "yyyy-MM-dd EEEE";

	public static final SimpleDateFormat DATE_FORMAT_DEFAULT = new SimpleDateFormat(
			YYYY_MM_DD_HH_MM_SS, Locale.getDefault());
	public static final SimpleDateFormat DATE_FORMAT_DATE_YYYY_MM_DD = new SimpleDateFormat(
			YYYY_MM_DD, Locale.getDefault());
	public static final SimpleDateFormat DATE_FORMAT_MM_SS_ = new SimpleDateFormat(
			MM_SS, Locale.getDefault());
	public static final SimpleDateFormat DATE_FORMAT_YYYY_M_D = new SimpleDateFormat(
			YYYY_M_D, Locale.getDefault());
	public static final SimpleDateFormat DATE_FORMAT_YYYY_MM_DD_EE = new SimpleDateFormat(
			YYYY_MM_DD_EE, Locale.getDefault());
	public static final SimpleDateFormat DATE_FORMAT_YYYY_MM_DD_EEEE = new SimpleDateFormat(
			YYYY_MM_DD_EEEE, Locale.getDefault());

	/**
	 * 获取相差时间 字符串描述 xx小时xx分
	 * 
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	public static String getHoursMinute2String(Calendar cal1, Calendar cal2) {
		long gap = (cal2.getTimeInMillis() - cal1.getTimeInMillis())
				/ (1000 * 60);// 间隔分钟
		if (gap < 0) {
			gap = gap + 1000 * 60 * 60 * 24;
		}
		String tip = "";

		if (gap >= 60) {  
			long l2 = gap / (60);
			long l3 = gap % 60;
			if (l3 > 0) {
				tip = l3 + "分钟";
			}

			tip = l2 + "小时" + tip;

		} else if (gap > 0) {
			tip = gap + "分钟";
		}

		return tip;
	}

	/**
	 * 
	 * 输入一个时间，获取该时间的时间戳
	 * 
	 * @param @param dateString
	 * @param @return
	 * @param @throws ParseException
	 */
	public static long string2Timestamp(String dateString)
			throws ParseException {
		Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.parse(dateString);
		long temp = date1.getTime();// JAVA的时间戳长度是13位
		return temp;
	}

	/**
	 * 获取yyyy.MM.dd HH:mm:ss格式的当前时间
	 */
	public static String getNowDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		String date = format.format(c.getTime());
		return date;
	}

	/**
	 * yyyy-MM-dd HH:mm:ss 中获取 yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(String date) {
		Date d = new Date();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat ymdDate = new SimpleDateFormat("yyyy-MM-dd");
		try {
			d = sdfDate.parse(date);
		} catch (ParseException e) {
			return date;
		}

		return ymdDate.format(d);
	}

	/**
	 * 获取日期 时分秒为0
	 * 
	 * @return
	 */
	public static Calendar getCalendar() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.CHINA);
		format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		Calendar c = Calendar.getInstance();
		String dateString = format.format(c.getTime());
		try {
			c.setTime(format.parse(dateString));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public static Calendar getCalendar(Calendar calendar) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = (Calendar) calendar.clone();
		String dateString = format.format(c.getTime());
		try {
			c.setTime(format.parse(dateString));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 将1900-01-01 07:00:00格式化为07:00
	 * 
	 * @param strTime
	 * @return 07:00格式
	 */
	public static String formatTime(String strTime) {
		SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = f1.parse(strTime);
			f1 = new SimpleDateFormat("HH:mm");
			strTime = f1.format(date);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return strTime;
	}

	/**
	 * 获取相差天数
	 * 
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	public static int getBetween(Calendar cal1, Calendar cal2) {
		long gap = (cal2.getTimeInMillis() - cal1.getTimeInMillis())
				/ (1000 * 3600 * 24);// 从间隔毫秒变成间隔天数
		return (int) gap;
	}

	/**
	 * 获取相差天数 去除后面的时分秒 获取相差天数
	 * 
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	public static int getBetween2(Calendar cal1, Calendar cal2) {
		long gap = (getCalendar((Calendar) cal2.clone()).getTimeInMillis() - getCalendar(
				(Calendar) cal1).getTimeInMillis())
				/ (1000 * 3600 * 24);// 从间隔毫秒变成间隔天数
		return (int) gap;
	}

	public static boolean isToday(Calendar cal) {
		boolean ret = false;
		Calendar tmp = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String s1 = format.format(cal.getTime());
		String s2 = format.format(tmp.getTime());
		if (s1.equals(s2)) {
			ret = true;
		}
		return ret;
	}

	public static boolean isExpired(String searchStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		boolean ret = false;
		Calendar cur = DateTools.getCalendar();
		Calendar search = DateTools.getCalendar();
		try {
			search.setTime(format.parse(searchStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (search.before(cur)) {
			ret = true;
		}
		return ret;
	}

	/**
	 * yyyy-MM-dd HH:mm:ss 字符串格式的两个时间的减法
	 * 
	 * @param str1
	 *            减数
	 * @param str2
	 *            被减数
	 * @return 返回毫秒级别的时间单位
	 */
	public static long getTimeTwoString(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = df.parse(str1);
			d2 = df.parse(str2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long time = d1.getTime() - d2.getTime();
		return time;
	}

	/**
	 * 把毫秒基本的时间格式转化为 hh:mm:ss 格式的时间
	 * 
	 * @param diff
	 * @return
	 */
	public static String twoDate(long diff) {
		long days = diff / (1000 * 60 * 60 * 24);
		long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
				* (1000 * 60 * 60))
				/ (1000 * 60);
		long miao = (diff - days * (100 * 60 * 60 * 24) - hours
				* (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);
		String str;
		if (String.valueOf(hours).length() == 1) {
			str = "0" + hours + ":";
		} else {
			str = hours + ":";
		}
		if (String.valueOf(minutes).length() == 1) {
			str = str + "0" + minutes + ":";
		} else {
			str = str + minutes + ":";
		}
		if (String.valueOf(miao).length() == 1) {
			str = str + "0" + miao;
		} else {
			str = str + miao;
		}
		return str;
	}

	/**
	 * 根据日期返回对应的周几，含今天、明天、后天逻辑，如果日期是当天返回”今天“
	 * 
	 * @param calendar
	 * @return like “今天” or “周一”
	 */
//	public static String getWeek(Calendar calendar) {
//		String week = "";
//		week = Utilities.getTodayFromDate(calendar.getTime());
//		if (TextUtils.isEmpty(week)) {
//			String[] weekDaysName = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
//			week = weekDaysName[calendar.get(Calendar.DAY_OF_WEEK)];
//		}
//		return week;
//	}

}
