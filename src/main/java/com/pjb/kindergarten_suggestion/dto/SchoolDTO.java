package com.pjb.kindergarten_suggestion.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

import com.pjb.kindergarten_suggestion.common.enums.EnrollStatus;
import com.pjb.kindergarten_suggestion.common.enums.SchoolStatus;

@Data
@Builder
public class SchoolDTO {
    private Long id;
    private String name;
    private String schoolType;
    private String email;
    private String phone;
    private AddressDTO address;
    private String childAge;
    private String educationMethod;
    private Long feeFrom;
    private Long feeTo;
    private Set<String> facilities;
    private Set<String> utilities;
    private String description;
    private List<String> images;
    private double rating;
    private long totalRatingCount;
    private EnrollStatus enrollStatus;
    private SchoolStatus schoolStatus;
}
