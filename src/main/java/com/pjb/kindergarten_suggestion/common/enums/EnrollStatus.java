package com.pjb.kindergarten_suggestion.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnrollStatus {
    PENDING("pending"),
    CANCELLED("cancelled"),
    ENROLL("enroll"),
    UNENROLL("unenroll");
    private final String name;
}
