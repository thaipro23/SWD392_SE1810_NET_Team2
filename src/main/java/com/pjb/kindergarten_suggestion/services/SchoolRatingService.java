package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.dto.SchoolAvgRating;
import com.pjb.kindergarten_suggestion.dto.SchoolRatingDTO;
import com.pjb.kindergarten_suggestion.entities.SchoolRating;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

public interface SchoolRatingService {
    List<SchoolRating> findRatingsByDateRange(Long schoolId, LocalDate fromDate, LocalDate toDate);

    double calculateAverageRating(Long schoolId);

    long countFeedbacks(Long schoolId);

    List<SchoolRating> filterRatingsByStars(Long schoolId, int stars);

    double getAverageLearningProgramRating(Long schoolId, LocalDate fromDate, LocalDate toDate);

    double getAverageFacilitiesRating(Long schoolId, LocalDate fromDate, LocalDate toDate);

    double getAverageExtracurricularRating(Long schoolId, LocalDate fromDate, LocalDate toDate);

    double getAverageTeachersRating(Long schoolId, LocalDate fromDate, LocalDate toDate);

    double getAverageHygieneRating(Long schoolId, LocalDate fromDate, LocalDate toDate);

    double getOverallAverageRating(Long schoolId, LocalDate fromDate, LocalDate toDate);

    long getTotalFeedbacks(Long schoolId, LocalDate fromDate, LocalDate toDate);

    Map<String, Object> getAllRatingData(Long schoolId, LocalDate fromDate, LocalDate toDate);

    SchoolRating getRatingsByParentId(Long parentId);

    SchoolRating getRatingByParentAndSchool(Long parentId, Long schoolId);

    SchoolRating createOrUpdateRating(SchoolRating sr);

    SchoolAvgRating getAvgRatingBySchoolId(Long schoolId);

    List<SchoolRating> getTop4RatingsWithFiveStar();

    boolean existsRating(Long parentId, Long schoolId);

    Page<SchoolRatingDTO> getRatingBySchoolId(Long id, int page, int size, Integer stars);
}
