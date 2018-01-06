package com.wmang.model.db.jedis;

import com.wmang.model.exception.BaseException;

public class RedisException extends BaseException {

	protected RedisException(String defineCode) {
		super(defineCode);
	}

	/**
	 * @Fields serialVersionUID :
	 */
	private static final long serialVersionUID = 3988229133456964977L;


}
