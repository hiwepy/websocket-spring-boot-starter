package com.github.vindell.websocket.session.handler.chain.def;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.github.vindell.websocket.event.WebSocketMessageEvent;
import com.github.vindell.websocket.session.handler.chain.HandlerChain;
import com.github.vindell.websocket.session.handler.chain.HandlerChainManager;
import com.github.vindell.websocket.session.handler.chain.HandlerChainResolver;

public class PathMatchingHandlerChainResolver implements HandlerChainResolver<WebSocketMessageEvent> {

	private static final Logger log = LoggerFactory.getLogger(PathMatchingHandlerChainResolver.class);
	/**
	 * handlerChain管理器
	 */
	private HandlerChainManager<WebSocketMessageEvent> handlerChainManager;
	
	/**
	 * 路径匹配器
	 */
	private PathMatcher pathMatcher;
	
	 public PathMatchingHandlerChainResolver() {
        this.pathMatcher = new AntPathMatcher();
        this.handlerChainManager = new DefaultHandlerChainManager();
    }

	public HandlerChainManager<WebSocketMessageEvent> getHandlerChainManager() {
		return handlerChainManager;
	}

	public void setHandlerChainManager(HandlerChainManager<WebSocketMessageEvent> handlerChainManager) {
		this.handlerChainManager = handlerChainManager;
	}

	public PathMatcher getPathMatcher() {
		return pathMatcher;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}
	
	@Override
	public HandlerChain<WebSocketMessageEvent> getChain(WebSocketMessageEvent event, HandlerChain<WebSocketMessageEvent> originalChain) {
        HandlerChainManager<WebSocketMessageEvent> handlerChainManager = getHandlerChainManager();
        if (!handlerChainManager.hasChains()) {
            return null;
        }
        String eventURI = getPathWithinEvent(event);
        for (String pathPattern : handlerChainManager.getChainNames()) {
            if (pathMatches(pathPattern, eventURI)) {
                if (log.isTraceEnabled()) {
                    log.trace("Matched path pattern [" + pathPattern + "] for eventURI [" + eventURI + "].  " +
                            "Utilizing corresponding handler chain...");
                }
                return handlerChainManager.proxy(originalChain, pathPattern);
            }
        }
        return null;
    }

    protected boolean pathMatches(String pattern, String path) {
        PathMatcher pathMatcher = getPathMatcher();
        return pathMatcher.match(pattern, path);
    }

    protected String getPathWithinEvent(WebSocketMessageEvent event) {
    	return event.getRouteExpression();
    }
	
}
