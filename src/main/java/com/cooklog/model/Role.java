package com.cooklog.model;

import java.util.Arrays;

import org.hibernate.annotations.DynamicInsert;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@DynamicInsert
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER","미식 초보"),
    USER2("ROLE_USER2","요리 연습생"),
    USER3("ROLE_USER3","요리 전문가"),
    ADMIN("ROLE_ADMIN","ADMIN");

    private final String key;
    private final String title;

}

