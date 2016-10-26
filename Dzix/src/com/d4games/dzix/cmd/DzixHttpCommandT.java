package com.d4games.dzix.cmd;

import java.lang.reflect.Constructor;

import com.d4games.dzix.model.DzixHttpResponseT;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;

/**
 * The obligatory "Hello World!" showing a simple implementation of a
 * {@link HystrixCommand}.
 * 
 * @param <T>
 * 
 * @param <T>
 */
public class DzixHttpCommandT<T> extends HystrixCommand<DzixHttpResponseT<T>> {
	private static final Logger log = LoggerFactory.getLogger(DzixHttpCommandT.class);
	protected Request request;
	protected Response response;

	protected String qid = "request_id";
	protected String src = "playerKey_or_serverIp";

	public DzixHttpCommandT(String groupName, Request request, Response response, int timeout) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupName)).andCommandPropertiesDefaults(
				HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(timeout)));

		this.request = request;
		this.response = response;
		this.qid = request.getHeader("qid");
		this.src = request.getHeader("src");
	}

	public DzixHttpCommandT(String groupName, Request request, Response response) {
		this(groupName, request, response, 2000);
	}

	public DzixHttpCommandT(Request request, Response response) {
		this("dzix", request, response, 2000);
	}

	public static Constructor<?> getConstructor(Class<?> classHttpCommand) throws NoSuchMethodException,
			SecurityException {
		Class<?>[] constructorParameterTypes = { Request.class, Response.class };
		return classHttpCommand.getConstructor(constructorParameterTypes);
	}

	public DzixHttpResponseT<T> makeResponseBadRequest() {
		ChannelHandlerContext ctx = (ChannelHandlerContext) request.getAttachment("ctx");
		log.debug("BAD_REQUEST : {}", ctx.getChannel());
		return new DzixHttpResponseT<T>(response, HttpResponseStatus.BAD_REQUEST);
	}

	public DzixHttpResponseT<T> makeResponseNotImplemented() {
		ChannelHandlerContext ctx = (ChannelHandlerContext) request.getAttachment("ctx");
		log.debug("NOT_IMPLEMENTED : {}", ctx.getChannel());
		return new DzixHttpResponseT<T>(response, HttpResponseStatus.NOT_IMPLEMENTED);
	}

	@Override
	protected DzixHttpResponseT<T> run() {
		return makeResponseNotImplemented();
	}

	@Override
	protected DzixHttpResponseT<T> getFallback() {
		Throwable e = this.getFailedExecutionException();
		log.error(e.toString(), e.getCause());
		return new DzixHttpResponseT<T>(response, HttpResponseStatus.SERVICE_UNAVAILABLE);
	}
}
