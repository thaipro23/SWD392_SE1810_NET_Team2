package com.pjb.kindergarten_suggestion.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    SCHOOL_OWNER("SCHOOL_OWNER"),
    PARENT("PARENT"),
    ADMIN("ADMIN");

    private final String name;
}
