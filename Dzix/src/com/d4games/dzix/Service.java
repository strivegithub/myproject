package com.d4games.dzix;

import java.net.InetSocketAddress;
import java.util.List;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.ConfigurationManager;
import com.strategicgains.restexpress.settings.ServerSettings;
import com.strategicgains.restexpress.settings.SocketSettings;
import com.strategicgains.restexpress.util.Bootstraps;
import com.strategicgains.restexpress.util.LogLevel;

public class Service {

	private static final Logger log = LoggerFactory.getLogger(Service.class);

	protected static final ChannelGroup allChannels = new DefaultChannelGroup("Dzix");

	public static final String DEFAULT_NAME = "Dzix";
	public static final int DEFAULT_PORT = 10080;

	private String protocol = "no";
	private ServerBootstrap bootstrap;
	private SocketSettings socketSettings = new SocketSettings();
	private ServerSettings serverSettings = new ServerSettings();
	private LogLevel logLevel = LogLevel.DEBUG; // Netty default
	private boolean sslEnable = false;
    private String userAgent = "none";
    private String dzixDecoder = "none";
    private String dzixEncoder = "none";
    private List<String> dzixNoneCryptoIPList;

	/**
	 * Create a new RestExpress service. By default, RestExpress uses port 8081.
	 * Supports JSON, and XML, providing JSEND-style wrapped responses. And
	 * displays some messages on System.out. These can be altered with the
	 * setPort(), noJson(), noXml(), noSystemOut(), and useRawResponses() DSL
	 * modifiers, respectively, as needed.
	 * 
	 * <p/>
	 * The default input and output format for messages is JSON. To change that,
	 * use the setDefaultFormat(String) DSL modifier, passing the format to use
	 * by default. Make sure there's a corresponding SerializationProcessor for
	 * that particular format. The Format class has the basics.
	 * 
	 * <p/>
	 * This DSL was created as a thin veneer on Netty functionality. The bind()
	 * method simply builds a Netty pipeline and uses this builder class to
	 * create it. Underneath the covers, RestExpress uses Google GSON for JSON
	 * handling and XStream for XML processing. However, both of those can be
	 * swapped out using the putSerializationProcessor(String,
	 * SerializationProcessor) method, creating your own instance of
	 * SerializationProcessor as necessary.
	 *
     */
	public Service() {
		super();
		setName(DEFAULT_NAME);
	}

	/**
	 * Get the name of this RestExpress service.
	 * 
	 * @return a String representing the name of this service suite.
	 */
	public String getName() {
		return serverSettings.getName();
	}

	/**
	 * Set the name of this RestExpress service suite.
	 * 
	 * @param name
	 *            the name.
	 * @return the RestExpress instance to facilitate DSL-style method chaining.
	 */
	public Service setName(String name) {
		serverSettings.setName(name);
		return this;
	}

	public int getPort() {
		return serverSettings.getPort();
	}

	public Service setPort(int port) {
		serverSettings.setPort(port);
		return this;
	}

	public boolean UseSsl() {
		return sslEnable;
	}

	public Service setSslEnable(boolean sslEnable) {
		this.sslEnable = sslEnable;
		if (sslEnable) {
			setProtocol(getProtocol() + "S");
		}
		return this;
	}

	public boolean useTcpNoDelay() {
		return socketSettings.useTcpNoDelay();
	}

	public Service setUseTcpNoDelay(boolean useTcpNoDelay) {
		socketSettings.setUseTcpNoDelay(useTcpNoDelay);
		return this;
	}

	public boolean useKeepAlive() {
		return serverSettings.isKeepAlive();
	}

	public Service setKeepAlive(boolean useKeepAlive) {
		serverSettings.setKeepAlive(useKeepAlive);
		return this;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public Service setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
		return this;
	}

	public boolean shouldReuseAddress() {
		return serverSettings.isReuseAddress();
	}

	public Service setReuseAddress(boolean reuseAddress) {
		serverSettings.setReuseAddress(reuseAddress);
		return this;
	}

	public int getSoLinger() {
		return socketSettings.getSoLinger();
	}

	public Service setSoLinger(int soLinger) {
		socketSettings.setSoLinger(soLinger);
		return this;
	}

	public int getReceiveBufferSize() {
		return socketSettings.getReceiveBufferSize();
	}

	public Service setReceiveBufferSize(int receiveBufferSize) {
		socketSettings.setReceiveBufferSize(receiveBufferSize);
		return this;
	}

	public int getConnectTimeoutMillis() {
		return socketSettings.getConnectTimeoutMillis();
	}

	public Service setConnectTimeoutMillis(int connectTimeoutMillis) {
		socketSettings.setConnectTimeoutMillis(connectTimeoutMillis);
		return this;
	}

	/**
	 * Return the number of requested NIO/HTTP-handling worker threads.
	 * 
	 * @return the number of requested worker threads.
	 */
	public int getIoThreadCount() {
		return serverSettings.getIoThreadCount();
	}

