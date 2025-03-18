package com.pjb.kindergarten_suggestion.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Set;

@Data
@Builder
@Getter
@AllArgsConstructor

public class SchoolSearchRequest {
    private String name;
    private Integer provinceId;
    private Integer districtId;
    private Long schoolTypeId;
    private Long childAgeId;
    @Min(value = 0, message = "Minimum fee cannot be negative")
    private Long feeFrom;

    @Min(value = 0, message = "Maximum fee cannot be negative")
    private Long feeTo;
    private Set<Long> facilityIds;
    private Set<Long> utilityIds;
    private String sortBy = "RATING";
    private int page;
    private int size;

    public SchoolSearchRequest() {

    }
}