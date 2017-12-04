package com.github.vindell.websocket.session.handler;

import com.github.vindell.websocket.event.WebSocketMessageEvent;

/**
 * 给Handler设置路径
 */
public interface PathProcessor<T extends WebSocketMessageEvent> {
	
	WebSocketMessageHandler<T> processPath(String path);

}
