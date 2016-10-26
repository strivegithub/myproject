package com.d4games.dzix.cmd;

import java.lang.reflect.Constructor;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

/**
 * The obligatory "Hello World!" showing a simple implementation of a
 * {@link HystrixCommand}.
 */
public class WsTextCommand extends HystrixCommand<Boolean> {
	private static final Logger log = LoggerFactory.getLogger(WsTextCommand.class);

	protected TextWebSocketFrame frame;
	protected ChannelHandlerContext ctx;
	protected String handshakerUri;

	public WsTextCommand(String group, String cmd, String handshakerUri, ChannelHandlerContext ctx,
			TextWebSocketFrame frame, int timeout) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(group))
				.andCommandKey(HystrixCommandKey.Factory.asKey(cmd))
				.andCommandPropertiesDefaults(
						HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(timeout)));

		this.frame = frame;
		this.ctx = ctx;
		this.handshakerUri = handshakerUri;
	}

	public WsTextCommand(String handshakerUri, ChannelHandlerContext ctx, TextWebSocketFrame frame, int timeout) {
		this(handshakerUri, handshakerUri, handshakerUri, ctx, frame, timeout);
	}

	public WsTextCommand(String handshakerUri, ChannelHandlerContext ctx, TextWebSocketFrame frame) {
		this(handshakerUri, ctx, frame, 1000);
	}

	public static Constructor<?> getConstructor(Class<?> classWsTextCommand) throws NoSuchMethodException,
			SecurityException {
		Class<?>[] constructorParameterTypes = { String.class, ChannelHandlerContext.class, TextWebSocketFrame.class };
		return classWsTextCommand.getConstructor(constructorParameterTypes);
	}

	@Override
	protected Boolean run() {

		// Send the uppercase string back.
		String request = ((TextWebSocketFrame) frame).getText();
		log.debug(String.format("uri {} Channel {} received {}", handshakerUri, ctx.getChannel().getId(), request));
		ctx.getChannel().write(new TextWebSocketFrame(request));

		return true;
	}
}
