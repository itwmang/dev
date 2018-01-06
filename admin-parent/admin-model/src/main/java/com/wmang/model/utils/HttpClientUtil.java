package com.wmang.model.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * @Description HttpPost请求
	 * @author shenz
	 */
	public static String postHttp(String url, Map<String, Object> paramMap) {
		String responseData = "";
		try {
			if (url == null || "".equals(url)) {
				logger.info("-----------http请求的URL地址为空-------------");
			} else {
				String requestParas = "";
				if (paramMap != null && paramMap.size() > 0) {
					// 处理请求参数
					Set<String> keySet = paramMap.keySet();
					Iterator<String> it = keySet.iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						String value = (String) paramMap.get(key);
						if ("".equals(requestParas)) {
							requestParas = key + "=" + value;
						} else {
							requestParas = requestParas + "&" + key + "=" + value;
						}
					}
				}
				logger.info("\n----------Http请求的URL：" + url + "----------");

				// 请求
				URL urlObj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				con.setRequestProperty("accept-language", "zh-cn");
				con.setRequestMethod("POST");
				con.setConnectTimeout(20000);
				con.setReadTimeout(20000);
				con.setDoOutput(true);
				con.setDoInput(true);
				con.connect();
				OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
				out.write(requestParas);
				out.flush();
				out.close();
				// 返回结果
				int code = con.getResponseCode();
				if (code >= 200 && code < 300) {
					BufferedReader bufRed = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
					StringBuffer buffer = new StringBuffer();
					String line;
					while ((line = bufRed.readLine()) != null) {
						buffer.append(line);
					}
					bufRed.close();
					if(con != null){
						con.disconnect();
					}
					responseData = buffer.toString();
					String reponselog = responseData;
					if(responseData != null && responseData.length() > 100){
						reponselog = responseData.substring(0, 100);
					}
					logger.info("\n--------Http请求返回结果：" + reponselog + "---------");
				} else {
					logger.info("\n--------Http请求失败,错误码:" + code + "----------");
				}
			}
		} catch (Exception e) {
			logger.error("\n---------Http请求异常----------\n" + e.toString());
			e.printStackTrace();
			// http请求异常
			responseData = "-1";
		}
		return responseData;
	}
	
	/**
	 * @Description HttpPost请求
	 * @author shenz
	 */
	public static String postHttp(Map<String, Object> paramMap,HttpURLConnection conn) {
		String responseData = "";
		try {
			String requestParas = "";
			if (paramMap != null && paramMap.size() > 0) {
				// 处理请求参数
				Set<String> keySet = paramMap.keySet();
				Iterator<String> it = keySet.iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					String value = (String) paramMap.get(key);
					if ("".equals(requestParas)) {
						requestParas = key + "=" + value;
					} else {
						requestParas = requestParas + "&" + key + "=" + value;
					}
				}
			}
			String parasLog = requestParas;
			if(parasLog != null && parasLog.length() > 100){
				parasLog = parasLog.substring(0, 100);
			}
			logger.info("\n----------Http请求的参数：" + parasLog + " ......");
			// 获取输出流
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(requestParas);
			out.flush();
			out.close();
			// 返回结果
			int code = conn.getResponseCode();
			if (code >= 200 && code < 300) {
				BufferedReader bufRed = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = bufRed.readLine()) != null) {
					buffer.append(line);
				}
				bufRed.close();
				if(conn != null){
					conn.disconnect();
				}
				responseData = buffer.toString();
				String reponselog = responseData;
				if(responseData != null && responseData.length() > 100){
					reponselog = responseData.substring(0, 100);
				}
				logger.info("\n--------Http请求返回结果：" + reponselog + "---------");
			} else {
				logger.info("\n--------Http请求失败,错误码:" + code + "----------");
			}
		} catch (Exception e) {
			// http请求异常
			responseData = "-1";
			logger.error("\n---------Http请求异常----------\n" + e.toString());
		}
		return responseData;
	}
	
	/**
	 * @Description 获取Http连接
	 * @author shenz
	 */
	public static HttpURLConnection getUrlConn(String url, int connTimeout) {
		HttpURLConnection conn = null;
		if(ValueUtil.isEmpty(url)){
			logger.info("\n--------- 获取HttpURLConnection连接异常,url为空");
		}else{
			try {
				URL urlObj = new URL(url);
				conn = (HttpURLConnection) urlObj.openConnection();
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("accept-language", "zh-cn");
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(connTimeout);
				conn.setReadTimeout(20000);
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.connect();
			} catch (Exception e) {
				conn = null;
				logger.error("\n--------- 获取HttpURLConnection连接异常," + url + ";" + e.toString());
			}
		}
		return conn;
	}
	
	/**
	 * @Description Http请求Webservice
	 * @author shenz
	 */
	public static String postHttp(String xmlParas, HttpURLConnection httpconn) {
		String responseData = "";
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xmlParas.getBytes("GBK"));
			byte[] b = bout.toByteArray();
			OutputStream out = httpconn.getOutputStream();
			out.write(b);
			out.flush();
			out.close();
			// 返回结果
			int code = httpconn.getResponseCode();
			if (code >= 200 && code < 300) {
				BufferedReader bufRed = new BufferedReader(new InputStreamReader(httpconn.getInputStream(), "UTF-8"));
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = bufRed.readLine()) != null) {
					buffer.append(line);
				}
				bufRed.close();
				if(httpconn != null){
					httpconn.disconnect();
				}
				responseData = buffer.toString();
				String reponselog = responseData;
				if(responseData != null && responseData.length() > 100){
					reponselog = responseData.substring(0, 100);
				}
				logger.info("\n--------Http-ws请求返回结果：" + reponselog + "---------");
			} else {
				logger.info("\n--------Http-ws请求失败,错误码:" + code + "----------");
			}
		} catch (Exception e) {
			// http请求异常
			responseData = "-1";
			logger.error("\n---------Http-ws请求异常----------\n" + e.toString());
		}
		return responseData;
	}
	
}
