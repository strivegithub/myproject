package com.d4games.dzix.cmd;

import java.lang.reflect.Constructor;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
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
public class WsBinaryCommand extends HystrixCommand<Boolean> {
	private static final Logger log = LoggerFactory.getLogger(WsBinaryCommand.class);

	protected BinaryWebSocketFrame frame;
	protected ChannelHandlerContext ctx;
	protected String handshakerUri;

	public WsBinaryCommand(String handshakerUri, ChannelHandlerContext ctx, BinaryWebSocketFrame frame, int timeout) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(handshakerUri))
				.andCommandKey(HystrixCommandKey.Factory.asKey(handshakerUri))
				.andCommandPropertiesDefaults(
						HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(timeout)));

		this.frame = frame;
		this.ctx = ctx;
		this.handshakerUri = handshakerUri;
	}

	public WsBinaryCommand(String handshakerUri, ChannelHandlerContext ctx, BinaryWebSocketFrame frame) {
		this(handshakerUri, ctx, frame, 1000);
	}

	public static Constructor<?> getConstructor(Class<?> classWsTextCommand) throws NoSuchMethodException,
			SecurityException {
		Class<?>[] constructorParameterTypes = { String.class, ChannelHandlerContext.class, BinaryWebSocketFrame.class };
		return classWsTextCommand.getConstructor(constructorParameterTypes);
	}

	@Override
	protected Boolean run() {

		// Send the uppercase string back.
		ChannelBuffer request = ((BinaryWebSocketFrame) frame).getBinaryData();
		log.debug(String.format("uri {} Channel {} received {}", handshakerUri, ctx.getChannel().getId(), request));
		ctx.getChannel().write(new BinaryWebSocketFrame(request));

		return true;
	}
}
