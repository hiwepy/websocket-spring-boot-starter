package com.github.vindell.websocket.session.handler;

import com.github.vindell.websocket.event.WebSocketMessageEvent;
import com.github.vindell.websocket.session.handler.chain.HandlerChain;

public interface WebSocketMessageHandler<T extends WebSocketMessageEvent> {

	public void doHandler(T event, HandlerChain<T> handlerChain) throws Exception;
	
}
