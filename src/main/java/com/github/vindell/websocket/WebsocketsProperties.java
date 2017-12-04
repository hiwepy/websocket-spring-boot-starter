package com.github.vindell.websocket;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(WebsocketsProperties.PREFIX)
public class WebsocketsProperties {

	public static final String PREFIX = "websocket";

	private long onlineCountLimit = -1;
	private int messageSizeLimit = -1;

	public long getOnlineCountLimit() {
		return onlineCountLimit;
	}

	public void setOnlineCountLimit(long onlineCountLimit) {
		this.onlineCountLimit = onlineCountLimit;
	}

	public int getMessageSizeLimit() {
		return messageSizeLimit;
	}

	public void setMessageSizeLimit(int messageSizeLimit) {
		this.messageSizeLimit = messageSizeLimit;
	}

}
