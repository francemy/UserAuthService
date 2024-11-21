package com.exemplo.usermanagement.exception;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {
        super(message);
    }
}
