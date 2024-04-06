package com.cooklog.exception.bookmark;

public class NotBookmarkedYetException extends RuntimeException{
    public NotBookmarkedYetException(String message) {
        super(message);
    }
}
