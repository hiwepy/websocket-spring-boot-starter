package com.github.vindell.websocket.handler;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.common.collect.Maps;
import com.github.vindell.websocket.annotation.SocketHandler;
import com.github.vindell.websocket.session.SessionFilter;
import com.github.vindell.websocket.session.SessionOthersFilter;
import com.github.vindell.websocket.utils.WebSocketUtils;

/**
 * 
 * @className	： BroadcastWebSocketsHandler
 * @description	： 无客户端限制的在线广播Socket连接Handler：用于向所有在线用户推送通知消息，一对多
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年12月4日 下午12:11:20
 * @version 	V1.0
 */
@SocketHandler("broadcast")
public class BroadcastWebSocketsHandler extends TextWebSocketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastWebSocketsHandler.class);
	private static Map<String, WebSocketSession> SESSION_MAP = Maps.newConcurrentMap();
	/** 在线人数统计 */
	private static AtomicInteger onlineCount = new AtomicInteger(0);
	private long onlineCountLimit = -1;
	private int messageSizeLimit = -1;
	private SessionFilter filter = SessionFilter.ALL;
	
	public BroadcastWebSocketsHandler(SessionFilter filter) {
		this.filter = filter;
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		if (!filter.matches(session)) {
			LOGGER.debug("[Socket Session filtered. ID : {} , RemoteAddress : {} , Uri : {} ].", session.getId(),
					session.getRemoteAddress().getHostString(), session.getUri().toString());
			return;
		}

		// 当在线数大于0，表示有限制
		if (getOnlineCountLimit() > 0 && onlineCount.get() > getOnlineCountLimit()) {

			CharBuffer welcome = CharBuffer.wrap(String.format("Socket Session online %s over limit %s.", onlineCount.get(), getOnlineCountLimit()));
			session.sendMessage(new TextMessage(welcome));
			LOGGER.debug("[Socket Session online {} over limit {}].", onlineCount.get(), getOnlineCountLimit());
			
			// 主动关闭当前连接
			if (session.isOpen()) {
				session.close();
			}
			
			return;
		}

		LOGGER.debug("[{} : {}] has be connected...", session.getUri(), session.getId());

		if (getMessageSizeLimit() > 0) {
			session.setBinaryMessageSizeLimit(getMessageSizeLimit());
			session.setTextMessageSizeLimit(getMessageSizeLimit());
		}

		if(SESSION_MAP.put(session.getId(), session) == null) {
			onlineCount.incrementAndGet();
		}
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		LOGGER.debug("Received Message: {}", message.getPayload());
		LOGGER.debug("Received Message Length: {}", message.getPayloadLength());
		
		// 将消息广播给其他在线客户端
		WebSocketUtils.broadcast(SESSION_MAP, new SessionOthersFilter(session), new TextMessage(message.getPayload() + ""));
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
		LOGGER.error("Connection Error : {}", throwable.getMessage());
		if (session.isOpen()) {
			session.close();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		if (filter.matches(session)) {
			LOGGER.debug("[{} : {} closed. {}]", session.getUri(), session.getId(), closeStatus.toString());
			if(SESSION_MAP.remove(session.getId()) != null) {
				onlineCount.decrementAndGet();
				LOGGER.info("Current Online Count: {}", onlineCount.get());
			}
		}
	}

	/**
	 * 给所有在线客户端群发消息
	 */
	public void broadcast(final TextMessage message) throws IOException {
		this.broadcast(filter, message);
	}

	/**
	 * 给过滤器筛选后的在线客户端群发消息
	 */
	public void broadcast(final SessionFilter filter, final TextMessage message) throws IOException {
		WebSocketUtils.broadcast(SESSION_MAP, filter, message);
	}

	public boolean hasConnection() throws IOException {
		return onlineCount.get() > 0;
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	public long getOnlineCountLimit() {
		return onlineCountLimit;
	}

	public void setOnlineCountLimit(long onlineCountLimit) {
		this.onlineCountLimit = onlineCountLimit;
	}

	public int getMessageSizeLimit() {
		return messageSizeLimit;
	}

	public void setMessageSizeLimit(int messageSizeLimit) {
		this.messageSizeLimit = messageSizeLimit;
	}

}
