package com.cooklog.exception.likes;

public class NotLikedYetException extends RuntimeException{
    public NotLikedYetException(String message) {
        super(message);
    }
}
