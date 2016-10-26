package com.d4games.dzix.model;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.strategicgains.restexpress.Response;

public class DzixHttpResponseT<T> {
	private int code = 200;
	private String desc = "OK";
	private T content;

	public DzixHttpResponseT(Response response, HttpResponseStatus status) {
		super();
		this.code = status.getCode();
		this.desc = status.getReasonPhrase();
		response.setResponseCode(this.code);
	}

	public DzixHttpResponseT(Response response, int code, String desc) {
		super();
		this.code = code;
		this.desc = desc;
		response.setResponseCode(this.code);
	}

	public DzixHttpResponseT(int code, String desc) {
		super();
		this.code = code;
		this.desc = desc;
	}

	public DzixHttpResponseT() {
		super();
	}

	public int getCode() {
		return code;
	}

	public DzixHttpResponseT<T> setCode(int code) {
		this.code = code;
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public DzixHttpResponseT<T> setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	public T getContent() {
		return content;
	}

	public DzixHttpResponseT<T> setContent(T content) {
		this.content = content;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DzixResponse [code=");
		builder.append(code);
		builder.append(", desc=");
		builder.append(desc);
		builder.append(", content=");
		builder.append(content);
		builder.append("]");
		return builder.toString();
	}

	public boolean isOK() {
		return getCode() == 200;
	}
}
