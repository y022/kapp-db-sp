package org.kapp.entity.exception;

public class ConnectionAcquireException extends DbSpException{
    public ConnectionAcquireException() {super();}
    public ConnectionAcquireException(String message) {super(message);}
    public ConnectionAcquireException(String message, Throwable cause) {super(message, cause);}

}
