package com.pjb.kindergarten_suggestion.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolRating;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.repositories.SchoolRepository;
import com.pjb.kindergarten_suggestion.services.AuthService;
import com.pjb.kindergarten_suggestion.services.SchoolEnrollmentService;
import com.pjb.kindergarten_suggestion.services.SchoolRatingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RatingsAndFeedbackController {

    private final SchoolRatingService schoolRatingService;
    private final AuthService authService;
    private final SchoolRepository schoolRepository;
    private final SchoolEnrollmentService schoolEnrollmentService;

    @GetMapping(AppRoutes.ADMIN_SCHOOL_RATING)
    public String viewRatings(
            @PathVariable("id") Long schoolId,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        model.addAttribute("pageTitle", "Ratings and Feedbacks");

        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            model.addAttribute("errorMessage",
                    "Invalid date range. 'To date' must be the same as or after 'From date' and both cannot be in the future.");
            return "error/error-page";
        } else {
            if (fromDate == null) {
                fromDate = LocalDate.of(2000, 1, 1);
            }
            if (toDate == null) {
                toDate = LocalDate.now();
            }
            Map<String, Object> ratingData = schoolRatingService.getAllRatingData(schoolId, fromDate, toDate);
            model.addAllAttributes(ratingData);
            model.addAttribute("fromDate", fromDate);
            model.addAttribute("schoolId", schoolId);
            model.addAttribute("toDate", toDate);
        }
        return "pages/school/view-ratings-and-feedbacks"; // View template name
    }

    @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL_RATING)
    public String viewRatingsSchool(
            @PathVariable("id") Long schoolId,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        return viewRatings(schoolId, fromDate, toDate, model);
    }

    @GetMapping(AppRoutes.PARENT_MY_SCHOOLS_RATING_DETAIL)
    public ResponseEntity<SchoolRating> getRating(@PathVariable("id") Long schoolId) {
        Optional<User> currentUser = authService.getCurrentUser();
        if (!currentUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = currentUser.get();
        SchoolRating rating = schoolRatingService.getRatingByParentAndSchool(user.getId(), schoolId);
        if (rating == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rating);
    }

    @PostMapping(AppRoutes.PARENT_MY_SCHOOLS_RATING + "{schoolId}")
    public ResponseEntity<SchoolRating> createOrUpdateRating(@PathVariable("schoolId") Long schoolId,
            @RequestBody SchoolRating rating) {
        Optional<User> currentUser = authService.getCurrentUser();
        if (!currentUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = currentUser.get();
        if (!schoolEnrollmentService.IsUserAllowedToRate(user.getId(), schoolId)) {
            return ResponseEntity.status(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS).build();
        }
        // Kiểm tra tất cả các rating
        int[] ratings = { rating.getRating1(), rating.getRating2(), rating.getRating3(), rating.getRating4(),
                rating.getRating5() };
        for (int i = 0; i < ratings.length; i++) {
            if (ratings[i] < 1 || ratings[i] > 5) {
                return ResponseEntity.badRequest().build();
            }
        }
        // Kiểm tra feedback
        if (rating.getAvgRating() != 5) {
            String feedback = rating.getFeedback();
            if (feedback == null || feedback.replaceAll("[^a-zA-Z]", "").length() < 10) {
                return ResponseEntity.badRequest().build();
            }
        }
        School school = schoolRepository.findById(schoolId).get();
        rating.setSchool(school);
        SchoolRating savedRating = schoolRatingService.createOrUpdateRating(rating);
        return ResponseEntity.ok(savedRating);
    }

}
