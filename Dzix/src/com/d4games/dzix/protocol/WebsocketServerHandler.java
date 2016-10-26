package com.d4games.dzix.protocol;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.lang.reflect.Constructor;

import com.d4games.dzix.DzixChannelInfo;
import com.d4games.dzix.DzixChannels;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.d4games.dzix.cmd.WsBinaryCommand;
import com.d4games.dzix.cmd.WsTextCommand;
import com.d4games.dzix.util.StringUtils;

/**
 * Handles handshakes and messages
 */
public class WebsocketServerHandler extends SimpleChannelUpstreamHandler {
	private static final Logger log = LoggerFactory.getLogger(WebsocketServerHandler.class);

	private static final String WEBSOCKET_PATH = "/dzix/";

	private WebSocketServerHandshaker handshaker;
	private String originHandshakerUri = "";
	private String handshakerUri = "";
	private Class<?> classWsTextCommand;
	private Class<?> classWsBinaryCommand;

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		DzixChannelInfo chInfo = (DzixChannelInfo) ctx.getChannel().getAttachment();
		chInfo.setLastPacketTime();

		Object msg = e.getMessage();
		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
		// Allow only GET methods.
		if (req.getMethod() != GET) {
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
			return;
		}

		// Send the favicon.ico
		if ("/favicon.ico".equals(req.getUri())) {
			HttpResponse res = new DefaultHttpResponse(HTTP_1_1, NOT_FOUND);
			sendHttpResponse(ctx, req, res);
			return;
		}

		// Check URI
		if (!req.getUri().startsWith(WEBSOCKET_PATH)) {
			HttpResponse res = new DefaultHttpResponse(HTTP_1_1, NOT_FOUND);
			sendHttpResponse(ctx, req, res);
			return;
		}

		int indexQueryString = req.getUri().indexOf('?');
		if (indexQueryString == -1)
			originHandshakerUri = req.getUri().substring(WEBSOCKET_PATH.length());
		else
			originHandshakerUri = req.getUri().substring(WEBSOCKET_PATH.length(), indexQueryString);

		handshakerUri = StringUtils.removeSpecialCharacters(originHandshakerUri);
		DzixChannels.getChannelInfo(ctx.getChannel()).setHandshakerUri(handshakerUri);

		// Handshake
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req),
				null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
		} else {
			handshaker.handshake(ctx.getChannel(), req).addListener(WebSocketServerHandshaker.HANDSHAKE_LISTENER);
		}
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
			return;
		}
		if (frame instanceof PingWebSocketFrame) {
			ctx.getChannel().write(new PongWebSocketFrame(frame.getBinaryData()));
			return;
		}
		if (frame instanceof TextWebSocketFrame) {
			onTextFrame(ctx, (TextWebSocketFrame) frame);
		}
		if (frame instanceof BinaryWebSocketFrame) {
			onBinaryFrame(ctx, (BinaryWebSocketFrame) frame);
		}
	}

	private void onBinaryFrame(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) {
		try {
			Constructor<?> constructor = WsBinaryCommand.getConstructor(classWsBinaryCommand);
			WsBinaryCommand instance = (WsBinaryCommand) constructor.newInstance(handshakerUri, ctx, frame);
			instance.queue();

		} catch (Throwable t) {
			log.error("Exception", t);
		} finally {
		}
	}

	private void onTextFrame(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
		try {
			Constructor<?> constructor = WsTextCommand.getConstructor(classWsTextCommand);
			WsTextCommand instance = (WsTextCommand) constructor.newInstance(handshakerUri, ctx, frame);
			instance.queue();

			// new WsTextCommand(handshakerUri, handshakerUri, ctx,
			// frame).execute();
		} catch (Throwable t) {
			log.error("Exception", t);
		} finally {
		}
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
		// Generate an error page if response status code is not OK (200).
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		log.info("Exception ", e.getCause());
		e.getChannel().close();
	}

	private static String getWebSocketLocation(HttpRequest req) {
		return "ws://" + req.getHeader(HOST) + WEBSOCKET_PATH;
	}

	public void setClassWsTextCommand(Class<?> classWsTextCommand) {
		this.classWsTextCommand = classWsTextCommand;
	}

	public void setClassWsBinaryCommand(Class<?> classWsBinaryCommand) {
		this.classWsBinaryCommand = classWsBinaryCommand;
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelOpen(ctx, e);

		DzixChannelInfo dzixChannelInfo = new DzixChannelInfo(ctx.getChannel());
		DzixChannels.addWs(handshakerUri, dzixChannelInfo.getChannel());
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// TODO Auto-generated method stub
		super.channelClosed(ctx, e);

		DzixChannels.removeWs(handshakerUri, ctx.getChannel());
	}
}
