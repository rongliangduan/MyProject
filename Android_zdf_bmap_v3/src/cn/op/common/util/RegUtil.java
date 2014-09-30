package cn.op.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegUtil {
	public static void main(String[] args) {
		boolean legalUsername = RegUtil.isLegalPsw("123j567");
		System.out.println(legalUsername);
	}

	public static boolean isMobileNO(String mobiles) {
		// Pattern p =
		// Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(14[5,7]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isEmail(String email) {
		// String
		// str="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		String str = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 昵称合法验证，中英数字 ,str = "^[\u4E00-\u9FA5A-Za-z0-9_]+$"
	 * 
	 * @param alias
	 * @return
	 */
	public static boolean isLegalAlias(String alias) {
		// String
		// str="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		String str = "^[\u4E00-\u9FA5A-Za-z0-9_]+$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(alias);
		return m.matches();
	}

	/**
	 * 密码
	 * 长度大于6位小于16位，必须数字加字母组合，特殊字符只允许_ -，str = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z_-]{6,16}$";
	 * 6-20位数字或字母，str = "^[0-9A-Za-z]{6,20}$";
	 * @param username
	 * @return
	 */
	public static boolean isLegalPsw(String username) {
		String str = "^[0-9A-Za-z]{6,20}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(username);
		return m.matches();
	}

	/**
	 * 身份证
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean isIdCard(String idCard) {
		boolean result;

		// 15位或18位
		String str = "(^\\d{15}$/)|(\\d{17}(?:\\d|x|X)$)";
		Pattern p1 = Pattern.compile(str);
		Matcher matcher = p1.matcher(idCard);
		result = matcher.matches();
		if (!result)
			return result;

		// 生日
		Pattern p2 = Pattern.compile("//d{6}(//d{8}).*"); // 用于提取出生日字符串
		Pattern p3 = Pattern.compile("(//d{4})(//d{2})(//d{2})");// 用于将生日字符串进行分解为年月日

		Matcher matcher2 = p2.matcher(idCard);
		boolean b = matcher2.find();
		if (b) {
			String s = matcher2.group(1);
			Matcher matcher3 = p3.matcher(s);
			if (matcher3.find()) {
				// TODO 验证生日
				System.out.println("生日为" + matcher3.group(1) + "年"
						+ matcher3.group(2) + "月" + matcher3.group(3) + "日");
			}
		}

		return true;
	}
}