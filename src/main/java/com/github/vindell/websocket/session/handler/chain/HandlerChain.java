package com.github.vindell.websocket.session.handler.chain;

import com.github.vindell.websocket.event.WebSocketMessageEvent;

public interface HandlerChain<T extends WebSocketMessageEvent>{

	void doHandler(T event) throws Exception;
	
}
