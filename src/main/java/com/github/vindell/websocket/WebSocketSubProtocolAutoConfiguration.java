/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.vindell.websocket;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

@Configuration
@AutoConfigureBefore( name = {
	"com.github.vindell.websocket.WebSocketAutoConfiguration",
	"com.github.vindell.websocket.WebSocketBrokerAutoConfiguration"
})
@ConditionalOnWebApplication
@ConditionalOnClass({ SubProtocolHandler.class })
@ConditionalOnProperty(prefix = WebSocketProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ WebSocketProperties.class })
public class WebSocketSubProtocolAutoConfiguration implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;
	
	@Bean
	@ConditionalOnMissingBean
	public StompSubProtocolErrorHandler stompErrorHandler() {
		return new StompSubProtocolErrorHandler();
	}

	@Bean
	@ConditionalOnMissingBean
	public SubProtocolHandler subProtocolHandler(StompSubProtocolErrorHandler stompErrorHandler) {
		StompSubProtocolHandler subProtocolHandler = new StompSubProtocolHandler();
		subProtocolHandler.setErrorHandler(stompErrorHandler);
		return subProtocolHandler;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
	
		Map<String, SubProtocolHandler> beansOfSubProtocol = getApplicationContext().getBeansOfType(SubProtocolHandler.class);
		Map<String, SubProtocolWebSocketHandler> beansOfHandler = getApplicationContext().getBeansOfType(SubProtocolWebSocketHandler.class);
		if (!ObjectUtils.isEmpty(beansOfHandler)) {
			Iterator<Entry<String, SubProtocolWebSocketHandler>> ite = beansOfHandler.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<String, SubProtocolWebSocketHandler> entry = ite.next();
				Iterator<Entry<String, SubProtocolHandler>> iteProtocol = beansOfSubProtocol.entrySet().iterator();
				while (iteProtocol.hasNext()) {
					entry.getValue().addProtocolHandler(iteProtocol.next().getValue());
				}
			}
		}
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	
}
