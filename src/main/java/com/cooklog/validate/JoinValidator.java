package com.cooklog.validate;

import com.cooklog.dto.JoinDTO;
import com.cooklog.exception.myPage.EmptyNicknameException;
import com.cooklog.exception.myPage.OverNicknameLengthLimitException;
import com.cooklog.exception.user.*;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.util.regex.Pattern;

public class JoinValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return JoinDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        JoinDTO request = (JoinDTO) target;
        String nickname = request.getNickname();
        String email = request.getEmail();
        String password = request.getPassword();

        if (isOverNicknameLengthLimit(nickname)) {
            throw new OverNicknameLengthLimitException();
        }

        if (isEmptyNickname(nickname)) {
            throw new EmptyNicknameException();
        }

        if (isEmptyEmail(email)) {
            throw new EmptyEmailException();
        }

        if (!isValidPassword(password)) {
            throw new NotValidPasswordException();
        }

        if (!isValidEmail(email)) {
            throw new NotValidEmailException();
        }
    }

    private boolean isEmptyEmail(String email) {
        return email.isEmpty();
    }

    private boolean isEmptyNickname(String nickname) {
        return nickname.isEmpty();
    }

    private boolean isOverNicknameLengthLimit(String nickname) {
        return nickname.length() > 20;
    }

    private boolean isValidEmail(String email) {
        // 이메일 정규 표현식
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // 패턴을 컴파일한 Pattern 객체 생성
        Pattern pattern = Pattern.compile(emailRegex);
        // 주어진 이메일이 패턴과 일치하는지 검사하여 반환
        return pattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        // 비밀번호 정규 표현식
        String passwordRegex = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}";
        // 패턴을 컴파일한 Pattern 객체 생성
        Pattern pattern = Pattern.compile(passwordRegex);
        // 주어진 비밀번호가 패턴과 일치하는지 검사하여 반환
        return pattern.matcher(password).matches();
    }

}