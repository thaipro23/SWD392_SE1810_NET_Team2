package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.common.enums.SchoolStatus;
import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.dto.SchoolRatingDTO;
import com.pjb.kindergarten_suggestion.entities.CounsellingRequest;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolEnrollment;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.AuthService;
import com.pjb.kindergarten_suggestion.services.FacilityService;
import com.pjb.kindergarten_suggestion.services.SchoolEnrollmentService;
import com.pjb.kindergarten_suggestion.services.SchoolRatingService;
import com.pjb.kindergarten_suggestion.services.SchoolService;
import com.pjb.kindergarten_suggestion.services.UtilitiesService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ParentSchoolDetailController {

  private final SchoolService schoolService;
  private final FacilityService facilityService;
  private final UtilitiesService utilitiesService;
  private final SchoolRatingService schoolRatingService;
  private final AuthService authService;
  private final SchoolEnrollmentService schoolEnrollmentService;

  @GetMapping(AppRoutes.PARENT_SCHOOL_DETAIL)
  public String getMethodName(Model model, @PathVariable("id") String id) {
    model.addAttribute("pageTitle", "School Detail");
    try {
      Long schoolId = Long.valueOf(id);
      School school = schoolService.getSchoolById(schoolId);
      if (!school.getStatus().equals(SchoolStatus.PUBLISHED)) {
        model.addAttribute("error", getErrorBySchoolStatus(school.getStatus()));
        return "error/school-not-found";
      }
      SchoolEnrollment myEnrollment = schoolEnrollmentService.getMyEnrollment(school);
      model.addAttribute("myEnrollment", myEnrollment);
      model.addAttribute("school", school);
      model.addAttribute("facilities", facilityService.getAll());
      model.addAttribute("utilities", utilitiesService.getAll());
      model.addAttribute(
          "ratingName",
          List.of(
              "Learning program",
              "Facilities and Utilities",
              "Extracurricular Activities",
              "Teachers and Staff",
              "Hygiene and Nutrition"));
      model.addAttribute("avgRating", schoolRatingService.getAvgRatingBySchoolId(schoolId));
      model.addAttribute("request", new CounsellingRequest());
      setupForRatingBtn(model, schoolId);
    } catch (Exception e) {
      e.printStackTrace();
      return "error/404";
    }
    return "pages/parent/school-detail";
  }

  @GetMapping("/api/school/rating")
  @ResponseBody
  public Page<SchoolRatingDTO> getRating(Long schoolId,int page, int size, Integer stars) {
    return schoolRatingService.getRatingBySchoolId(schoolId, page, size, stars);
  }

  private String getErrorBySchoolStatus(SchoolStatus status) {
    String error;
    switch (status) {
      case SAVED, APPROVED, SUBMITTED:
        error = "School is not available(Pending)";
        break;
      case DELETED:
        error = "School has been deleted";
        break;
      case REJECTED:
        error = "School has been banned by admin";
        break;
      case UNPUBLISHED:
        error = "School has been hidden";
        break;
      default:
        error = "School is not Found";
        break;
    }
    return error;
  }

  private void setupForRatingBtn(Model model, Long id) {
    Optional<User> currentUser = authService.getCurrentUser();
    Long userId = null;
    if (currentUser.isPresent()) {
      userId = currentUser.get().getId();
    }
    model.addAttribute(
        "IsUserAllowedToRate",
        userId != null && schoolEnrollmentService.IsUserAllowedToRate(userId, id));
    model.addAttribute(
        "IsUserAlreadyHasRating", userId != null && schoolRatingService.existsRating(userId, id));
  }
}
