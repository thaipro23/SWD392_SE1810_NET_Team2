package com.pjb.kindergarten_suggestion.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {
    private String province;
    private String district;
    private String wards;
    private String detail;
}