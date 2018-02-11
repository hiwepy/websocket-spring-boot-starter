/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
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

import org.springframework.web.util.WebUtils;

public class WebsocketUrlPathHelperProperties {

	private boolean alwaysUseFullPath = false;

	private boolean urlDecode = true;

	private boolean removeSemicolonContent = true;

	private String defaultEncoding = WebUtils.DEFAULT_CHARACTER_ENCODING;

	public boolean isAlwaysUseFullPath() {
		return alwaysUseFullPath;
	}

	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.alwaysUseFullPath = alwaysUseFullPath;
	}

	public boolean isUrlDecode() {
		return urlDecode;
	}

	public void setUrlDecode(boolean urlDecode) {
		this.urlDecode = urlDecode;
	}

	public boolean isRemoveSemicolonContent() {
		return removeSemicolonContent;
	}

	public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
		this.removeSemicolonContent = removeSemicolonContent;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

}
