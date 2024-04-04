package com.cooklog.exception;

import com.cooklog.exception.board.BoardNotFoundException;
import com.cooklog.exception.board.NoImageException;
import com.cooklog.exception.board.OverContentLengthLimitException;
import com.cooklog.exception.board.OverTagCountLimitException;
import com.cooklog.exception.board.OverTagLengthLimitException;
import com.cooklog.exception.bookmark.AlreadyBookmarkedException;
import com.cooklog.exception.bookmark.NotBookmarkedYetException;
import com.cooklog.exception.likes.AlreadyLikedException;
import com.cooklog.exception.likes.NotLikedYetException;
import com.cooklog.exception.myPage.EmptyNicknameException;
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
    public ResponseEntity<String> AlreadyExistsEmailException(AlreadyExistsEmailException ex) {
        return new ResponseEntity<>("이미 가입된 이메일 주소입니다. 다른 이메일 주소를 사용하세요.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyEmailException.class)
    public ResponseEntity<String> EmptyEmailException(EmptyEmailException ex) {
        return new ResponseEntity<>("이메일을 입력해주세요.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidEmailException.class)
    public ResponseEntity<String> NotValidEmailException(NotValidEmailException ex) {
        return new ResponseEntity<>("이메일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidPasswordException.class)
    public ResponseEntity<String> NotValidPasswordException(NotValidPasswordException ex) {
        return new ResponseEntity<>("비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.", HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(NoImageException.class)
    public ResponseEntity<String> NoImageException(NoImageException ex){
        return new ResponseEntity<>("이미지를 한 장 이상 첨부해주세요.",HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity<>("ID가 길이를 초과하였습니다. 20자 이하로 입력해 주세요.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OverIntroductionLengthLimitException.class)
    public ResponseEntity<String> OverIntroductionLengthLimitException(OverIntroductionLengthLimitException ex){
        return new ResponseEntity<>("인삿말이 길이를 초과하였습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyNicknameException.class)
    public ResponseEntity<String> EmptyNicknameException(EmptyNicknameException ex){
        return new ResponseEntity<>("ID를 입력해주세요.", HttpStatus.BAD_REQUEST);
    }
}
