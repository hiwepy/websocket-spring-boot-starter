package com.github.hiwepy.websocket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.hiwepy.websocket.property.WebSocketHandlerProperties;

@ConfigurationProperties(WebSocketProperties.PREFIX)
public class WebSocketProperties {

	public static final String PREFIX = "websocket";

	/** Whether Enable Websocket Stomp. */
	private boolean enabled = false;
	/** Websocket Handler. */
	private List<WebSocketHandlerProperties> handlers = new ArrayList<WebSocketHandlerProperties>();
	/** Enable SockJS fallback options. */
	private boolean withSockJs = true;

	private long onlineLimit = -1;
	private int messageSize = -1;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<WebSocketHandlerProperties> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<WebSocketHandlerProperties> handlers) {
		this.handlers = handlers;
	}

	public boolean isWithSockJs() {
		return withSockJs;
	}

	public void setWithSockJs(boolean withSockJs) {
		this.withSockJs = withSockJs;
	}

	public long getOnlineLimit() {
		return onlineLimit;
	}

	public void setOnlineLimit(long onlineLimit) {
		this.onlineLimit = onlineLimit;
	}

	public int getMessageSize() {
		return messageSize;
	}

	public void setMessageSize(int messageSize) {
		this.messageSize = messageSize;
	}


}
