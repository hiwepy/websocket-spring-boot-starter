package com.github.vindell.websocket.event;

import java.util.EventObject;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@SuppressWarnings("serial")
public class WebSocketMessageEvent extends EventObject {

	/** System time when the event happened */
	private final long timestamp;
	/** WebSocket Session*/
	private WebSocketSession session;
	/** WebSocket message*/
	private WebSocketMessage<?> message;
	/** Route Expression*/
	private String routeExpression;
	
	
	public WebSocketMessageEvent(WebSocketSession session, WebSocketMessage<?> message) {
		super(message);
		this.session = session;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
		this.routeExpression = session.getUri().getPath();
	}

	/**
	 * Return the system time in milliseconds when the event happened.
	 */
	public final long getTimestamp() {
		return this.timestamp;
	}

	public WebSocketSession getSession() {
		return session;
	}
	
	public WebSocketMessage<?> getMessage() {
		return message;
	}

	public String getRouteExpression() {
		return routeExpression;
	}

	public void setRouteExpression(String routeExpression) {
		this.routeExpression = routeExpression;
	}

}
