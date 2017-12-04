/**
 * <p>Coyright (R) 2014 正方软件股份有限公司。<p>
 */
package com.github.vindell.websocket.session;

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
