package com.github.hiwepy.websocket.session.handler.chain;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hiwepy.websocket.event.WebSocketMessageEvent;
import com.github.hiwepy.websocket.session.handler.WebSocketMessageHandler;

public class ProxiedHandlerChain implements HandlerChain<WebSocketMessageEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ProxiedHandlerChain.class);
	
    private ProxiedHandlerChain originalChain;
    private List<WebSocketMessageHandler<WebSocketMessageEvent>> handlers;
    private int currentPosition = 0;

    public ProxiedHandlerChain() {
        this.currentPosition = -1;
    }
    
    public ProxiedHandlerChain(ProxiedHandlerChain orig, List<WebSocketMessageHandler<WebSocketMessageEvent>> handlers) {
        if (orig == null) {
            throw new NullPointerException("original HandlerChain cannot be null.");
        }
        this.originalChain = orig;
        this.handlers = handlers;
        this.currentPosition = 0;
    }

    @Override
	public void doHandler(WebSocketMessageEvent event) throws Exception {
        if (this.handlers == null || this.handlers.size() == this.currentPosition) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking original filter chain.");
            }
            if(this.originalChain != null) {
            	this.originalChain.doHandler(event);
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking wrapped filter at index [" + this.currentPosition + "]");
            }
            this.handlers.get(this.currentPosition++).doHandler(event, this);
        }
    }
    
}
