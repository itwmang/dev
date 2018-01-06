/**
 * 
 */
package com.wmang.model.utils;

/**
 * 转换java工具类
 * 
 * @author wmang
 *
 */
public class ConvertJavaUtil {
	/**
	 * 如：ORG_ID 转换成： orgId _ORG_ID_ 转换成： orgId
	 */
	public static String toJavaFirstLower(String str) {
		if (str == null || str.length() < 1) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		boolean flag = false; // true 当前字符为符号
		boolean first = false; // false 第一个字母未出现
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (isCharNum(c)) {
				if (flag && first) {
					sb.append(Character.toUpperCase(c));
				} else {
					sb.append(Character.toLowerCase(c));
				}
				;
				flag = false;
				if (!first) {
					first = true;
				}
			} else {
				flag = true;
			}
		}
		return sb.toString();
	}

	/**
	 * 如：T_SYM_ORG 转换成： TSymOrg _T_SYM_ORG_ 转换成： TSymOrg
	 */
	public static String toJavaFirstUpper(String str) {
		if (str == null || str.length() < 1) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		boolean flag = true;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (isCharNum(c)) {
				if (flag) {
					sb.append(Character.toUpperCase(c));
				} else {
					sb.append(Character.toLowerCase(c));
				}
				;
				flag = false;
			} else {
				flag = true;
			}
		}
		return sb.toString();
	}

	/**
	 * 如：orgId 转换成： ORG_ID
	 */
	public static String toDbUpper(String str) {
		if (str == null || str.length() < 1) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append("_" + c);
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 是否字母或者数字
	 */
	public static boolean isCharNum(char c) {
		if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String orgId = "orgIdfdsfsdIdkdfHdsd";
		System.out.println(ConvertJavaUtil.toDbUpper(orgId));
	}
}
