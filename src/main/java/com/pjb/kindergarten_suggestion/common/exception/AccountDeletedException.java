package com.pjb.kindergarten_suggestion.common.exception;

public class AccountDeletedException extends RuntimeException {
    public AccountDeletedException(String message) {
        super(message);
    }
}