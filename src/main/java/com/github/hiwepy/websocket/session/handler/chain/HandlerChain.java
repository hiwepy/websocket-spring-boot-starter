package com.github.hiwepy.websocket.session.handler.chain;

import com.github.hiwepy.websocket.event.WebSocketMessageEvent;

public interface HandlerChain<T extends WebSocketMessageEvent>{

	void doHandler(T event) throws Exception;
	
}
