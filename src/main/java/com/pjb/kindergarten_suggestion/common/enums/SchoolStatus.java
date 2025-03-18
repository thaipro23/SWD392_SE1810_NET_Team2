package com.pjb.kindergarten_suggestion.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SchoolStatus {
    /*
-	Saved
-	Submitted
-	Approved
-	Rejected
-	Published
-	Unpublished
-	Deleted
     */
    SAVED("Saved"),
    SUBMITTED("Submitted"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PUBLISHED("Published"),
    UNPUBLISHED("Unpublished"),
    DELETED("Deleted");
    
    private final String name;
}
