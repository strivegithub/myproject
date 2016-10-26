package com.d4games.dzix.protocol;

import javax.net.ssl.SSLEngine;

import com.d4games.dzix.DzixSslContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;

/**
 * Provides a tiny DSL to define the pipeline features.
 * 
 * @author toddf
 * @since Aug 27, 2010
 */
public class WebsocketPipelineBuilder
implements ChannelPipelineFactory
{
	// SECTION: INSTANCE VARIABLES

	private boolean useSsl = false;
	private Class<?> classWsTextCommand;
    private Class<?> classWsBinaryCommand;
    
	// SECTION: CONSTRUCTORS

	public WebsocketPipelineBuilder(boolean useSsl, Class<?> classWsTextCommand, Class<?> classWsBinaryCommand)
	{
		super();
		this.useSsl = useSsl;
		this.classWsTextCommand = classWsTextCommand;
		this.classWsBinaryCommand = classWsBinaryCommand;
	}

	// SECTION: CHANNEL PIPELINE FACTORY

	@Override
	public ChannelPipeline getPipeline()
	throws Exception
	{
		ChannelPipeline pipeline = Channels.pipeline();

		if (useSsl) {
			SSLEngine engine = DzixSslContext.getInstance().serverContext().createSSLEngine();
	        engine.setUseClientMode(false);
	        pipeline.addLast("ssl", new SslHandler(engine));
		}

		pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        WebsocketServerHandler wsHandler = new WebsocketServerHandler();
        wsHandler.setClassWsTextCommand(classWsTextCommand);
        wsHandler.setClassWsBinaryCommand(classWsBinaryCommand);
        pipeline.addLast("handler", wsHandler);

		return pipeline;
	}
}
