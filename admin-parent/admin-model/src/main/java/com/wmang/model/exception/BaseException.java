/**   
 * Copyright © 2016  东软集团. All rights reserved.
 * 
 * @Title: BaseException.java 
 * @Prject: css-web-core
 * @Package: com.neusoft.cs.core.exceptions 
 * @Description: TODO
 * @author: Li Qiang   
 * @date: 2016年3月8日 下午9:07:49 
 * @version: V1.0   
 */
package com.wmang.model.exception;

/**
 * @Title: 异常基础类
 * @Description: 用来接收个模块自定义异常信息
 */
public class BaseException extends RuntimeException {

	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description: TODO
	 */
	private static final long serialVersionUID = 914360918539343267L;

	private String errorCode;

	private String errorMsg;

	public BaseException() {
		super();
	}

	/**
	 * @Title:BaseException
	 * @Description:TODO
	 * @param errorCode
	 * @param errorMsg
	 */
	public BaseException(String errorCode, String errorMsg) {
		super("errorCode[" + errorCode + "],errorMsg[" + errorMsg + "]");
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	/**
	 * @Title:BaseException
	 * @Description:TODO
	 * @param errorCode
	 */
	public BaseException(String errorCode) {
		super("errorCode[" + errorCode + "]");
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 *            the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/*
	 * (non Javadoc)
	 * 
	 * @Title: toString
	 * 
	 * @Description: TODO
	 * 
	 * @return
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseException [errorCode=" + errorCode + ", errorMsg=" + errorMsg + "]";
	}

}
