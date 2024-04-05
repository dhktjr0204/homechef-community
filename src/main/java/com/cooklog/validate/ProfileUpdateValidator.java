package com.cooklog.validate;

import com.cooklog.dto.MyPageUpdateRequestDTO;
import com.cooklog.exception.myPage.EmptyNicknameException;
import com.cooklog.exception.myPage.OverIntroductionLengthLimitException;
import com.cooklog.exception.myPage.OverNicknameLengthLimitException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ProfileUpdateValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return MyPageUpdateRequestDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MyPageUpdateRequestDTO request=(MyPageUpdateRequestDTO) target;
        String nickname=request.getNickname();
        String introduction=request.getIntroduction();
        if(isOverNicknameLengthLimit(nickname)){
            throw new OverNicknameLengthLimitException();
        }
        if(isEmptyNickname(nickname)){
            throw new EmptyNicknameException();
        }
        if(isOverIntroductionLengthLimit(introduction)){
            throw new OverIntroductionLengthLimitException();
        }
    }
    private boolean isOverNicknameLengthLimit(String nickname){
        return nickname.length()>20;
    }
    private boolean isEmptyNickname(String nickname){
        return nickname.isEmpty();
    }
    private boolean isOverIntroductionLengthLimit(String introduction){return introduction.length()>300;}

}
