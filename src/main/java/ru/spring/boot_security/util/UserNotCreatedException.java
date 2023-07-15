package ru.spring.boot_security.util;

public class UserNotCreatedException extends RuntimeException{
    public UserNotCreatedException (String msg) {
        super(msg);
    }
}
