package com.pjb.kindergarten_suggestion.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SchoolSearchResponse {
    private List<SchoolDTO> schools;
    private int totalPages;
    private long totalElements;
    private int currentPage;
}