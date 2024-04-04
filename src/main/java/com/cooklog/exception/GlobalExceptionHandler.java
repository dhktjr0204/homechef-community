package com.cooklog.exception;

import com.cooklog.exception.board.BoardNotFoundException;
import com.cooklog.exception.board.OverContentLengthLimitException;
import com.cooklog.exception.board.OverTagCountLimitException;
import com.cooklog.exception.board.OverTagLengthLimitException;
import com.cooklog.exception.bookmark.AlreadyBookmarkedException;
import com.cooklog.exception.bookmark.NotBookmarkedYetException;
import com.cooklog.exception.likes.AlreadyLikedException;
import com.cooklog.exception.likes.NotLikedYetException;
import com.cooklog.exception.myPage.OverIntroductionLengthLimitException;
import com.cooklog.exception.myPage.OverNicknameLengthLimitException;
import com.cooklog.exception.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //유저 관련 예외처리
    @ExceptionHandler(NotValidateUserException.class)
    public ResponseEntity<String> NotValidateUserException(NotValidateUserException e){
        return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
    }

    // 회원가입 관련 예외처리
    @ExceptionHandler(AlreadyExistsEmailException.class)
    public ResponseEntity<String> AlreadyExistsEmailException(
            AlreadyExistsEmailException ex) {
        return new ResponseEntity<>("이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LengthPasswordException.class)
    public ResponseEntity<String> LengthPasswordException(LengthPasswordException ex){
        return new ResponseEntity<>("비밀번호는 최소 8자 이상이어야 합니다.", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotIncludeEngilshPasswordException.class)
    public ResponseEntity<String> NotIncludeEngilshPasswordException(NotIncludeEngilshPasswordException ex){
        return new ResponseEntity<>("비밀번호는 영문자를 포함해야 합니다.", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotIncludeNumPasswordException.class)
    public ResponseEntity<String> NotIncludeNumPasswordException(NotIncludeNumPasswordException ex){
        return new ResponseEntity<>("비밀번호는 숫자를 포함해야 합니다.", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotIncludeSpecialCharException.class)
    public ResponseEntity<String> NotIncludeSpecialCharException(NotIncludeSpecialCharException ex){
        return new ResponseEntity<>("비밀번호는 특수문자를 포함해야 합니다.", HttpStatus.BAD_REQUEST);
    }

    //게시글 관련 예외처리

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

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<String> BoardNotFoundException(BoardNotFoundException ex){
        return ResponseEntity.notFound().build();
    }


    //북마크 관련 예외처리
    @ExceptionHandler(AlreadyBookmarkedException.class)
    public ResponseEntity<String> AlreadyBookmarkedException(AlreadyBookmarkedException ex) {
        return new ResponseEntity<>("해당 게시물은 이미 북마크로 등록이 되어있습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotBookmarkedYetException.class)
    public ResponseEntity<String> NotBookmarkedYetException(NotBookmarkedYetException ex) {
        return new ResponseEntity<>("해당 게시물은 이미 북마크로 등록이 되어있지 않습니다.",HttpStatus.BAD_REQUEST);
    }

    //좋아요 관련 예외처리
    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<String> AlreadyLikedException(AlreadyLikedException ex) {
        return new ResponseEntity<>("해당 게시물은 이미 좋아요가 눌러져 있습니다.",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotLikedYetException.class)
    public ResponseEntity<String> NotLikedYetException(NotLikedYetException ex) {
        return new ResponseEntity<>("해당 게시물은 이미 좋아요가 눌러져 있지 않습니다.", HttpStatus.BAD_REQUEST);
    }
    //마이페이지 예외 처리
    @ExceptionHandler(OverNicknameLengthLimitException.class)
    public ResponseEntity<String> OverNicknameLengthLimitException(OverNicknameLengthLimitException ex) {
        return new ResponseEntity<>("ID가 길이를 초과하였습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OverIntroductionLengthLimitException.class)
    public ResponseEntity<String> OverIntroductionLengthLimitException(OverIntroductionLengthLimitException ex){
        return new ResponseEntity<>("인삿말이 길이를 초과하였습니다.", HttpStatus.BAD_REQUEST);
    }
}
