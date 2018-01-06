package com.wmang.model.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueUtil {
	private static DecimalFormat DecimalFormatter = new DecimalFormat("#.######");
	@SuppressWarnings("unused")
	private static Pattern datetimepattern = Pattern.compile(
			"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([2][0-3])|([1][0-9]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
	@SuppressWarnings("unused")
	private static Pattern datepattern = Pattern.compile(
			"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))");

	// 增加日志输出处理，获取失败日志。add by pengjingya 2016-02-18
	private static final Logger logger = LoggerFactory.getLogger(ValueUtil.class);

	// 工具类不应该有公共的构造器，使用私有的构造器来隐藏公共的构造器.add by pengjingya 2016-02-19
	private ValueUtil() {
	}

	/**
	 * 对象转字符串
	 * 
	 * @author: 陈志光
	 * @date: 2014年11月20日上午10:32:48
	 */
	public static String toStrNull(Object value) {
		if (null == value) {
			return null;
		}
		if (value instanceof Double || value instanceof Float) {
			return DecimalFormatter.format(value);
		}
		return value.toString().trim();
	}

	/**
	 * 返回字符串，为空则返回"" Description:
	 * 
	 * @author: 陈志光
	 * @date: 2014年11月20日上午10:32:48
	 */
	public static String toStr(Object value) {
		if (null == value) {
			return "";
		}
		if (value instanceof Double || value instanceof Float) {
			return DecimalFormatter.format(value);
		}
		return value.toString().trim();
	}

	public static Byte toByte(Object value) {
		if (null == value) {
			return 0;
		}
		Byte val = 0;
		if (value instanceof String) {
			try {
				val = Byte.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("String转换成Byte报错", e);
			}
			if (null == val) {
				try {
					val = Float.valueOf(value.toString()).byteValue();
				} catch (Exception er) {
					logger.error("String转换成Float报错", er);
					return 0;
				}
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).byteValue();
			return val;
		}
		if (value instanceof Number) {
			val = ((Number) value).byteValue();
			return val;
		}

		return 0;
	}

	public static Byte toByteNull(Object value) {
		if (null == value) {
			return null;
		}
		Byte val = 0;
		if (value instanceof String) {
			try {
				val = Byte.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("String转换成Byte报错", e);
			}
			if (null == val) {
				try {
					val = Float.valueOf(value.toString()).byteValue();
				} catch (Exception er) {
					logger.error("String转换成Float报错", er);
					return null;
				}
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).byteValue();
			return val;
		}
		if (value instanceof Number) {
			val = ((Number) value).byteValue();
			return val;
		}

		return null;
	}

	/**
	 * 返回整数，为空则返回null Description:
	 * 
	 * @author: 陈志光
	 * @date: 2014年11月20日上午10:32:48
	 */
	public static Integer toIntNull(Object value) {
		if (null == value) {
			return null;
		}
		Integer val = null;
		if (value instanceof String) {
			try {
				val = Integer.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("String转换成Integer报错", e);
			}
			if (null == val) {
				try {
					val = Float.valueOf(value.toString()).intValue();
				} catch (Exception er) {
					logger.error("String转换成Float报错", er);
					return null;
				}
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).intValue();
			return val;
		}
		if (value instanceof Number) {
			val = ((Number) value).intValue();
			return val;
		}

		return null;
	}

	/**
	 * 返回整数，为空则返回0 Description:
	 * 
	 * @author: 陈志光
	 * @date: 2014年11月20日上午10:32:48
	 */
	public static Integer toInt(Object value) {
		if (null == value) {
			return 0;
		}
		Integer val = 0;
		if (value instanceof String) {
			try {
				val = Integer.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("String转换成Integer报错", e);
			}
			if (null == val) {
				try {
					val = Float.valueOf(value.toString()).intValue();
				} catch (Exception er) {
					logger.error("String转换成Float报错", er);
					return 0;
				}
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).intValue();
			return val;
		}
		if (value instanceof Number) {
			val = ((Number) value).intValue();
			return val;
		}

		return 0;
	}

	public static Float toFloatNull(Object value) {
		if (null == value) {
			return null;
		}
		Float val = null;
		if (value instanceof String) {
			try {
				val = Float.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("String转换成Float报错", e);
				return null;
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).floatValue();
			return val;
		}
		if (value instanceof Number) {
			val = ((Number) value).floatValue();
			return val;
		}

		return null;
	}

	public static Double toDoubleNull(Object value) {
		if (null == value) {
			return null;
		}
		Double val = null;
		if (value instanceof String) {
			try {
				val = Double.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("String转换成Double报错", e);
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).doubleValue();
			return val;
		}
		if (value instanceof Number) {
			val = ((Number) value).doubleValue();
			return val;
		}

		return val;
	}

	public static Double toDouble(Object value) {
		if (null == value) {
			return 0d;
		}
		Double val = 0d;
		if (value instanceof String) {
			try {
				val = Double.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("String转换成Double报错", e);
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).doubleValue();
			return val;
		}
		if (value instanceof Number) {
			val = ((Number) value).doubleValue();
			return val;
		}

		return val;
	}

	public static Long toLongNull(Object value) {
		if (null == value) {
			return null;
		}
		Long val = null;
		if (value instanceof String) {
			try {
				val = Long.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("String转换成Long报错", e);
			}
			if (null == val) {
				try {
					val = Double.valueOf(value.toString()).longValue();
				} catch (Exception er) {
					logger.error("String转换成Double报错", er);
				}
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).longValue();
			return val;
		}
		if (value instanceof Number) {
			val = ((Number) value).longValue();
			return val;
		}

		return val;
	}

	public static Long toLong(Object value) {
		if (null == value) {
			return 0l;
		}
		Long val = 0l;
		if (value instanceof String) {
			try {
				val = Long.valueOf(value.toString());
			} catch (Exception e) {
				logger.error("String转换成Long报错", e);
			}
			if (null == val) {
				try {
					val = Double.valueOf(value.toString()).longValue();
				} catch (Exception er) {
					logger.error("String转换成Double报错", er);
				}
			}
			return val;
		}
		if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).longValue();
			return val;
		}
		if (value instanceof Number) {
			val = ((Number) value).longValue();
			return val;
		}

		return val;
	}

	/**
	 * @Description 转换BigDecimal
	 * @author 陈志光
	 */
	public static BigDecimal toBigDecimalNull(Object value) {
		if (null == value) {
			return null;
		}

		try {
			BigDecimal deciaml = new BigDecimal(value + "");

			return deciaml;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 判断是否相等
	 */
	public static boolean equal(Object value1, Object value2) {
		if (null == value1 || null == value2) {
			return false;
		}
		return value1.equals(value2);
	}

	// 浮点型判断
	public static boolean isDecimal(String str) {
		if (null == str || "".equals(str))
			return false;
		Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
		return pattern.matcher(str).matches();
	}

	// 整型判断
	public static boolean isInteger(String str) {
		if (null == str)
			return false;
		Pattern pattern = Pattern.compile("[0-9]+");
		return pattern.matcher(str).matches();
	}

	public static String lpad(String str, int n, String padStr) {
		if (str.length() >= n) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		while (sb.length() < n - str.length()) {
			sb.append(padStr);
		}
		sb.append(str);
		return sb.toString();
	}

	public static String rpad(String str, int n, String padStr) {
		if (str.length() >= n) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() < n) {
			sb.append(padStr);
		}
		return sb.toString();
	}

	/**
	 * 判断对象是否为空，字符串，集合，数组 Description:
	 * 
	 * @author: 陈志光
	 * @date: 2014年11月20日上午10:32:48
	 */

	public static boolean isEmpty(Object obj) {
		if (null == obj) {
			return true;
		}
		if (obj instanceof String) {
			return ((String) obj).trim().length() == 0;
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) (obj)).size() == 0;
		}
		if (obj instanceof Object[]) {
			return ((Object[]) (obj)).length == 0;
		}
		return false;
	}

	/**
	 * @Description: 比较
	 * @author: 陈志光
	 * @date: 2014年11月20日上午10:32:48
	 */
	public static int compare(Object value1, Object value2) {
		if (value1 instanceof String) {
			return ValueUtil.toStr(value1).compareTo(ValueUtil.toStr(value2));
		}
		if (value1 instanceof Number) {
			return ValueUtil.toDouble(value1).compareTo(ValueUtil.toDouble(value2));
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
	}
}
