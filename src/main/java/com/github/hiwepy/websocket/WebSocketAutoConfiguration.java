package com.github.hiwepy.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;

import com.github.hiwepy.websocket.annotation.SocketConsumer;
import com.github.hiwepy.websocket.annotation.SocketHandler;
import com.github.hiwepy.websocket.config.Ini;
import com.github.hiwepy.websocket.event.WebSocketMessageEvent;
import com.github.hiwepy.websocket.handler.BroadcastWebSocketsHandler;
import com.github.hiwepy.websocket.handler.MessageEventWebSocketHandler;
import com.github.hiwepy.websocket.interceptor.HandshakeSessionInterceptor;
import com.github.hiwepy.websocket.property.WebSocketHandlerProperties;
import com.github.hiwepy.websocket.property.WebsocketSockJSProperties;
import com.github.hiwepy.websocket.session.SessionFilter;
import com.github.hiwepy.websocket.session.handler.Nameable;
import com.github.hiwepy.websocket.session.handler.WebSocketMessageHandler;
import com.github.hiwepy.websocket.session.handler.chain.HandlerChainManager;
import com.github.hiwepy.websocket.session.handler.chain.def.DefaultHandlerChainManager;
import com.github.hiwepy.websocket.session.handler.chain.def.PathMatchingHandlerChainResolver;
import com.github.hiwepy.websocket.utils.StringUtils;
 
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ WebSocketHandlerRegistry.class })
@ConditionalOnProperty(prefix = WebSocketProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ WebSocketProperties.class, ServerProperties.class })
@EnableWebSocket
public class WebSocketAutoConfiguration implements WebSocketConfigurer, ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(WebSocketAutoConfiguration.class);
	private ApplicationContext applicationContext;
	
	@Autowired
	private WebSocketProperties properties;
	@Autowired
	private ServerProperties serverProperties;
	@Autowired
	private HandshakeHandler handshakeHandler;
	@Autowired(required = false)
	private SockJsMessageCodec sockJsMessageCodec;
	
	@Bean
	@ConditionalOnMissingBean
	public SessionFilter sessionFilter() {
		return SessionFilter.ALL;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@Bean
	public HandshakeSessionInterceptor handshakeSessionInterceptor() {
		return new HandshakeSessionInterceptor();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public HandshakeHandler handshakeHandler() {
		return new DefaultHandshakeHandler();
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = WebSocketProperties.PREFIX, value = "withSockJS", havingValue = "true")
	public SockJsMessageCodec sockJsMessageCodec() {
		return new Jackson2SockJsMessageCodec();
	}
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

		List<WebSocketHandlerProperties> handlers = properties.getHandlers();
		if(CollectionUtils.isEmpty(handlers)) {
			return;
		}
		
		// 查找上下文中定义的WebSocketHandler实现；并通过组件方式进行标记
		Map<String, WebSocketHandler> beansOfHandler = getApplicationContext().getBeansOfType(WebSocketHandler.class);
		Map<String, WebSocketHandler> handlerMap =  new LinkedHashMap<String, WebSocketHandler>();
		if (!ObjectUtils.isEmpty(beansOfHandler)) {
			Iterator<Entry<String, WebSocketHandler>> ite = beansOfHandler.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, WebSocketHandler> entry = ite.next();
				SocketHandler annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), SocketHandler.class);
				if(annotationType == null) {
					// 注解为空，则打印错误信息
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", SocketHandler.class, entry.getValue().getClass(), entry.getKey());
				} else {
					handlerMap.put(annotationType.value(), entry.getValue());
				}
			}
		}
		
		Map<String, HandshakeInterceptor> beansOfType = getApplicationContext().getBeansOfType(HandshakeInterceptor.class);
		for (WebSocketHandlerProperties handlerProperties : handlers) {
			
			// 通过参数指定的WebSocketHandler必须是已经定义过的
			if(StringUtils.hasText(handlerProperties.getHandler()) && handlerMap.containsKey(handlerProperties.getHandler())) {
				
				WebSocketHandlerRegistration registration = registry.addHandler(handlerMap.get(handlerProperties.getHandler()), StringUtils.tokenizeToStringArray(handlerProperties.getPaths()))
						// setAllowedOrigins() 方法表示允许连接的域名
						.setAllowedOrigins(handlerProperties.getAllowedOrigins()).setHandshakeHandler(handshakeHandler);

				if (!ObjectUtils.isEmpty(beansOfType)) {
					HandshakeInterceptor[] interceptors = beansOfType.values().toArray(new HandshakeInterceptor[beansOfType.size()]);
					registration.addInterceptors(interceptors);
				}
				
				if (properties.isWithSockJs()) {

					WebsocketSockJSProperties sockJSProperties = handlerProperties.getSockjs();

					// withSockJS()方法表示支持以SockJS方式连接服务器。
					SockJsServiceRegistration sockJs = registration.withSockJS();

					sockJs.setClientLibraryUrl(sockJSProperties.getClientLibraryUrl());
					sockJs.setDisconnectDelay(sockJSProperties.getDisconnectDelay());
					sockJs.setHeartbeatTime(sockJSProperties.getHeartbeatTime());
					sockJs.setHttpMessageCacheSize(sockJSProperties.getHttpMessageCacheSize());
					sockJs.setMessageCodec(sockJsMessageCodec);
					sockJs.setSessionCookieNeeded(sockJSProperties.isSessionCookieNeeded());
					sockJs.setStreamBytesLimit(sockJSProperties.getStreamBytesLimit());
					sockJs.setSupressCors(sockJSProperties.isSuppressCors());
					sockJs.setWebSocketEnabled(sockJSProperties.isWebSocketEnabled());

				}
				
			}
			
		}

	}
	
	@Bean
	public BroadcastWebSocketsHandler broadcastWebSocketsHandler(SessionFilter filter) {
		
		BroadcastWebSocketsHandler socketsHandler = new BroadcastWebSocketsHandler(filter);
		
		socketsHandler.setOnlineCountLimit(properties.getOnlineLimit());
		socketsHandler.setMessageSizeLimit(properties.getMessageSize());
		
		return socketsHandler;
		
	}
	
	@Bean
	public MessageEventWebSocketHandler messageEventWebSocketHandler(WebSocketProperties properties,
			Map<String, WebSocketMessageHandler<WebSocketMessageEvent>> webSocketMessageHandlers) {
		
		/**
		 * 处理器链定义
		 */
		Map<String, WebSocketMessageHandler<WebSocketMessageEvent>> webSocketHandlers = new LinkedHashMap<String, WebSocketMessageHandler<WebSocketMessageEvent>>();
		Map<String, String> handlerChainDefinitionMap = new HashMap<String, String>();
		
		Iterator<Entry<String, WebSocketMessageHandler<WebSocketMessageEvent>>> ite = webSocketMessageHandlers.entrySet().iterator();
		while (ite.hasNext()) {
			Entry<String, WebSocketMessageHandler<WebSocketMessageEvent>> entry = ite.next();
			if (entry.getValue() instanceof MessageEventWebSocketHandler) {
				// 跳过入口实现类
				continue;
			}
			
			SocketConsumer annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), SocketConsumer.class);
			if(annotationType == null) {
				// 注解为空，则打印错误信息
				LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", SocketConsumer.class, entry.getValue().getClass(), entry.getKey());
			} else {
				String path = StringUtils.hasText(serverProperties.getServlet().getContextPath()) ? serverProperties.getServlet().getContextPath() + annotationType.path() : annotationType.path();
				handlerChainDefinitionMap.put(path, entry.getKey());
			}
			
			webSocketHandlers.put(entry.getKey(), entry.getValue());
		}
		
		HandlerChainManager<WebSocketMessageEvent> manager = createHandlerChainManager(webSocketHandlers, handlerChainDefinitionMap);
		PathMatchingHandlerChainResolver chainResolver = new PathMatchingHandlerChainResolver();
		chainResolver.setHandlerChainManager(manager);

		MessageEventWebSocketHandler socketsHandler = new MessageEventWebSocketHandler(chainResolver);
		
		socketsHandler.setOnlineCountLimit(properties.getOnlineLimit());
		socketsHandler.setMessageSizeLimit(properties.getMessageSize());
		
		return socketsHandler;
	}

	protected Map<String, String> parseHandlerChainDefinitions(String definitions) {
		Ini ini = new Ini();
		try {
			ini.load(definitions);
			Ini.Section section = ini.getSection("urls");
			if (CollectionUtils.isEmpty(section)) {
				section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
			}
			return section;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected HandlerChainManager<WebSocketMessageEvent> createHandlerChainManager(
			Map<String, WebSocketMessageHandler<WebSocketMessageEvent>> eventHandlers,
			Map<String, String> handlerChainDefinitionMap) {

		HandlerChainManager<WebSocketMessageEvent> manager = new DefaultHandlerChainManager();
		if (!CollectionUtils.isEmpty(eventHandlers)) {
			for (Map.Entry<String, WebSocketMessageHandler<WebSocketMessageEvent>> entry : eventHandlers.entrySet()) {
				String name = entry.getKey();
				WebSocketMessageHandler<WebSocketMessageEvent> handler = entry.getValue();
				if (handler instanceof Nameable) {
					((Nameable) handler).setName(name);
				}
				manager.addHandler(name, handler);
			}
		}

		if (!CollectionUtils.isEmpty(handlerChainDefinitionMap)) {
			for (Map.Entry<String, String> entry : handlerChainDefinitionMap.entrySet()) {
				// ant匹配规则
				String rule = entry.getKey();
				String chainDefinition = entry.getValue();
				manager.createChain(rule, chainDefinition);
			}
		}

		return manager;
	}
   

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
