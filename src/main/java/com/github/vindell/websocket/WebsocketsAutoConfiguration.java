package com.github.vindell.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.github.vindell.websocket.annotation.MessageRule;
import com.github.vindell.websocket.config.Ini;
import com.github.vindell.websocket.event.WebSocketMessageEvent;
import com.github.vindell.websocket.handler.BroadcastWebSocketsHandler;
import com.github.vindell.websocket.handler.MessageEventWebSocketHandler;
import com.github.vindell.websocket.interceptor.HandshakeSessionInterceptor;
import com.github.vindell.websocket.session.SessionFilter;
import com.github.vindell.websocket.session.handler.Nameable;
import com.github.vindell.websocket.session.handler.WebSocketMessageHandler;
import com.github.vindell.websocket.session.handler.chain.HandlerChainManager;
import com.github.vindell.websocket.session.handler.chain.def.DefaultHandlerChainManager;
import com.github.vindell.websocket.session.handler.chain.def.PathMatchingHandlerChainResolver;
 

/**
 * 
 * @className	： WebsocketsAutoConfiguration
 * @description	： TODO(描述这个类的作用)
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年12月4日 下午12:12:44
 * @version 	V1.0
 */
@Configuration
@EnableConfigurationProperties({ WebsocketsProperties.class })
@SuppressWarnings({"rawtypes", "unchecked"})
public class WebsocketsAutoConfiguration implements ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(WebsocketsAutoConfiguration.class);
	private ApplicationContext applicationContext;
	
	/**
	 * 处理器链定义
	 */
	private Map<String, String> handlerChainDefinitionMap = new HashMap<String, String>();
   
	@Bean
	@ConditionalOnMissingBean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@Bean
	public HandshakeSessionInterceptor handshakeSessionInterceptor(WebsocketsProperties properties) {
		return new HandshakeSessionInterceptor();
	}
	
	@Bean("broadcastSessionFilter")
	@ConditionalOnMissingBean(name = "broadcastSessionFilter")
	public SessionFilter sessionFilter(WebsocketsProperties properties) {
		return SessionFilter.ALL;
	}
	
	@Bean("broadcastWebSocketsHandler")
	public BroadcastWebSocketsHandler broadcastWebSocketsHandler(WebsocketsProperties properties,
			@Qualifier("broadcastSessionFilter") SessionFilter filter) {
		BroadcastWebSocketsHandler socketsHandler = new BroadcastWebSocketsHandler(filter);
		return socketsHandler;
	}
   
    /**
	 * Handler实现集合
	 */
	@Bean("webSocketMessageHandlers")
	public Map<String, WebSocketMessageHandler<WebSocketMessageEvent>> webSocketMessageHandlers() {

		Map<String, WebSocketMessageHandler<WebSocketMessageEvent>> disruptorPreHandlers = new LinkedHashMap<String, WebSocketMessageHandler<WebSocketMessageEvent>>();

		Map<String, WebSocketMessageHandler> beansOfType = getApplicationContext().getBeansOfType(WebSocketMessageHandler.class);
		if (!ObjectUtils.isEmpty(beansOfType)) {
			Iterator<Entry<String, WebSocketMessageHandler>> ite = beansOfType.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, WebSocketMessageHandler> entry = ite.next();
				if (entry.getValue() instanceof MessageEventWebSocketHandler) {
					// 跳过入口实现类
					continue;
				}
				
				MessageRule annotationType = getApplicationContext().findAnnotationOnBean(entry.getKey(), MessageRule.class);
				if(annotationType == null) {
					// 注解为空，则打印错误信息
					LOG.error("Not Found AnnotationType {0} on Bean {1} Whith Name {2}", MessageRule.class, entry.getValue().getClass(), entry.getKey());
				} else {
					handlerChainDefinitionMap.put(annotationType.value(), entry.getKey());
				}
				
				disruptorPreHandlers.put(entry.getKey(), entry.getValue());
			}
		}
		return disruptorPreHandlers;
	}
	 
	
   /**
    * 
    * @description	：  构造WebSocketMessageEventHandler
    * @author 		： 万大龙（743）
    * @date 		：2017年12月4日 上午11:28:22
    * @param webSocketMessageHandlers
    * @return
    */
	@Bean("messageEventWebSocketHandler")
	public MessageEventWebSocketHandler messageEventWebSocketHandler(WebsocketsProperties properties,
			Map<String, WebSocketMessageHandler<WebSocketMessageEvent>> webSocketMessageHandlers) {
		
		HandlerChainManager<WebSocketMessageEvent> manager = createHandlerChainManager(webSocketMessageHandlers, handlerChainDefinitionMap);
		PathMatchingHandlerChainResolver chainResolver = new PathMatchingHandlerChainResolver();
		chainResolver.setHandlerChainManager(manager);

		MessageEventWebSocketHandler socketsHandler = new MessageEventWebSocketHandler(chainResolver);
		
		
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
