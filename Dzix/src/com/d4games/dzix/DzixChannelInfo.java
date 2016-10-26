package com.d4games.dzix;

import java.net.InetSocketAddress;
import java.util.HashMap;

import org.jboss.netty.channel.Channel;

public class DzixChannelInfo {
	private Channel channel;
	long lastPacketTime;
	private String channelKey;
	private String handshakerUri;
	private HashMap<String, String> option = new HashMap<String, String>();

	public DzixChannelInfo(Channel channel) {
		this.setChannel(channel);
		channel.setAttachment(this);
		setLastPacketTime();
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void setLastPacketTime() {
		lastPacketTime = System.currentTimeMillis();
	}

	public long getIdleTime() {
		return System.currentTimeMillis() - lastPacketTime;
	}

	public String getChannelKey() {
		return channelKey;
	}

	public void setChannelKey(String channelKey) {
		this.channelKey = channelKey;
	}

	public String getRemoteIp() {
		InetSocketAddress addr = (InetSocketAddress) channel.getRemoteAddress();
		return addr.getAddress().toString();
	}

	public String getHandshakerUri() {
		return handshakerUri;
	}

	public void setHandshakerUri(String handshakerUri) {
		this.handshakerUri = handshakerUri;
	}

	public String getOption(String key) {
		return option.get(key);
	}

	public void setOption(String key, String value) {
		option.put(key, value);
	}
}
