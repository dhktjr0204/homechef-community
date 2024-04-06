package com.cooklog.exception.bookmark;

public class AlreadyBookmarkedException extends RuntimeException{
    public AlreadyBookmarkedException(String message) {
        super(message);
    }
}
