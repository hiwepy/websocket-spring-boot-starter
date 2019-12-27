/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
package com.github.hiwepy.websocket.property;

public class WebsocketSockJSProperties {

	/**
	 * Transports with no native cross-domain communication (e.g. "eventsource",
	 * "htmlfile") must get a simple page from the "foreign" domain in an invisible
	 * iframe so that code in the iframe can run from a domain local to the SockJS
	 * server. Since the iframe needs to load the SockJS javascript client library,
	 * this property allows specifying where to load it from.
	 * <p>
	 * By default this is set to point to
	 * "https://cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js". However it can also be
	 * set to point to a URL served by the application.
	 * <p>
	 * Note that it's possible to specify a relative URL in which case the URL must
	 * be relative to the iframe URL. For example assuming a SockJS endpoint mapped
	 * to "/sockjs", and resulting iframe URL "/sockjs/iframe.html", then the the
	 * relative URL must start with "../../" to traverse up to the location above
	 * the SockJS mapping. In case of a prefix-based Servlet mapping one more
	 * traversal may be needed.
	 */
	private String clientLibraryUrl = "https://cdn.bootcss.com/sockjs-client/1.1.4/sockjs.min.js";

	/**
	 * Streaming transports save responses on the client side and don't free memory
	 * used by delivered messages. Such transports need to recycle the connection
	 * once in a while. This property sets a minimum number of bytes that can be
	 * send over a single HTTP streaming request before it will be closed. After
	 * that client will open a new request. Setting this value to one effectively
	 * disables streaming and will make streaming transports to behave like polling
	 * transports.
	 * <p>
	 * The default value is 128K (i.e. 128 * 1024).
	 */
	private Integer streamBytesLimit = 128 * 1024;
	/**
	 * The SockJS protocol requires a server to respond to the initial "/info"
	 * request from clients with a "cookie_needed" boolean property that indicates
	 * whether the use of a JSESSIONID cookie is required for the application to
	 * function correctly, e.g. for load balancing or in Java Servlet containers for
	 * the use of an HTTP session.
	 *
	 * <p>
	 * This is especially important for IE 8,9 that support XDomainRequest -- a
	 * modified AJAX/XHR -- that can do requests across domains but does not send
	 * any cookies. In those cases, the SockJS client prefers the "iframe-htmlfile"
	 * transport over "xdr-streaming" in order to be able to send cookies.
	 *
	 * <p>
	 * The default value is "true" to maximize the chance for applications to work
	 * correctly in IE 8,9 with support for cookies (and the JSESSIONID cookie in
	 * particular). However, an application can choose to set this to "false" if the
	 * use of cookies (and HTTP session) is not required.
	 */
	private boolean sessionCookieNeeded = false;
	/**
	 * The amount of time in milliseconds when the server has not sent any messages
	 * and after which the server should send a heartbeat frame to the client in
	 * order to keep the connection from breaking.
	 * <p>
	 * The default value is 25,000 (25 seconds).
	 */
	private Long heartbeatTime = 25000L;

	/**
	 * The amount of time in milliseconds before a client is considered disconnected
	 * after not having a receiving connection, i.e. an active connection over which
	 * the server can send data to the client.
	 * <p>
	 * The default value is 5000.
	 */
	private Long disconnectDelay = 5000L;

	/**
	 * The number of server-to-client messages that a session can cache while
	 * waiting for the next HTTP polling request from the client. All HTTP
	 * transports use this property since even streaming transports recycle HTTP
	 * requests periodically.
	 * <p>
	 * The amount of time between HTTP requests should be relatively brief and will
	 * not exceed the allows disconnect delay (see
	 * {@link #setDisconnectDelay(long)}), 5 seconds by default.
	 * <p>
	 * The default size is 100.
	 */
	private Integer httpMessageCacheSize = 100;
	/**
	 * Some load balancers don't support WebSocket. This option can be used to
	 * disable the WebSocket transport on the server side.
	 * <p>
	 * The default value is "true".
	 */
	private boolean webSocketEnabled = true;
	/**
	 * This option can be used to disable automatic addition of CORS headers for
	 * SockJS requests.
	 * <p>
	 * The default value is "false".
	 */
	private boolean suppressCors = false;

	public String getClientLibraryUrl() {
		return clientLibraryUrl;
	}

	public void setClientLibraryUrl(String clientLibraryUrl) {
		this.clientLibraryUrl = clientLibraryUrl;
	}

	public Integer getStreamBytesLimit() {
		return streamBytesLimit;
	}

	public void setStreamBytesLimit(Integer streamBytesLimit) {
		this.streamBytesLimit = streamBytesLimit;
	}

	public boolean isSessionCookieNeeded() {
		return sessionCookieNeeded;
	}

	public void setSessionCookieNeeded(boolean sessionCookieNeeded) {
		this.sessionCookieNeeded = sessionCookieNeeded;
	}

	public Long getHeartbeatTime() {
		return heartbeatTime;
	}

	public void setHeartbeatTime(Long heartbeatTime) {
		this.heartbeatTime = heartbeatTime;
	}

	public Long getDisconnectDelay() {
		return disconnectDelay;
	}

	public void setDisconnectDelay(Long disconnectDelay) {
		this.disconnectDelay = disconnectDelay;
	}

	public Integer getHttpMessageCacheSize() {
		return httpMessageCacheSize;
	}

	public void setHttpMessageCacheSize(Integer httpMessageCacheSize) {
		this.httpMessageCacheSize = httpMessageCacheSize;
	}

	public boolean isWebSocketEnabled() {
		return webSocketEnabled;
	}

	public void setWebSocketEnabled(boolean webSocketEnabled) {
		this.webSocketEnabled = webSocketEnabled;
	}

	public boolean isSuppressCors() {
		return suppressCors;
	}

	public void setSuppressCors(boolean suppressCors) {
		this.suppressCors = suppressCors;
	}

}
