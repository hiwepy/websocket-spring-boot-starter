package com.github.hiwepy.websocket.session.handler;

import com.github.hiwepy.websocket.event.WebSocketMessageEvent;
import com.github.hiwepy.websocket.session.handler.chain.HandlerChain;

public interface WebSocketMessageHandler<T extends WebSocketMessageEvent> {

	public void doHandler(T event, HandlerChain<T> handlerChain) throws Exception;
	
}
