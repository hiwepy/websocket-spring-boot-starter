/**
 * <p>Coyright (R) 2014 正方软件股份有限公司。<p>
 */
package com.github.vindell.websocket.utils;

import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.lang3.builder.Builder;
import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSONObject;

public class TextMessageBuilder implements Builder<TextMessage> {

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * TextMessage结构如下： { time : "yyyy-MM-dd HH:mm:ss", content : "", ... }
	 */
	private JSONObject text;

	public TextMessageBuilder() {
		text = new JSONObject();
	}

	public TextMessageBuilder content(final String content) {
		text.put("content", content);
		return this;
	}

	public TextMessageBuilder content(final Map<String, Object> content) {
		text.putAll(content);
		return this;
	}

	@Override
	public TextMessage build() {
		text.put("time", FORMAT.format(System.currentTimeMillis()));
		CharBuffer temp = CharBuffer.wrap(text.toJSONString());
		return new TextMessage(temp);
	}

	public static TextMessageBuilder get() {
		return new TextMessageBuilder();
	}
}
