package com.github.hiwepy.websocket.session;

import org.springframework.web.socket.WebSocketSession;

public interface SessionFilter {

	public SessionFilter ALL = new SessionFilter() {
		
		@Override
		public boolean matches(WebSocketSession session) {
			return true;
		}
	};
	
	boolean matches(WebSocketSession session);
	
}
