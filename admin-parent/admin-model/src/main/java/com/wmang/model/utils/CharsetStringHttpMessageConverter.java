package com.wmang.model.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.util.FileCopyUtils;

/**
 * Some times, direct String value should be returned by Action, and need set Charset .
 * since 1.0.2
 */
public class CharsetStringHttpMessageConverter  extends AbstractHttpMessageConverter<String> {
	private Charset defaultCharset ;
	private final List<Charset> availableCharsets;
	private boolean writeAcceptCharset = true;
	//增加日志输出处理，获取失败日志。add by pengjingya 2016-02-19
	private static final Logger logger = LoggerFactory.getLogger(CharsetStringHttpMessageConverter.class);
	
	public CharsetStringHttpMessageConverter() {
		this("UTF-8");
	}
	public CharsetStringHttpMessageConverter(String defaultCharsetName) {
		super(new MediaType("text", "plain", Charset.forName(defaultCharsetName)), MediaType.ALL);
		this.availableCharsets = new ArrayList<Charset>(Charset.availableCharsets().values());
		this.defaultCharset = Charset.forName(defaultCharsetName);
	}

	/**
	 * Indicates whether the {@code Accept-Charset} should be written to any outgoing request.
	 * <p>Default is {@code true}.
	 */
	public void setWriteAcceptCharset(boolean writeAcceptCharset) {
		this.writeAcceptCharset = writeAcceptCharset;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return String.class.equals(clazz);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected String readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException {
		Charset charset = getContentTypeCharset(inputMessage.getHeaders().getContentType());
		return FileCopyUtils.copyToString(new InputStreamReader(inputMessage.getBody(), charset));
	}

	@Override
	protected Long getContentLength(String s, MediaType contentType) {
		Charset charset = getContentTypeCharset(contentType);
		try {
			return (long) s.getBytes(charset.name()).length;
		}
		catch (UnsupportedEncodingException ex) {
			logger.error("获取内容长度报错", ex);
			throw new InternalError(ex.getMessage());
		}
	}

	@Override
	protected void writeInternal(String s, HttpOutputMessage outputMessage) throws IOException {
		if (writeAcceptCharset) {
			outputMessage.getHeaders().setAcceptCharset(getAcceptedCharsets());
		}
		Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
		FileCopyUtils.copy(s, new OutputStreamWriter(outputMessage.getBody(), charset));
	}

	/**
	 * Return the list of supported {@link Charset}.
	 * <p>By default, returns {@link Charset#availableCharsets()}. Can be overridden in subclasses.
	 * @return the list of accepted charsets
	 */
	protected List<Charset> getAcceptedCharsets() {
		return this.availableCharsets;
	}

	private Charset getContentTypeCharset(MediaType contentType) {
		if (contentType != null && contentType.getCharSet() != null) {
			return contentType.getCharSet();
		}else {
			return defaultCharset;
		}
	}

}

