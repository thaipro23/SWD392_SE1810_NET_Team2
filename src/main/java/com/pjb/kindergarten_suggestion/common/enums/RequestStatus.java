package com.pjb.kindergarten_suggestion.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestStatus {
    OPEN("open"),
    CLOSED("closed");
    private final String name;
}
