package com.github.hiwepy.websocket.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.common.collect.Maps;
import com.github.hiwepy.websocket.session.SessionFilter;

public class WebSocketUtils {

	/*
	 * 筛选过滤器匹配的WebSocketSession
	 */
	public static Map<String, WebSocketSession> sessions(final Map<String, WebSocketSession> sessionMap,final SessionFilter filter) throws IOException {
		Map<String, WebSocketSession> filterSessionMap = Maps.newConcurrentMap();
		Iterator<Entry<String, WebSocketSession>> ite = sessionMap.entrySet().iterator();
		while (ite.hasNext()) {
			Entry<String, WebSocketSession> entry = ite.next();
			WebSocketSession session = entry.getValue();
			if(filter.matches(session)) {
				filterSessionMap.put(entry.getKey(), session);
			}
		}
		return filterSessionMap;
	}
	
	/*
	 * 给所有在线客户端群发消息
	 */
	public static void broadcast(final Map<String, WebSocketSession> sessionMap,final TextMessage message) throws IOException {
		broadcast(sessionMap, SessionFilter.ALL, message);
	}
	
	/*
	 * 给过滤器筛选后的在线客户端群发消息
	 */
	public static void broadcast(final Map<String, WebSocketSession> sessionMap,final SessionFilter filter,final TextMessage message) throws IOException {
		Iterator<Entry<String, WebSocketSession>> ite = sessionMap.entrySet().iterator();
		while (ite.hasNext()) {
			Entry<String, WebSocketSession> entry = ite.next();
			WebSocketSession session = entry.getValue();
			if(filter.matches(session)) {
				if (session.isOpen()) {
					try {
						if (session.isOpen()) {
							session.sendMessage(message);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/*
	 * 给某个client发送消息
	 *
	 * @param session
	 * @param message
	 */
	public static void sendMessageToUser(WebSocketSession session, TextMessage message) throws IOException {
		if (session != null && session.isOpen()) {
			session.sendMessage(message);
		}
	}

	/*
	 * 给所有在线client发送消息 这里的message是client推送给服务端的请求信息，这里假设服务器推
	 * 送系统当前时间给client，忽略client传过来的消息
	 * 
	 * @param message
	 */
	public static void sendMessageToUsers(Map<String, WebSocketSession> sessionMap, TextMessage message)
			throws IOException {
		Iterator<Entry<String, WebSocketSession>> ite = sessionMap.entrySet().iterator();
		while (ite.hasNext()) {
			Entry<String, WebSocketSession> entry = ite.next();
			WebSocketSession session = entry.getValue();
			sendMessageToUser(session, message);
		}
	}

	/*
	 * 给所有在线client发送消息 这里的message是client推送给服务端的请求信息，这里假设服务器推
	 * 送系统当前时间给client，忽略client传过来的消息
	 * 
	 * @param message
	 */
	public static void sendMessageToUsers(CopyOnWriteArraySet<WebSocketSession> sessionSets, SessionFilter filter, TextMessage message)
			throws IOException {
		for (WebSocketSession session : sessionSets) {
			sendMessageToUser(session, message);
		}
	}
}