	/**
	 * Set the number of NIO/HTTP-handling worker threads. This value controls
	 * the number of simultaneous connections the application can handle.
	 * 
	 * The default (if this value is not set, or set to zero) is the Netty
	 * default, which is 2 times the number of processors (or cores).
	 * 
	 * @param value
	 *            the number of desired NIO worker threads.
	 * @return the RestExpress instance.
	 */
	public Service setIoThreadCount(int value) {
		if (value > 0) {
			serverSettings.setIoThreadCount(value);
		}
		return this;
	}

	/**
	 * Returns the number of background request-handling (executor) threads.
	 * 
	 * @return the number of executor threads.
	 */
	public int getExecutorThreadCount() {
		return serverSettings.getExecutorThreadPoolSize();
	}

	/**
	 * Set the number of background request-handling (executor) threads. This
	 * value controls the number of simultaneous blocking requests that the
	 * server can handle. For longer-running requests, a higher number may be
	 * indicated.
	 * 
	 * For VERY short-running requests, a value of zero will cause no background
	 * threads to be created, causing all processing to occur in the NIO
	 * (front-end) worker thread.
	 * 
	 * @param value
	 *            the number of executor threads to create.
	 * @return the RestExpress instance.
	 */
	public Service setExecutorThreadCount(int value) {
		serverSettings.setExecutorThreadPoolSize(value);
		return this;
	}

	public Service setHystrixThreadPool(int value) {
		if (value > 0) {
			ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.coreSize", value);
		}
		return this;
	}

	public Channel bind() {
		return bind((getPort() > 0 ? getPort() : DEFAULT_PORT));
	}

	/**
	 * The last call in the building of a RestExpress server, bind() causes
	 * Netty to bind to the listening address and process incoming messages.
	 * 
	 * @return Channel
	 */
	public Channel bind(int port) {
		setPort(port);

		// Configure the server.
		if (getIoThreadCount() == 0) {
			bootstrap = Bootstraps.createServerNioBootstrap();
		} else {
			bootstrap = Bootstraps.createServerNioBootstrap(getIoThreadCount());
		}

		bootstrap.setPipelineFactory(CreateChannelPipelineFactory());
		setBootstrapOptions();

		log.error("Starting " + protocol + " Service on port " + port);

		Channel channel = bootstrap.bind(new InetSocketAddress(port));
		allChannels.add(channel);
		return channel;
	}

	protected ChannelPipelineFactory CreateChannelPipelineFactory() {
		log.error("Not Implemented CreateChannelPipelineFactory");

		return null;
	}

	private void setBootstrapOptions() {
		bootstrap.setOption("child.tcpNoDelay", useTcpNoDelay());
		bootstrap.setOption("child.keepAlive", serverSettings.isKeepAlive());
		bootstrap.setOption("reuseAddress", shouldReuseAddress());
		bootstrap.setOption("child.soLinger", getSoLinger());
		bootstrap.setOption("connectTimeoutMillis", getConnectTimeoutMillis());
		bootstrap.setOption("receiveBufferSize", getReceiveBufferSize());
	}

	/**
	 * Releases all resources associated with this server so the JVM can
	 * shutdown cleanly. Call this method to finish using the server. To utilize
	 * the default shutdown hook in main() provided by RestExpress, call
	 * awaitShutdown() instead.
	 */
	public void shutdown() {
		ChannelGroupFuture future = allChannels.close();
		future.awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

    public Service setMaxQueueSize(int queueSize) {
        if( queueSize > 0)
            ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.maxQueueSize", queueSize);

        return this;
    }

    public Service setQueueSizeRejectionThreshold(int queueSizeRejectionThreshold) {
        if( queueSizeRejectionThreshold > 0)
            ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.queueSizeRejectionThreshold", queueSizeRejectionThreshold);

        return this;
    }

    // Dzix 디코더 이름을 설정한다.
    public Service setDzixDecoderName(String decoder) {
        dzixDecoder = decoder;

        return this;
    }

    public String getDzixDecoderName() {
        return dzixDecoder;
    }

    // Dzix 인코더 이름을 설정한다.
    public Service setDzixEncoderName(String encoder) {
        dzixEncoder = encoder;

        return this;
    }

    public String getDzixEncoderName() {
        return dzixEncoder;
    }

    // User Agent 이름을 설정한다.
    public String getUserAgent() {
        return userAgent;
    }

    public Service setUserAgent(String userAgent) {
        this.userAgent = userAgent;

        return this;
    }

    public List<String> getDzixNoneCryptoIpList() {
        return dzixNoneCryptoIPList;
    }

    public void setDzixNoneCryptoIpList(List<String> dzixNoneCryptoIPList) {
        this.dzixNoneCryptoIPList = dzixNoneCryptoIPList;
    }
}
