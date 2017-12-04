package com.github.vindell.websocket.session.handler.chain;

import com.github.vindell.websocket.event.WebSocketMessageEvent;

public interface HandlerChainResolver<T extends WebSocketMessageEvent> {

	HandlerChain<T> getChain(T event , HandlerChain<T> originalChain);
	
}
