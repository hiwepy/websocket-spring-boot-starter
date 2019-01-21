package com.github.vindell.websocket.session.handler.chain;

import java.util.Map;
import java.util.Set;

import com.github.vindell.websocket.event.WebSocketMessageEvent;
import com.github.vindell.websocket.session.handler.NamedHandlerList;
import com.github.vindell.websocket.session.handler.WebSocketMessageHandler;

/**
 * HandlerChain管理器，负责创建和维护HandlerChain
 */
public interface HandlerChainManager<T extends WebSocketMessageEvent> {

	/*
	 * 获取所有HandlerChain
	 */
    Map<String, WebSocketMessageHandler<T>> getHandlers();

    /*
     * 根据指定的chainName获取Handler列表
     */
    NamedHandlerList<T> getChain(String chainName);

    /*
     * 是否有HandlerChain
     */
    boolean hasChains();

    /*
     * 获取HandlerChain名称列表
     */
    Set<String> getChainNames();

    /*
     * <p>生成代理HandlerChain,先执行chainName指定的filerChian,最后执行servlet容器的original<p>
     */
    HandlerChain<T> proxy(HandlerChain<T> original, String chainName);

   /*
    * <p>方法说明：增加handler到handler列表中<p>
    */
    void addHandler(String name, WebSocketMessageHandler<T> handler);
    
    /*
     * <p>方法说明：创建HandlerChain<p>
     */
    void createChain(String chainName, String chainDefinition);

    /*
     * <p>方法说明：追加handler到指定的HandlerChian中<p>
     */
    void addToChain(String chainName, String handlerName);
	
}