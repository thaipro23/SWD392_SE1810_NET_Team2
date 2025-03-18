package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.common.enums.EnrollStatus;
import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.exception.EnrollmentAlreadyExistsException;
import com.pjb.kindergarten_suggestion.common.exception.RequestAcceptedException;
import com.pjb.kindergarten_suggestion.common.exception.RequestCancelledException;
import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolEnrollment;
import com.pjb.kindergarten_suggestion.entities.SchoolRating;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.SchoolEnrollmentService;
import com.pjb.kindergarten_suggestion.services.SchoolRatingService;
import com.pjb.kindergarten_suggestion.services.SchoolService;
import com.pjb.kindergarten_suggestion.services.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class ParentManagementController {
    private final UserService userService;
    private final SchoolEnrollmentService schoolEnrollmentService;
    private final SchoolRatingService schoolRatingService;
    private final SchoolService schoolService;

    @GetMapping(AppRoutes.ADMIN_PARENT_MANAGEMENT)
    public String parentManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model) {
        model.addAttribute("pageTitle", "Parent Management");
        if (page < 0) {
            page = 0;
        }

        Page<User> usersPage = (keyword != null && !keyword.isEmpty())
                ? userService.getParentByKeyWithPagination(keyword, page, size)
                : userService.getParentWithPagination(page, size);

        int totalPages = usersPage.getTotalPages();

        if (totalPages > 0 && page >= totalPages) {
            page = totalPages - 1;
            usersPage = (keyword != null && !keyword.isEmpty())
                    ? userService.getParentByKeyWithPagination(keyword, page, size)
                    : userService.getParentWithPagination(page, size);
        }
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("totalPages", totalPages);
        }

        if (usersPage == null || usersPage.getTotalElements() == 0) {
            model.addAttribute("noUsersMessage", "No results found.");
            model.addAttribute("totalPages", 1);
        } else {
            model.addAttribute("totalPages", usersPage.getTotalPages());
        }
        Map<Long, EnrollStatus> enrollmentStatuses = usersPage.getContent().stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> schoolEnrollmentService.getEnrollmentStatusByParentId(user.getId())));

        model.addAttribute("enrollmentStatuses", enrollmentStatuses);

        return "pages/admin/parent/parent-list";
    }

    @GetMapping(AppRoutes.ADMIN_PARENT_DETAIL)
    public String getUserDetail(Model model, @PathVariable long id) {
        model.addAttribute("pageTitle", "Parent Detail");
        User user = this.userService.findUserById(id);
        model.addAttribute("user", user);
        List<SchoolEnrollment> enrollments = schoolEnrollmentService.getAllEnrollmentsByParentId(id);
        model.addAttribute("enrollments", enrollments);
        List<SchoolEnrollment> cancelledSchools = schoolEnrollmentService.getSchoolsCancelledByParentId(id);
        model.addAttribute("cancelledSchools", cancelledSchools);
        return "pages/admin/parent/parent-detail";
    }

    @GetMapping(AppRoutes.ADMIN_PARENT_ENROLL_ID)
    public String getEnrollParentPage(Model model, @PathVariable long id) {
        model.addAttribute("pageTitle", "Enroll Parent");
        User user = this.userService.findUserById(id);
        model.addAttribute("user", user);
        List<School> schoolsRequired = this.schoolService.getSchoolsRequiredByParentId(id);
        model.addAttribute("schools", schoolsRequired);
        return "pages/admin/parent/enroll-parent";
    }

    @PostMapping(AppRoutes.ADMIN_PARENT_ENROLL_ID)
    public String handleParentsSchool(@RequestParam("action") String action, Model model, @PathVariable long id,
            @RequestParam(name = "selectedSchoolId", required = false) Long selectedSchoolId,
            RedirectAttributes redirectAttributes) {
        if (selectedSchoolId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "School ID is missing.");
            return "redirect:" + AppRoutes.ADMIN_PARENT_ENROLL_ID.replace("{id}", String.valueOf(id));
        }
        if (action.equals("enroll")) {
            return handleEnrollParentToSchool(model, id, selectedSchoolId, redirectAttributes, Role.ADMIN);
        } else if (action.equals("cancel")) {
            return handleCancelParentToSchool(model, id, selectedSchoolId, redirectAttributes, Role.ADMIN);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Something is wrong!");
            return "redirect:" + AppRoutes.ADMIN_PARENT_DETAIL.replace("{id}", String.valueOf(id));
        }
    }

    public String handleEnrollParentToSchool(Model model, @PathVariable long id,
            @RequestParam(name = "selectedSchoolId", required = false) Long selectedSchoolId,
            RedirectAttributes redirectAttributes, Role myRole) {
        try {
            School selectedSchool = schoolService.getSchoolById(selectedSchoolId);
            User user = this.userService.findUserById(id);
            if (selectedSchool != null && user != null) {
                schoolEnrollmentService.enrollUserToSchool(selectedSchool, user);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Enrolled the parent successfully into the school.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Enrolled the parent not successfully into the school.");
            }
        } catch (RequestAcceptedException | EnrollmentAlreadyExistsException ra) {
            redirectAttributes.addFlashAttribute("errorMessage", ra.getMessage());
        }
        if (myRole.equals(Role.ADMIN))
            return "redirect:" + AppRoutes.ADMIN_PARENT_DETAIL.replace("{id}", String.valueOf(id));
        else if (myRole.equals(Role.SCHOOL_OWNER))
            return "redirect:" + AppRoutes.SCHOOL_OWNER_PARENT_DETAIL.replace("{id}", String.valueOf(id));
        else
            return null;
    }

    public String handleCancelParentToSchool(Model model, @PathVariable long id,
            @RequestParam(name = "selectedSchoolId", required = false) Long selectedSchoolId,
            RedirectAttributes redirectAttributes, Role myRole) {
        try {
            School selectedSchool = schoolService.getSchoolById(selectedSchoolId);
            User user = this.userService.findUserById(id);
            if (selectedSchool != null && user != null) {
                schoolEnrollmentService.cancelUserEnrollRequest(selectedSchool, user);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Cancelled enrollment of the parent successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cancelled the parent not successfully into the school.");
            }
        } catch (EnrollmentAlreadyExistsException | RequestCancelledException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        if (myRole.equals(Role.ADMIN))
            return "redirect:" + AppRoutes.ADMIN_PARENT_DETAIL.replace("{id}", String.valueOf(id));
        else if (myRole.equals(Role.SCHOOL_OWNER))
            return "redirect:" + AppRoutes.SCHOOL_OWNER_PARENT_DETAIL.replace("{id}", String.valueOf(id));
        else
            return null;
    }

    @PostMapping(AppRoutes.ADMIN_PARENT_UNENROLL_ID)
    public String handleUnenrolledParentToSchool(Model model, @PathVariable long id,
            @RequestParam("enrollmentId") Long enrollmentId,
            RedirectAttributes redirectAttributes) {
        try {
            schoolEnrollmentService.UnenrolledById(enrollmentId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully unenrolled the parent from the school.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to unenroll the parent from the school.");
        }
        return "redirect:" + AppRoutes.ADMIN_PARENT_DETAIL.replace("{id}", String.valueOf(id));
    }

    @PostMapping(AppRoutes.ADMIN_PARENT_UNCANCEL_ID)
    public String adminHandleUncancelledParentFromSchool(Model model,
            @PathVariable long id,
            @RequestParam("cancelledId") Long cancelledId,
            RedirectAttributes redirectAttributes) {
        try {
            schoolEnrollmentService.UncancelledById(cancelledId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully transferred the cancellation request to the pending list");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to uncancelled the parent from the school.");
        }
        return "redirect:" + AppRoutes.ADMIN_PARENT_DETAIL.replace("{id}", String.valueOf(id));
    }

    @GetMapping(AppRoutes.SCHOOL_OWNER_PARENT_MANAGEMENT)
    public String schoolOwnerParentManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model) {
        model.addAttribute("pageTitle", "Parent Management");
        if (page < 0) {
            page = 0;
        }

        Page<User> usersPage = (keyword != null && !keyword.isEmpty())
                ? userService.getParentByKeyInMySchool(keyword, page, size)
                : userService.getParentsInMySchool(page, size);

        int totalPages = usersPage.getTotalPages();

        if (totalPages > 0 && page >= totalPages) {
            page = totalPages - 1;
            usersPage = (keyword != null && !keyword.isEmpty())
                    ? userService.getParentByKeyInMySchool(keyword, page, size)
                    : userService.getParentsInMySchool(page, size);
        }
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("totalPages", totalPages);
        }

        if (usersPage == null || usersPage.getTotalElements() == 0) {
            model.addAttribute("noUsersMessage", "No results found.");
            model.addAttribute("totalPages", 1);
        } else {
            model.addAttribute("totalPages", usersPage.getTotalPages());
        }
        Map<Long, EnrollStatus> enrollmentStatuses = usersPage.getContent().stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> schoolEnrollmentService.getEnrollmentStatusByParentId(user.getId())));

        model.addAttribute("enrollmentStatuses", enrollmentStatuses);

        return "pages/admin/parent/parent-list";

    }

    @GetMapping(AppRoutes.SCHOOL_OWNER_PARENT_DETAIL)
    public String schoolOwnerGetUserDetail(Model model, @PathVariable long id) {
        model.addAttribute("pageTitle", "Parent Detail");
        User user = this.userService.findUserById(id);
        model.addAttribute("user", user);
        List<SchoolEnrollment> enrollments = schoolEnrollmentService.getMySchoolEnrollmentsByParentId(id);
        model.addAttribute("enrollments", enrollments);
        List<SchoolEnrollment> cancelledSchools = schoolEnrollmentService.getMySchoolCancelledByParentId(id);
        model.addAttribute("cancelledSchools", cancelledSchools);
        return "pages/admin/parent/parent-detail";
    }

    @GetMapping(AppRoutes.SCHOOL_OWNER_PARENT_ENROLL_ID)
    public String schoolOwnerGetEnrollParentPage(Model model, @PathVariable long id) {
        model.addAttribute("pageTitle", "Enroll Parent");
        User user = this.userService.findUserById(id);
        model.addAttribute("user", user);
        List<School> schoolsRequired = this.schoolService.getMySchoolsRequiredByParentId(id);
        model.addAttribute("schools", schoolsRequired);
        return "pages/admin/parent/enroll-parent";
    }

    @PostMapping(AppRoutes.SCHOOL_OWNER_PARENT_ENROLL_ID)
    public String schoolOwnerHandleEnrollParentToSchool(@RequestParam("action") String action, Model model,
            @PathVariable long id,
            @RequestParam(name = "selectedSchoolId", required = false) Long selectedSchoolId,
            RedirectAttributes redirectAttributes) {
        if (selectedSchoolId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "School ID is missing.");
            return "redirect:" + AppRoutes.SCHOOL_OWNER_PARENT_ENROLL_ID.replace("{id}", String.valueOf(id));
        }
        if (action.equals("enroll")) {
            return handleEnrollParentToSchool(model, id, selectedSchoolId, redirectAttributes, Role.SCHOOL_OWNER);
        } else if (action.equals("cancel")) {
            return handleCancelParentToSchool(model, id, selectedSchoolId, redirectAttributes, Role.SCHOOL_OWNER);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Something is wrong!");
            return "redirect:" + AppRoutes.SCHOOL_OWNER_PARENT_DETAIL.replace("{id}", String.valueOf(id));
        }
    }

    @PostMapping(AppRoutes.SCHOOL_OWNER_PARENT_UNENROLL_ID)
    public String schoolOwnerHandleUnenrolledParentFromSchool(Model model,
            @PathVariable long id,
            @RequestParam("enrollmentId") Long enrollmentId,
            RedirectAttributes redirectAttributes) {
        try {
            schoolEnrollmentService.UnenrolledById(enrollmentId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully unenrolled the parent from the school.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to unenrolled the parent from the school.");
        }
        return "redirect:" + AppRoutes.SCHOOL_OWNER_PARENT_DETAIL.replace("{id}", String.valueOf(id));
    }

    @PostMapping(AppRoutes.SCHOOL_OWNER_PARENT_UNCANCEL_ID)
    public String schoolOwnerHandleUncancelledParentFromSchool(Model model,
            @PathVariable long id,
            @RequestParam("cancelledId") Long cancelledId,
            RedirectAttributes redirectAttributes) {
        try {
            schoolEnrollmentService.UncancelledById(cancelledId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully transferred the cancellation request to the pending list");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to uncancelled the parent from the school.");
        }
        return "redirect:" + AppRoutes.SCHOOL_OWNER_PARENT_DETAIL.replace("{id}", String.valueOf(id));
    }

    @GetMapping("/api/enrollment/{enrollmentId}/rating")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRatingByEnrollment(@PathVariable Long enrollmentId) {
        try {
            SchoolEnrollment enrollment = schoolEnrollmentService.getEnrollmentById(enrollmentId);
            if (enrollment == null) {
                return ResponseEntity.notFound().build();
            }

            Long parentId = enrollment.getParent().getId();
            Long schoolId = enrollment.getSchool().getId();

            SchoolRating rating = schoolRatingService.getRatingByParentAndSchool(parentId, schoolId);
            Map<String, Object> response = new HashMap<>();

            if (rating != null) {
                response.put("rating", rating.getAvgRating());
                response.put("feedback", rating.getFeedback());
                response.put("hasRating", true);
            } else {
                response.put("hasRating", false);
                response.put("rating", 0);
                response.put("feedback", "No feedback");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
