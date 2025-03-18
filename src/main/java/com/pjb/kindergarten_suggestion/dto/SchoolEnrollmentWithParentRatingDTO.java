package com.pjb.kindergarten_suggestion.dto;

import com.pjb.kindergarten_suggestion.entities.SchoolEnrollment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchoolEnrollmentWithParentRatingDTO {
    private final SchoolEnrollment schoolEnrollment;
    private final double rating;
    private final long count;
}
