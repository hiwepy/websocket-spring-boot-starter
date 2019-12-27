package com.github.hiwepy.websocket.session.handler;

import java.util.List;

import com.github.hiwepy.websocket.event.WebSocketMessageEvent;
import com.github.hiwepy.websocket.session.handler.chain.HandlerChain;


public interface NamedHandlerList<T extends WebSocketMessageEvent> extends List<WebSocketMessageHandler<T>> {
	 
	/**
     * Returns the configuration-unique name assigned to this {@code Handler} list.
     * @return configuration-unique name assigned to this {@code Handler} list.
     */
    String getName();

    /**
     * Returns a new {@code HandlerChain<T>} instance that will first execute this list's {@code Handler}s (in list order)
     * and end with the execution of the given {@code handlerChain} instance.
     * @param handlerChain {@code HandlerChain} instance.
     * @return {@code HandlerChain} instance.
     */
    HandlerChain<T> proxy(HandlerChain<T> handlerChain);
    
}
