/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.vindell.websocket.property.WebsocketStompProperties;
import com.github.vindell.websocket.property.WebsocketUrlPathHelperProperties;

@ConfigurationProperties(WebSocketBrokerProperties.PREFIX)
public class WebSocketBrokerProperties {

	public static final String PREFIX = "websocket.broker";

	/** Whether Enable Websocket Broker. */
	private boolean enabled = false;
	/** Websocket Stomp Order. */
	private int order = 0;
	/** Websocket urlPathHelper. */
	private WebsocketUrlPathHelperProperties urlPathHelper = new WebsocketUrlPathHelperProperties();
	/** Websocket Stomp Endpoint. */
	private List<WebsocketStompProperties> stompEndpoints = new ArrayList<WebsocketStompProperties>();
	/** Enable SockJS fallback options. */
	private boolean withSockJs = true;
	/**
	 * Configure one or more prefixes to filter destinations targeting application
	 * annotated methods. For example destinations prefixed with "/app" may be
	 * processed by annotated methods while other destinations may target the
	 * message broker (e.g. "/topic", "/queue").
	 * <p>
	 * When messages are processed, the matching prefix is removed from the
	 * destination in order to form the lookup path. This means annotations should
	 * not contain the destination prefix.
	 * <p>
	 * Prefixes that do not have a trailing slash will have one automatically
	 * appended.
	 */
	private String applicationDestinationPrefixes;

	/**
	 * Configure the prefix used to identify user destinations. User destinations
	 * provide the ability for a user to subscribe to queue names unique to their
	 * session as well as for others to send messages to those unique, user-specific
	 * queues.
	 * <p>
	 * For example when a user attempts to subscribe to
	 * "/user/queue/position-updates", the destination may be translated to
	 * "/queue/position-updatesi9oqdfzo" yielding a unique queue name that does not
	 * collide with any other user attempting to do the same. Subsequently when
	 * messages are sent to "/user/{username}/queue/position-updates", the
	 * destination is translated to "/queue/position-updatesi9oqdfzo".
	 * <p>
	 * The default prefix used to identify such destinations is "/user/".
	 */
	private String userDestinationPrefix;

	/**
	 * Configure the cache limit to apply for registrations with the broker.
	 * <p>
	 * This is currently only applied for the destination cache in the subscription
	 * registry. The default cache limit there is 1024.
	 */
	private Integer cacheLimit = 1024;

	/**
	 * simple message broker and configure one or more prefixes to filter
	 * destinations targeting the broker (e.g. destinations prefixed with "/topic").
	 */
	private String simpleBrokerDestinationPrefixes = "/topic";

	/**
	 * Enable a STOMP broker relay and configure the destination prefixes supported
	 * by the message broker. Check the STOMP documentation of the message broker
	 * for supported destinations.
	 */
	private String stompBrokerRelayDestinationPrefixes;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public WebsocketUrlPathHelperProperties getUrlPathHelper() {
		return urlPathHelper;
	}

	public void setUrlPathHelper(WebsocketUrlPathHelperProperties urlPathHelper) {
		this.urlPathHelper = urlPathHelper;
	}

	public List<WebsocketStompProperties> getStompEndpoints() {
		return stompEndpoints;
	}

	public void setStompEndpoints(List<WebsocketStompProperties> stompEndpoints) {
		this.stompEndpoints = stompEndpoints;
	}

	public boolean isWithSockJs() {
		return withSockJs;
	}

	public void setWithSockJs(boolean withSockJs) {
		this.withSockJs = withSockJs;
	}

	public String getApplicationDestinationPrefixes() {
		return applicationDestinationPrefixes;
	}

	public void setApplicationDestinationPrefixes(String applicationDestinationPrefixes) {
		this.applicationDestinationPrefixes = applicationDestinationPrefixes;
	}

	public String getUserDestinationPrefix() {
		return userDestinationPrefix;
	}

	public void setUserDestinationPrefix(String userDestinationPrefix) {
		this.userDestinationPrefix = userDestinationPrefix;
	}

	public Integer getCacheLimit() {
		return cacheLimit;
	}

	public void setCacheLimit(Integer cacheLimit) {
		this.cacheLimit = cacheLimit;
	}

	public String getSimpleBrokerDestinationPrefixes() {
		return simpleBrokerDestinationPrefixes;
	}

	public void setSimpleBrokerDestinationPrefixes(String simpleBrokerDestinationPrefixes) {
		this.simpleBrokerDestinationPrefixes = simpleBrokerDestinationPrefixes;
	}

	public String getStompBrokerRelayDestinationPrefixes() {
		return stompBrokerRelayDestinationPrefixes;
	}

	public void setStompBrokerRelayDestinationPrefixes(String stompBrokerRelayDestinationPrefixes) {
		this.stompBrokerRelayDestinationPrefixes = stompBrokerRelayDestinationPrefixes;
	}
	

}
