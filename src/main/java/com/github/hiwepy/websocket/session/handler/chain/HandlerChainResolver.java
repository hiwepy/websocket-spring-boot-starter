package com.github.hiwepy.websocket.session.handler.chain;

import com.github.hiwepy.websocket.event.WebSocketMessageEvent;

public interface HandlerChainResolver<T extends WebSocketMessageEvent> {

	HandlerChain<T> getChain(T event , HandlerChain<T> originalChain);
	
}
