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
package com.github.vindell.websocket.property;

public class WebsocketStompProperties {

	/**
	 * Register a STOMP over WebSocket endpoint at the given mapping path.
	 */
	private String paths  = "*";
	private String allowedOrigins = "*";
	private WebsocketSockJSProperties sockjs = new WebsocketSockJSProperties();

	public String getPaths() {
		return paths;
	}

	public void setPaths(String paths) {
		this.paths = paths;
	}

	public String getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(String allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	public WebsocketSockJSProperties getSockjs() {
		return sockjs;
	}

	public void setSockjs(WebsocketSockJSProperties sockjs) {
		this.sockjs = sockjs;
	}

}
