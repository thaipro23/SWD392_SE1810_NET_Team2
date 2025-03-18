package com.pjb.kindergarten_suggestion.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pjb.kindergarten_suggestion.common.enums.EnrollStatus;
import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.dto.SchoolEnrollmentWithParentRatingDTO;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.AuthService;
import com.pjb.kindergarten_suggestion.services.SchoolEnrollmentService;

@RequiredArgsConstructor
@Controller
public class MySchoolsController {
    private final AuthService authService;
    private final SchoolEnrollmentService schoolEnrollmentService;

    @GetMapping(AppRoutes.PARENT_MY_SCHOOLS)
    public String getAllSchoolEnrolled(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int currentTab,
            Model model) {
        model.addAttribute("pageTitle", "My Schools");
        int actualPage = page - 1;
        if (actualPage < 0) {
            actualPage = 0;
            page = 1;
        }
        Optional<User> currentUser = authService.getCurrentUser();
        if (!currentUser.isPresent()) {
            return "redirect:" + AppRoutes.LOGIN;
        }
        Page<SchoolEnrollmentWithParentRatingDTO> schoolsPage;
        User user = currentUser.get();
        if (currentTab == 0) {
            schoolsPage = schoolEnrollmentService.getAllSchoolEnrolledByParentIdAndStatus(user.getId(),
                    EnrollStatus.ENROLL, actualPage, size);
        } else {
            schoolsPage = schoolEnrollmentService.getAllSchoolEnrolledByParentIdAndStatus(user.getId(),
                    EnrollStatus.UNENROLL, actualPage, size);
        }
        int countSchoolPrevious = schoolEnrollmentService.countByParentAndStatus(user, EnrollStatus.UNENROLL);
        model.addAttribute("schoolsPageCurrent", schoolsPage);
        model.addAttribute("schoolsPagePrevious", schoolsPage);
        model.addAttribute("totalPages", schoolsPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("count", convertNumberToWords(countSchoolPrevious));
        model.addAttribute("currentTab", currentTab);
        return "pages/parent/parent-my-school";
    }

    private String convertNumberToWords(int number) {
        String[] ones = { "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
        String[] teens = { "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
                "eighteen", "nineteen" };
        String[] tens = { "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety" };

        if (number == 0) {
            return "zero";
        }

        String result = "";

        if (number >= 100) {
            result += ones[number / 100] + " hundred ";
            number %= 100;
        }

        if (number >= 20) {
            result += tens[number / 10] + " ";
            number %= 10;
        }

        if (number >= 10) {
            result += teens[number - 10] + " ";
        } else if (number > 0) {
            result += ones[number] + " ";
        }

        return result.trim();
    }

}
