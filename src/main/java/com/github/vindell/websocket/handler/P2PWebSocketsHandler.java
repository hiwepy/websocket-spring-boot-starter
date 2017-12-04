package com.github.vindell.websocket.handler;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 
 * @className	： P2PWebSocketsHandler
 * @description	： 在线提醒Socket连接： 用于用户主页面的通知信息，一对一
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年12月4日 下午12:12:02
 * @version 	V1.0
 */
public class P2PWebSocketsHandler extends TextWebSocketHandler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 在线人数统计
	 */
	private static AtomicInteger onlineCount = new AtomicInteger(0);

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info("new connection...current online count: {}", onlineCount.incrementAndGet());
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		logger.info("message received: {}", message);
		session.sendMessage(message);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		logger.info("one connection closed...current online count: {}", onlineCount.decrementAndGet());
	}

	@Override
	public boolean supportsPartialMessages() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
