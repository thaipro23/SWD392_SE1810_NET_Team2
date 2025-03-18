package com.pjb.kindergarten_suggestion.dto;

import com.pjb.kindergarten_suggestion.entities.CounsellingRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CounsellingRequestWithRatingDTO {
    private CounsellingRequest counsellingRequest;
    private double rating;
    private long count;
}
