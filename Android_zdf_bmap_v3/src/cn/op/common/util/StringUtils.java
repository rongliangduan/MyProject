package cn.op.common.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 字符串操作工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	private final static SimpleDateFormat dateFormater = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final String REG_EX_NO_CHINESE = "[0-9a-zA-Z/\\:*?？，<>【】……#,。!！%$_'\\^\\-\\+\\.\\(\\)|\"\n\t\\s]";
	private static final String TAG = StringUtils.class.getSimpleName();

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 将日期类型转为字符串
	 * 
	 * @return
	 */
	public static String getNow() {
		return dateFormater.format(new Date());
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.format(cal.getTime());
		String paramDate = dateFormater2.format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = dateFormater2.format(time);
		}
		return ftime;
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.format(today);
			String timeDate = dateFormater2.format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 判断给定字符串是否为空或空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 。若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input) || "null".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 -1
	 */
	public static int toInt(Object obj) {
		if (obj == null) {
			Log.e(TAG, "======toInt====== obj=null");
			return -1;
		}
		return toInt(obj.toString(), -1);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 转double
	 * 
	 * @param obj
	 * @return 转换异常返回 0.0
	 */
	public static Double toDouble(String obj) {
		try {
			return Double.parseDouble(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	/**
	 * 转double
	 * 
	 * @param obj
	 * @return 转换异常返回 0.0
	 */
	public static Float toFloat(String obj) {
		try {
			return Float.parseFloat(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0f;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 1. 处理特殊字符 2. 去除后缀名带来的文件浏览器的视图凌乱(特别是图片更需要如此类似处理，否则有的手机打开图库，全是我们的缓存图片)
	 * 
	 * @param url
	 * @return
	 */
	public static String replaceUrlWithPlus(String url) {

		if (url != null) {
			return url.replaceAll("http://(.)*?/", "")
					.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
		}
		return null;
	}

	/**
	 * 去除字符串中的空格、回车、换行符、制表符，将其替换为单个空格 注：\n 回车( ) \t 水平制表符( ) \s 空格(\u0008) \r
	 * 换行( )
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = null;
		if (str != null) {
			Pattern p = Pattern.compile("\\s+|\t|\r|\n");
			Matcher m = p.matcher(str.trim());
			dest = m.replaceAll(" ");
		}
		return dest;
	}

	/**
	 * 解密后转换为int
	 * 
	 * @param str
	 * @return 转换异常返回 -1
	 */
	public static int toIntAfterDecrypt(String str) {

		str = DesUtils.decrypt(str);

		return toInt(str);
	}

	/**
	 * 解密后转换为 double
	 * 
	 * @param str
	 * @return 转换异常返回 0.0
	 */
	public static double toDoubleAfterDecrypt(String str) {
		str = DesUtils.decrypt(str);
		return toDouble(str);
	}

	/**
	 * 将float装换为 价格 格式字符串，蹦哦流量为小数， 52.0===》52.00；52.012===》52.01；52.017===》52.01
	 * 
	 * @param price
	 * @return
	 */
	public static String toPriceString(float price) {

		price = RoundTool.round(price, 2, BigDecimal.ROUND_DOWN);

		String priceStr = String.valueOf(price);

		int length = priceStr.substring(priceStr.indexOf(".") + 1).length();

		if (length == 1) {
			priceStr += "0";
		}

		return priceStr;
	}

	/**
	 * 将string装换为 价格 格式字符串，蹦哦流量为小数，
	 * 52.00===》52.0；52.012===》52.01；52.017===》52.01
	 * 
	 * @param price
	 * @return
	 */
	public static float toPriceFloat(String priceStr) {

		Float float1 = toFloat(priceStr);

		String priceString = StringUtils.toPriceString(float1);

		Float priceFloat = toFloat(priceString);

		return priceFloat;
	}

	/**
	 * 对字符串进行过滤，只能输入汉字，过滤特殊字符，过滤a-z A-Z 0-9 / \ : * ? < > # - + . ( ) | " 空格
	 * 
	 * @param editable
	 * @return
	 */
	public static String stringFilterChinese(String str) {
		String regEx = REG_EX_NO_CHINESE;
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		return m.replaceAll("");
	}

	/**
	 * 对字符串进行过滤，过滤特殊字符，过滤 / \ : * ? < > # - + . ( ) | " 空格
	 * 
	 * @param str
	 * @return 过滤后的字符串
	 * @throws PatternSyntaxException
	 */
	public static String stringFilter(String str) throws PatternSyntaxException {
		String regEx = "[/\\:*?？，<>【】……#,。!！%$_'\\^\\-\\+\\.\\(\\)|\"\n\t\\s]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		return m.replaceAll("");
	}

	/**
	 * 是否只包含汉字
	 * 
	 * @param chineseStr
	 * @return
	 */
	public static boolean isOnlyChinese(String chineseStr) {

		String regEx = REG_EX_NO_CHINESE;
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(chineseStr);

		return !m.find();
	}
}