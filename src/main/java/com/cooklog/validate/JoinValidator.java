package com.cooklog.validate;

import com.cooklog.dto.JoinDTO;
import com.cooklog.exception.myPage.OverNicknameLengthLimitException;
import com.cooklog.exception.user.NotIncludeEngilshPasswordException;
import com.cooklog.exception.user.LengthPasswordException;
import com.cooklog.exception.user.NotIncludeNumPasswordException;
import com.cooklog.exception.user.NotIncludeSpecialCharException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

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

        if (password.length() < 8) {
            throw new LengthPasswordException();
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new NotIncludeEngilshPasswordException();
        }
        if (!password.matches(".*\\d.*")) {
            throw new NotIncludeNumPasswordException();
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new NotIncludeSpecialCharException();
        }

    }

    private boolean isOverNicknameLengthLimit(String nickname) {
        return nickname.length() > 50;
    }

}
