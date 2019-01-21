package com.github.vindell.websocket.session.handler.chain.def;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.github.vindell.websocket.event.WebSocketMessageEvent;
import com.github.vindell.websocket.session.handler.Nameable;
import com.github.vindell.websocket.session.handler.NamedHandlerList;
import com.github.vindell.websocket.session.handler.WebSocketMessageHandler;
import com.github.vindell.websocket.session.handler.chain.HandlerChain;
import com.github.vindell.websocket.session.handler.chain.HandlerChainManager;
import com.github.vindell.websocket.utils.StringUtils;

public class DefaultHandlerChainManager implements HandlerChainManager<WebSocketMessageEvent> {
	
	private static transient final Logger log = LoggerFactory.getLogger(DefaultHandlerChainManager.class);

    private Map<String, WebSocketMessageHandler<WebSocketMessageEvent>> handlers; 

    private Map<String, NamedHandlerList<WebSocketMessageEvent>> handlerChains;

    private final static String DEFAULT_CHAIN_DEFINATION_DELIMITER_CHAR = ",";
    
    public DefaultHandlerChainManager() {
        this.handlers = new LinkedHashMap<String, WebSocketMessageHandler<WebSocketMessageEvent>>();
        this.handlerChains = new LinkedHashMap<String, NamedHandlerList<WebSocketMessageEvent>>();
    }
    
    @Override
    public Map<String, WebSocketMessageHandler<WebSocketMessageEvent>> getHandlers() {
        return handlers;
    }

    public void setHandlers(Map<String, WebSocketMessageHandler<WebSocketMessageEvent>> handlers) {
        this.handlers = handlers;
    }

    public Map<String, NamedHandlerList<WebSocketMessageEvent>> getHandlerChains() {
        return handlerChains;
    }
    
    public void setHandlerChains(Map<String, NamedHandlerList<WebSocketMessageEvent>> handlerChains) {
        this.handlerChains = handlerChains;
    }

    public WebSocketMessageHandler<WebSocketMessageEvent> getHandler(String name) {
        return this.handlers.get(name);
    }
    
    @Override
    public void addHandler(String name, WebSocketMessageHandler<WebSocketMessageEvent> handler) {
        addHandler(name, handler, true);
    }
    
    protected void addHandler(String name, WebSocketMessageHandler<WebSocketMessageEvent> handler, boolean overwrite) {
        WebSocketMessageHandler<WebSocketMessageEvent> existing = getHandler(name);
        if (existing == null || overwrite) {
            if (handler instanceof Nameable) {
                ((Nameable) handler).setName(name);
            }
            this.handlers.put(name, handler);
        }
    }

    @Override
    public void createChain(String chainName, String chainDefinition) {
        if (StringUtils.isBlank(chainName)) {
            throw new NullPointerException("chainName cannot be null or empty.");
        }
        if (StringUtils.isBlank(chainDefinition)) {
            throw new NullPointerException("chainDefinition cannot be null or empty.");
        }
        if (log.isDebugEnabled()) {
            log.debug("Creating chain [" + chainName + "] from String definition [" + chainDefinition + "]");
        }
        String[] handlerTokens = splitChainDefinition(chainDefinition);
        for (String token : handlerTokens) {
            addToChain(chainName, token);
        }
    }

    /*
     * Splits the comma-delimited handler chain definition line into individual handler definition tokens.
     */
    protected String[] splitChainDefinition(String chainDefinition) {
    	String trimToNull = StringUtils.trimToNull(chainDefinition);
    	if(trimToNull == null){
    		return null;
    	}
    	String[] split = StringUtils.splits(trimToNull, DEFAULT_CHAIN_DEFINATION_DELIMITER_CHAR);
    	for (int i = 0; i < split.length; i++) {
    		split[i] = StringUtils.trimToNull(split[i]);
		}
        return split;
    }

    public static void main(String[] args) {
		
	}
    
    @Override
    public void addToChain(String chainName, String handlerName) {
        if (!StringUtils.hasText(chainName)) {
            throw new IllegalArgumentException("chainName cannot be null or empty.");
        }
        WebSocketMessageHandler<WebSocketMessageEvent> handler = getHandler(handlerName);
        if (handler == null) {
            throw new IllegalArgumentException("There is no handler with name '" + handlerName +
                    "' to apply to chain [" + chainName + "] in the pool of available Handlers.  Ensure a " +
                    "handler with that name/path has first been registered with the addHandler method(s).");
        }
        NamedHandlerList<WebSocketMessageEvent> chain = ensureChain(chainName);
        chain.add(handler);
    }

    protected NamedHandlerList<WebSocketMessageEvent> ensureChain(String chainName) {
        NamedHandlerList<WebSocketMessageEvent> chain = getChain(chainName);
        if (chain == null) {
            chain = new DefaultNamedHandlerList(chainName);
            this.handlerChains.put(chainName, chain);
        }
        return chain;
    }

    @Override
    public NamedHandlerList<WebSocketMessageEvent> getChain(String chainName) {
        return this.handlerChains.get(chainName);
    }

    @Override
    public boolean hasChains() {
        return !CollectionUtils.isEmpty(this.handlerChains);
    }

    @SuppressWarnings("unchecked")
    @Override
	public Set<String> getChainNames() {
        return this.handlerChains != null ? this.handlerChains.keySet() : Collections.EMPTY_SET;
    }

    @Override
    public HandlerChain<WebSocketMessageEvent> proxy(HandlerChain<WebSocketMessageEvent> original, String chainName) {
        NamedHandlerList<WebSocketMessageEvent> configured = getChain(chainName);
        if (configured == null) {
            String msg = "There is no configured chain under the name/key [" + chainName + "].";
            throw new IllegalArgumentException(msg);
        }
        return configured.proxy(original);
    }

}
