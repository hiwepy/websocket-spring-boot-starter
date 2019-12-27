package com.github.hiwepy.websocket.exception;

@SuppressWarnings("serial")
public class EventHandleException extends RuntimeException {

    public EventHandleException(Exception e) {
        super(e.getMessage(), null);
    }
    
    public EventHandleException(String errorMessage) {
        super(errorMessage, null);
    }
    
    public EventHandleException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
 
    
}
