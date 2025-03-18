package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.dto.SchoolAvgRating;
import com.pjb.kindergarten_suggestion.dto.SchoolRatingDTO;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolRating;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.repositories.SchoolRatingRepository;
import com.pjb.kindergarten_suggestion.repositories.SchoolRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SchoolRatingServiceImpl implements SchoolRatingService {

    private final SchoolRatingRepository schoolRatingRepository;
    private final AuthService authService;
    private final SchoolRepository schoolRepository;

    @Override
    public List<SchoolRating> findRatingsByDateRange(Long schoolId, LocalDate fromDate, LocalDate toDate) {
        return schoolRatingRepository.findBySchoolIdAndCreatedAtBetweenOrderByCreatedAtDesc(schoolId,
                fromDate.atStartOfDay(),
                toDate.atTime(23, 59, 59));
    }

    @Override
    public double calculateAverageRating(Long schoolId) {
        List<SchoolRating> ratings = schoolRatingRepository.findBySchoolId(schoolId);
        return ratings.stream().mapToDouble(SchoolRating::getAvgRating).average().orElse(0.0);
    }

    @Override
    public long countFeedbacks(Long schoolId) {
        return schoolRatingRepository.countBySchoolId(schoolId);
    }

    @Override
    public List<SchoolRating> filterRatingsByStars(Long schoolId, int stars) {
        List<SchoolRating> ratings = schoolRatingRepository.findBySchoolId(schoolId);
        return ratings.stream()
                .filter(rating -> rating.getAvgRating() >= stars)
                .collect(Collectors.toList());
    }

    public double getAverageLearningProgramRating(Long schoolId, LocalDate fromDate, LocalDate toDate) {
        List<SchoolRating> ratings = findRatingsByDateRange(schoolId, fromDate, toDate);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToDouble(SchoolRating::getRating1)
                .average()
                .orElse(0.0);
    }

    public double getAverageFacilitiesRating(Long schoolId, LocalDate fromDate, LocalDate toDate) {
        List<SchoolRating> ratings = findRatingsByDateRange(schoolId, fromDate, toDate);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToDouble(SchoolRating::getRating2)
                .average()
                .orElse(0.0);
    }

    public double getAverageExtracurricularRating(Long schoolId, LocalDate fromDate, LocalDate toDate) {
        List<SchoolRating> ratings = findRatingsByDateRange(schoolId, fromDate, toDate);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToDouble(SchoolRating::getRating3)
                .average()
                .orElse(0.0);
    }

    public double getAverageTeachersRating(Long schoolId, LocalDate fromDate, LocalDate toDate) {
        List<SchoolRating> ratings = findRatingsByDateRange(schoolId, fromDate, toDate);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToDouble(SchoolRating::getRating4)
                .average()
                .orElse(0.0);
    }

    public double getAverageHygieneRating(Long schoolId, LocalDate fromDate, LocalDate toDate) {
        List<SchoolRating> ratings = findRatingsByDateRange(schoolId, fromDate, toDate);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToDouble(SchoolRating::getRating5)
                .average()
                .orElse(0.0);
    }

    public double getOverallAverageRating(Long schoolId, LocalDate fromDate, LocalDate toDate) {
        List<SchoolRating> ratings = findRatingsByDateRange(schoolId, fromDate, toDate);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToDouble(SchoolRating::getAvgRating)
                .average()
                .orElse(0.0);
    }

    public long getTotalFeedbacks(Long schoolId, LocalDate fromDate, LocalDate toDate) {
        return findRatingsByDateRange(schoolId, fromDate, toDate).size();
    }

    public Map<String, Object> getAllRatingData(Long schoolId, LocalDate fromDate, LocalDate toDate) {
        Map<String, Object> ratingData = new HashMap<>();

        ratingData.put("learningProgramRating", getAverageLearningProgramRating(schoolId, fromDate, toDate));
        ratingData.put("facilitiesRating", getAverageFacilitiesRating(schoolId, fromDate, toDate));
        ratingData.put("extracurricularRating", getAverageExtracurricularRating(schoolId, fromDate, toDate));
        ratingData.put("teachersRating", getAverageTeachersRating(schoolId, fromDate, toDate));
        ratingData.put("hygieneRating", getAverageHygieneRating(schoolId, fromDate, toDate));
        ratingData.put("averageRating", getOverallAverageRating(schoolId, fromDate, toDate));
        ratingData.put("totalFeedbacks", getTotalFeedbacks(schoolId, fromDate, toDate));
        ratingData.put("feedbacks", findRatingsByDateRange(schoolId, fromDate, toDate));

        return ratingData;
    }

    @Override
    public SchoolRating getRatingsByParentId(Long parentId) {
        return schoolRatingRepository.findFirstByParentId(parentId);
    }

    @Override
    public SchoolRating getRatingByParentAndSchool(Long parentId, Long schoolId) {
        return schoolRatingRepository.findByParentIdAndSchoolId(parentId, schoolId).get();
    }

    @Override
    @Transactional
    public SchoolRating createOrUpdateRating(SchoolRating sr) {
        User currentUser = authService.getCurrentUser().get();
        School school = schoolRepository.findById(sr.getSchool().getId()).get();
        SchoolRating rating = schoolRatingRepository.findByParentIdAndSchoolId(currentUser.getId(), school.getId())
                .orElse(new SchoolRating());
        rating.setParent(currentUser);
        rating.setSchool(school);
        rating.setRating1(sr.getRating1());
        rating.setRating2(sr.getRating2());
        rating.setRating3(sr.getRating3());
        rating.setRating4(sr.getRating4());
        rating.setRating5(sr.getRating5());
        rating.setFeedback(sr.getFeedback());
        rating.setCreatedAt(LocalDateTime.now());

        return schoolRatingRepository.save(rating);
    }

    @Override
    public SchoolAvgRating getAvgRatingBySchoolId(Long schoolId) {
        return schoolRatingRepository.getAvgRatingBySchoolId(schoolId);
    }

    @Override
    public List<SchoolRating> getTop4RatingsWithFiveStar() {
        return schoolRatingRepository.findTopRatings(PageRequest.of(0, 4));
    }

    @Override
    public boolean existsRating(Long parentId, Long schoolId) {
        return schoolRatingRepository.findByParentIdAndSchoolId(parentId, schoolId).isPresent();
    }

    @Override
    public Page<SchoolRatingDTO> getRatingBySchoolId(Long id, int page, int size, Integer stars) {
        if (stars == null) {
            return schoolRatingRepository.findBySchoolIdOrderByCreatedAtDesc(id, PageRequest.of(page, size));
        }
        return schoolRatingRepository.findBySchoolIdOrderByCreatedAtDesc(id, PageRequest.of(page, size), stars);
    }
    
}
