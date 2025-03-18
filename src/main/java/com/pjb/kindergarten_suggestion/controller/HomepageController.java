package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.dto.SchoolSearchRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import com.pjb.kindergarten_suggestion.entities.Province;
import com.pjb.kindergarten_suggestion.entities.SchoolRating;
import com.pjb.kindergarten_suggestion.services.ProvinceService;
import com.pjb.kindergarten_suggestion.services.SchoolRatingService;

@Controller
@RequiredArgsConstructor
public class HomepageController {
    private final ProvinceService provinceService;
    private final SchoolRatingService schoolRatingService;

    @GetMapping("/homepage")
    public String getHomePage(Model model) {
        model.addAttribute("pageTitle", "Home");
        List<Province> provinces = provinceService.getAllProvinces();
        model.addAttribute("provinces", provinces);
        SchoolSearchRequest searchRequest = new SchoolSearchRequest();

        model.addAttribute("searchRequest", searchRequest);
        List<SchoolRating> ratings = schoolRatingService.getTop4RatingsWithFiveStar();
        model.addAttribute("ratings", ratings);
        return "/pages/homepage";
    }

}
