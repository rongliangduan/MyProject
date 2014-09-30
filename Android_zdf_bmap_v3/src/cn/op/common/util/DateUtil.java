package cn.op.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 日期格式化工具
 * 
 * @author lufei
 * 
 */
public class DateUtil {
	private static final String TAG = Log.makeLogTag(DateUtil.class);

	/**
	 * Date转字符串
	 * 
	 * @param date
	 *            日期对象
	 * @param format
	 *            指定格式
	 * @return 指定格式的日期字符串
	 */
	public static String date2Str(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 字符串转Date
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param format
	 *            指定格式
	 * @return Date or null
	 * 
	 */
	public static Date str2Date(String dateStr, String format) {
		try {
			return new SimpleDateFormat(format).parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final String DATE_FORMAT_DETAULT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_1 = "yyyyMMddHHmmss";

	/**
	 * Date转字符串
	 * 
	 * @param date
	 * @return 格式为yyyy-MM-dd HH:mm:ss
	 */
	public static String date2Str1(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DETAULT);
		return sdf.format(date);
	}

	/**
	 * 获取China地区日期时间
	 * 
	 * @return yyyy-MM-dd hh:mm:ss形的时间字符
	 */
	public static String getDate() {
		Date b = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_DETAULT,
				Locale.CHINESE);
		return formatter.format(b);
	}

	/**
	 * 获取China地区日期
	 * 
	 * @return yyyy-MM-dd形的时间字符
	 */
	public static String getDateOnly() {
		String date = getDate();
		String dateOnly = date.split(" ")[0];
		return dateOnly;
	}

	/**
	 * 获取China地区日期时间
	 * 
	 * @return yyyy年MM月dd日形的时间字符串
	 */
	public static String getDate(Date b) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日",
				Locale.CHINESE);
		return formatter.format(b);
	}

	/**
	 * 获取下一月的日期
	 * 
	 * @param currentDate
	 *            当前日期
	 * @return
	 */
	public static Date nextMonth(Date currentDate) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(currentDate);
		cal.add(GregorianCalendar.MONTH, 1);// 在月份上加1
		return cal.getTime();
	}

	/**
	 * 获取上一月的日期
	 * 
	 * @param currentDate
	 *            当前日期
	 * @return
	 */
	public static Date lastMonth(Date currentDate) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(currentDate);
		cal.add(GregorianCalendar.MONTH, -1);// 在月份上加1
		return cal.getTime();
	}

	/**
	 * 获取上一天的日期
	 * 
	 * @param currentDate
	 *            当前日期
	 * @return
	 */
	public static Date lastDay(Date currentDate) {
		GregorianCalendar cre = new GregorianCalendar();
		cre.setTime(currentDate);
		cre.add(GregorianCalendar.DAY_OF_MONTH, -1);// 天数加1
		return cre.getTime();
	}

	/**
	 * 获取下一天的日期
	 * 
	 * @param currentDate
	 * @return
	 */
	public static Date nextDay(Date currentDate) {
		GregorianCalendar cre = new GregorianCalendar();
		cre.setTime(currentDate);
		cre.add(GregorianCalendar.DAY_OF_MONTH, 1);// 天数减一
		return cre.getTime();
	}

	/**
	 * @param c
	 * @return E.g., at 10:04:15.250 PM the HOUR_OF_DAY is 22.
	 */
	public static int getHour(Calendar calendar) {
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		return hour;
	}

	/**
	 * @param c
	 * @return E.g., at 10:04:15.250 PM the HOUR_OF_DAY is 22.
	 */
	public static String getHourStr(Calendar calendar) {
		int hour = getHour(calendar);

		String str = "" + hour;
		if (hour - 10 < 0) {
			str = "0" + hour;
		}
		return str;
	}

	/**
	 * @return E.g., at 10:04:15.250 PM the MINUTE is 4.
	 */
	public static int getMinute(Calendar calendar) {
		int minute = calendar.get(Calendar.MINUTE);

		return minute;
	}

	/**
	 * @return E.g., at 10:04:15.250 PM the MINUTE is 04.
	 */
	public static String getMinuteStr(Calendar calendar) {

		int minute = getMinute(calendar);

		String minuteStr = "" + minute;
		if (minute - 10 < 0) {
			minuteStr = "0" + minute;
		}

		return minuteStr;
	}

	/**
	 * 返回中国月份
	 * 
	 * @param calendar
	 * @return
	 */
	public static int getMonth(Calendar calendar) {
		int month = calendar.get(Calendar.MONTH);
		return month + 1;
	}

	/**
	 * @return E.g., 02
	 */
	public static String getMonthStr(Calendar calendar) {

		int month = getMonth(calendar);

		String monthStr = "" + month;
		if (month - 10 < 0) {
			monthStr = "0" + month;
		}

		return monthStr;
	}

	public static int getDayOfMonth(Calendar calendar) {
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * @param calendar
	 * @return E.g., 03
	 */
	public static String getDayOfMonthStr(Calendar calendar) {
		int day = getDayOfMonth(calendar);

		String dayStr = "" + day;
		if (day - 10 < 0) {
			dayStr = "0" + day;
		}

		return dayStr;
	}

	public static int getYear(Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		return year;
	}

	/**
	 * yyyyMMddhhmmss形的时间字符
	 * 
	 * @return
	 */
	public static String getDate4Zdf() {
		Date b = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.CHINESE);
		String date = formatter.format(b);

		return date;
	}

	/**
	 * 判断次日起是否是当天
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isToday(Calendar c) {
		Calendar calendarTodayStart = getTodayStart();
		Calendar calendarTodayEnd = getTodayEnd();

		return c.after(calendarTodayStart) && c.before(calendarTodayEnd);
	}

	private static Calendar getTodayEnd() {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);

		return todayEnd;
	}

	private static Calendar getTodayStart() {
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);

		return todayStart;
	}

	/**
	 * 当前时间是否处于开始与结束时间之间
	 * 
	 * @param beginDate
	 * @param endTime
	 * @return
	 */
	public static boolean isLiveDate(String beginDate, String endTime) {

		if (StringUtils.isEmpty(beginDate) || StringUtils.isEmpty(endTime)) {
			return false;
		}

		long curt = StringUtils.toLong(DateUtil.getDate4Zdf());
		long begin = StringUtils.toLong(beginDate);
		long end = StringUtils.toLong(endTime);

		return curt - begin > 0 && curt - end < 0;
	}

}
