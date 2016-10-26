package com.d4games.dzix.protocol;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLEngine;

import com.d4games.dzix.DzixSslContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

/**
 * Provides a tiny DSL to define the pipeline features.
 * 
 * @author toddf
 * @since Aug 27, 2010
 */
public class HttpPipelineBuilder
implements ChannelPipelineFactory
{
	// SECTION: INSTANCE VARIABLES

	private List<ChannelHandler> requestHandlers = new ArrayList<ChannelHandler>();
	private ExecutionHandler executionHandler = null;
	private boolean useSsl = false;
    private String userAgent = "none";
    private String dzixDecoder = "none";
    private String dzixEncoder = "none";
    private List<String> dzixNoneCryptoIPList;

	
	// SECTION: CONSTRUCTORS

    public HttpPipelineBuilder(boolean useSsl)
    {
        super();
        this.useSsl = useSsl;
    }

    public HttpPipelineBuilder(boolean useSsl, String userAgent, String decoder, String encoder, List<String> dzixNoneCryptoIPList)
    {
        super();
        this.useSsl = useSsl;
        this.userAgent = userAgent;
        this.dzixDecoder = decoder;
        this.dzixEncoder = encoder;
        this.dzixNoneCryptoIPList = dzixNoneCryptoIPList;
    }

	
	// SECTION: BUILDER METHODS
	
	public HttpPipelineBuilder setExecutionHandler(ExecutionHandler handler)
	{
		this.executionHandler = handler;
		return this;
	}

	public HttpPipelineBuilder addRequestHandler(ChannelHandler handler)
	{
		if (!requestHandlers.contains(handler))
		{
			requestHandlers.add(handler);
		}

		return this;
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
		pipeline.addLast("aggregator", new HttpChunkAggregator(1000000));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("chunkWriter", new ChunkedWriteHandler());
		pipeline.addLast("inflater", new HttpContentDecompressor());
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("dzixDecoder", new DzixDecoderHandler(userAgent, dzixDecoder, dzixNoneCryptoIPList));
        pipeline.addLast("dzixEncoder", new DzixEncoderHandler(dzixEncoder, dzixNoneCryptoIPList));
		
		if (executionHandler != null)
		{
			pipeline.addLast("executionHandler", executionHandler);
		}

		for (ChannelHandler handler : requestHandlers)
		{
			pipeline.addLast(handler.getClass().getSimpleName(), handler);
		}

		return pipeline;
	}
}
