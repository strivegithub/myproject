package com.d4games.dzix.cmd;

import java.lang.reflect.Constructor;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.d4games.dzix.util.JsonHttpResponse;

/**
 * The obligatory "Hello World!" showing a simple implementation of a
 * {@link HystrixCommand}.
 */
public class HttpCommand extends HystrixCommand<JsonHttpResponse> {
	private static final Logger log = LoggerFactory.getLogger(HttpCommand.class);

	protected Request request;
	protected Response response;

	public HttpCommand(String groupName, Request request, Response response, int timeout) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupName)).andCommandPropertiesDefaults(
				HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(timeout)));

		this.request = request;
		this.response = response;
		log.debug("Requester IP : {}, URI : {}", request.getRemoteAddress().getAddress().getHostAddress(),
				request.getUrl());
	}

	public HttpCommand(String groupName, Request request, Response response) {
		this(groupName, request, response, 2000);
	}

	public static Constructor<?> getConstructor(Class<?> classHttpCommand) throws NoSuchMethodException,
			SecurityException {
		Class<?>[] constructorParameterTypes = { Request.class, Response.class };
		return classHttpCommand.getConstructor(constructorParameterTypes);
	}

	public JsonHttpResponse makeResponseBadRequest() {
		ChannelHandlerContext ctx = (ChannelHandlerContext) request.getAttachment("ctx");
		log.debug("BAD_REQUEST : {}", ctx.getChannel());
		return new JsonHttpResponse(response, HttpResponseStatus.BAD_REQUEST);
	}

	public JsonHttpResponse makeResponseNotImplemented() {
		ChannelHandlerContext ctx = (ChannelHandlerContext) request.getAttachment("ctx");
		log.debug("NOT_IMPLEMENTED : {}", ctx.getChannel());
		return new JsonHttpResponse(response, HttpResponseStatus.NOT_IMPLEMENTED);
	}

	@Override
	protected JsonHttpResponse run() {
		return makeResponseNotImplemented();
	}
}
