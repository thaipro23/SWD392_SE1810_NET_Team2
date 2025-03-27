package com.pjb.kindergarten_suggestion.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BillStatus {
    /*
    - Unpaid: Chưa đóng
    - Paid: Đã đóng
    */
    UNPAID("Unpaid"),
    PAID("Paid");

    private final String name;
}
