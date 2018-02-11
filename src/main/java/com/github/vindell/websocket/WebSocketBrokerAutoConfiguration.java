package com.github.vindell.websocket;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;
import org.springframework.web.util.UrlPathHelper;

import com.github.vindell.websocket.property.WebsocketSockJSProperties;
import com.github.vindell.websocket.property.WebsocketStompProperties;
import com.github.vindell.websocket.property.WebsocketUrlPathHelperProperties;
import com.github.vindell.websocket.utils.StringUtils;

/**
 * 
 * @className ： WebsocketStompAutoConfiguration
 * @description ： <br/>
 *              1.@EnableWebSocketMessageBroker注解表示开启使用STOMP协议来传输基于代理的消息<br/>
 *              2.registerStompEndpoints方法表示注册STOMP协议的节点，并指定映射的URL。<br/>
 *              3.stompEndpointRegistry.addEndpoint("/endpointSang").withSockJS();这一行代码用来注册STOMP协议节点，同时指定使用SockJS协议。<br/>
 *              4.configureMessageBroker方法用来配置消息代理，由于我们是实现推送功能，这里的消息代理是/topic
 * @author ： <a href="https://github.com/vindell">vindell</a>
 * @date ： 2018年2月11日 上午11:32:04
 * @version V1.0
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ StompEndpointRegistry.class })
@ConditionalOnProperty(prefix = WebSocketBrokerProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ WebSocketBrokerProperties.class })
@EnableWebSocketMessageBroker
public class WebSocketBrokerAutoConfiguration extends AbstractWebSocketMessageBrokerConfigurer implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Autowired
	private WebSocketBrokerProperties brokerProperties;
	@Autowired
	private UrlPathHelper urlPathHelper;
	@Autowired
	private StompSubProtocolErrorHandler stompErrorHandler;
	@Autowired
	private HandshakeHandler handshakeHandler;
	@Autowired(required = false)
	private SockJsMessageCodec sockJsMessageCodec;
	@Autowired
	public PathMatcher pathMatcher;

	@Bean
	@ConditionalOnMissingBean
	public PathMatcher pathMatcher() {
		return new AntPathMatcher();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		
		/*
		 * registry.enableSimpleBroker("/topic", "/user");这句话表示在topic和user这两个域上可以向客户端发消息。
		 * registry.setUserDestinationPrefix("/user");这句话表示给指定用户发送一对一的主题前缀是"/user"。
		 * registry.setApplicationDestinationPrefixes("/app"); 这句话表示客户单向服务器端发送时的主题上面需要加"/app"作为前缀。
		 */
		if(StringUtils.hasText(brokerProperties.getSimpleBrokerDestinationPrefixes())) {
			registry.enableSimpleBroker(StringUtils.tokenizeToStringArray(brokerProperties.getSimpleBrokerDestinationPrefixes()));
		}
		if(StringUtils.hasText(brokerProperties.getStompBrokerRelayDestinationPrefixes())) {
			registry.enableStompBrokerRelay(StringUtils.tokenizeToStringArray(brokerProperties.getStompBrokerRelayDestinationPrefixes()));
		}
		if(StringUtils.hasText(brokerProperties.getApplicationDestinationPrefixes())) {
			registry.setApplicationDestinationPrefixes(StringUtils.tokenizeToStringArray(brokerProperties.getApplicationDestinationPrefixes()));
		}
		registry.setCacheLimit(brokerProperties.getCacheLimit());
		registry.setPathMatcher(pathMatcher);
		if(StringUtils.hasText(brokerProperties.getUserDestinationPrefix())) {
			registry.setUserDestinationPrefix(brokerProperties.getUserDestinationPrefix());
		}
		
	}
	
	@Bean
	@ConditionalOnMissingBean
	public StompSubProtocolErrorHandler stompErrorHandler() {
		return new StompSubProtocolErrorHandler();
	}

	@Bean
	@ConditionalOnMissingBean
	public HandshakeHandler handshakeHandler() {
		return new DefaultHandshakeHandler();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public UrlPathHelper urlPathHelper() {

		UrlPathHelper urlPathHelper = new UrlPathHelper();

		WebsocketUrlPathHelperProperties uphProperties = brokerProperties.getUrlPathHelper();

		urlPathHelper.setAlwaysUseFullPath(uphProperties.isAlwaysUseFullPath());
		urlPathHelper.setDefaultEncoding(uphProperties.getDefaultEncoding());
		urlPathHelper.setRemoveSemicolonContent(uphProperties.isRemoveSemicolonContent());
		urlPathHelper.setUrlDecode(uphProperties.isUrlDecode());

		return urlPathHelper;
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = WebSocketBrokerProperties.PREFIX, value = "withSockJS", havingValue = "true")
	public SockJsMessageCodec sockJsMessageCodec() {
		return new Jackson2SockJsMessageCodec();
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {

		List<WebsocketStompProperties> stompEndpoints = brokerProperties.getStompEndpoints();
		if(CollectionUtils.isEmpty(stompEndpoints)) {
			return;
		}
		Map<String, HandshakeInterceptor> beansOfType = getApplicationContext().getBeansOfType(HandshakeInterceptor.class);
		for (WebsocketStompProperties endpointProperties : stompEndpoints) {

			// 将"Endpoint" 路径注册为STOMP端点，这个路径与发送和接收消息的目的路径有所不同，这是一个端点，客户端在订阅或发布消息到目的地址前，要连接该端点，
			StompWebSocketEndpointRegistration registration = stompEndpointRegistry
					.addEndpoint(StringUtils.tokenizeToStringArray(endpointProperties.getPaths()))
					// setAllowedOrigins() 方法表示允许连接的域名
					.setAllowedOrigins(endpointProperties.getAllowedOrigins()).setHandshakeHandler(handshakeHandler);

			if (!ObjectUtils.isEmpty(beansOfType)) {
				HandshakeInterceptor[] interceptors = beansOfType.values().toArray(new HandshakeInterceptor[beansOfType.size()]);
				registration.addInterceptors(interceptors);
			}

			if (brokerProperties.isWithSockJs()) {

				WebsocketSockJSProperties sockJSProperties = endpointProperties.getSockjs();

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

		stompEndpointRegistry.setErrorHandler(stompErrorHandler);
		stompEndpointRegistry.setUrlPathHelper(urlPathHelper);
		stompEndpointRegistry.setOrder(brokerProperties.getOrder());

	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
