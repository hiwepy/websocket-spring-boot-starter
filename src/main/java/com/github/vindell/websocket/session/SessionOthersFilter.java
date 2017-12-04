/**
 * <p>Coyright (R) 2014 正方软件股份有限公司。<p>
 */
package com.github.vindell.websocket.session;

import org.springframework.web.socket.WebSocketSession;

public class SessionOthersFilter implements SessionFilter {

	private WebSocketSession session;
	
	public SessionOthersFilter(WebSocketSession session) {
		this.session = session;
	}
	
	@Override
	public boolean matches(WebSocketSession sessionItem) {
		return !sessionItem.getId().equals(session.getId());
	}
	
}
