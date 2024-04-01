package com.cooklog.exception;

import com.cooklog.exception.board.OverContentLengthLimitException;
import com.cooklog.exception.board.OverTagCountLimitException;
import com.cooklog.exception.board.OverTagLengthLimitException;
import com.cooklog.exception.user.NotValidateUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotValidateUserException.class)
    public ResponseEntity<String> NotValidateUserException(NotValidateUserException e){
        return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OverContentLengthLimitException.class)
    public ResponseEntity<String> OverContentLengthLimitException(
            OverContentLengthLimitException ex) {
        return new ResponseEntity<>("콘텐츠 길이가 최대 길이를 초과하였습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OverTagCountLimitException.class)
    public ResponseEntity<String> OverTagsLimitException(
            OverTagCountLimitException ex) {
        return new ResponseEntity<>("태그가 최대 개수를 초과하였습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OverTagLengthLimitException.class)
    public ResponseEntity<String> OverTagLengthLimitException(
            OverTagLengthLimitException ex) {
        return new ResponseEntity<>("태그 길이가 최대 길이를 초과하였습니다. 태크 길이를 줄여주세요.", HttpStatus.BAD_REQUEST);
    }
}
