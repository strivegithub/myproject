package com.d4games.dzix;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DzixChannels {
	INSTANCE;

	private static final Logger log = LoggerFactory.getLogger(DzixChannels.class);

	private static final ChannelGroup httpChannels = new DefaultChannelGroup("http");
	// private static final Map<String, ChannelGroup> wsChannels = new
	// ConcurrentHashMap<String, ChannelGroup>();
	private static final ChannelGroup wsChannels = new DefaultChannelGroup("ws");
	private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static int channelValidPeriod = 300000;

	public static void addHttp(Channel channel) {
		httpChannels.add(channel);
	}

	public static void removeHttp(Channel channel) {
		httpChannels.remove(channel);
	}

	public static void addWs(String handshakerUri, Channel channel) {
		wsChannels.add(channel);
		/*
		 * ChannelGroup channelGroup = wsChannels.get(handshakerUri); if
		 * (channelGroup != null) return channelGroup;
		 * 
		 * synchronized(wsChannels) { channelGroup = new DefaultChannelGroup();
		 * wsChannels.put(handshakerUri, channelGroup); return channelGroup; }
		 */
	}

	public static void removeWs(String handshakerUri, Channel channel) {
		wsChannels.remove(channel);
	}

	public static int getChannelValidPeriod() {
		return channelValidPeriod;
	}

	public static void setChannelValidPeriod(int channelValidPeriod) {
		DzixChannels.channelValidPeriod = channelValidPeriod;
	}

	public static DzixChannelInfo getChannelInfo(Channel channel) {
		if (channel == null)
			return null;
		return (DzixChannelInfo) channel.getAttachment();
	}

	public static void startChannelPruning() {
		final Runnable beeper = new Runnable() {
			int stepSize = 5000;
			int cursor = 0;

			public void run() {
				if (wsChannels.size() <= 0)
					return;
				log.debug("wsChannels size : {}", wsChannels.size());

				Channel[] channels = wsChannels.toArray(new Channel[0]);
				int start = cursor * stepSize;
				int end = Math.min(start + stepSize, channels.length);
				for (int i = start; i < end; ++i) {
					try {
						Channel channel = channels[i];
						DzixChannelInfo chInfo = (DzixChannelInfo) channel.getAttachment();
						if (chInfo.getIdleTime() > getChannelValidPeriod()) {
							channel.close();
							log.debug("channel close {}", channel.getId());
						}
					} catch (Exception e) {
						log.debug("Exception", e);
					}
				}

				if (end >= channels.length)
					cursor = 0;
				else
					++cursor;
			}
		};

		scheduler.scheduleAtFixedRate(beeper, 1, 1, SECONDS);
	}
}
