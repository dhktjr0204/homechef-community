package com.cooklog.dto;

import com.cooklog.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
   private User user;
    public CustomUserDetails(User user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().name();
            }
        });
        // 사용자 권한 정보 반환
        return collection;
    }


    @Override
    public String getPassword() {
        // 사용자 비밀번호 반환
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // 사용자 아이디 반환
        return user.getEmail();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public String getIntroduction() {
        return user.getIntroduction();
    }

    public String getProfileImageUrl() {
        return user.getProfileImage();
    }

    public Long getIdx() {
        // 사용자 아이디(pk) 반환
        return user.getIdx();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
