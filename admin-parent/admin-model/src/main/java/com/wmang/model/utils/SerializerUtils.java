/**   
 * @Prject: css-common-util
 * @version: V1.0   
 */
package com.wmang.model.utils;

import java.io.Serializable;

import org.springframework.util.SerializationUtils;


/**
 * @Title: SerializerUtils
 * @Description: 将可序列化对象序列化或者
 * @author: Li Qiang
 * @date: 2016年3月15日 上午11:06:29
 */
public class SerializerUtils {
	/**
	 * 
	 * @Title: serialize 
	 * @Description: 序列化
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Serializable object) {
		return SerializationUtils.serialize(object);
	}
	/**
	 * 
	 * @Title: deserialize 
	 * @Description: 反序列化
	 * @param bytes
	 * @return
	 */
	public static Object deserialize(byte[] bytes) {
		return SerializationUtils.deserialize(bytes);
	}
}
