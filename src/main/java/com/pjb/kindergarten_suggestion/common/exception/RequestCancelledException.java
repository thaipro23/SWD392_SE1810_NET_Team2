package com.pjb.kindergarten_suggestion.common.exception;

public class RequestCancelledException extends RuntimeException {
    public RequestCancelledException(String message) {
        super(message);
    }
}
