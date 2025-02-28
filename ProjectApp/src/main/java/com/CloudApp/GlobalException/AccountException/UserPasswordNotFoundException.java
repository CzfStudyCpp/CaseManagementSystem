package com.CloudApp.GlobalException.AccountException;

public class UserPasswordNotFoundException extends RuntimeException {
    public UserPasswordNotFoundException(String message) {
        super(message);
    }
}
