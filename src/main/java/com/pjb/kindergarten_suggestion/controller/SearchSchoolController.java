package com.pjb.kindergarten_suggestion.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.pjb.kindergarten_suggestion.dto.SchoolSearchRequest;
import com.pjb.kindergarten_suggestion.dto.SchoolSearchResponse;
import com.pjb.kindergarten_suggestion.entities.CounsellingRequest;
import com.pjb.kindergarten_suggestion.entities.Province;
import com.pjb.kindergarten_suggestion.services.AuthService;
import com.pjb.kindergarten_suggestion.services.ChildAgeService;
import com.pjb.kindergarten_suggestion.services.DistrictService;
import com.pjb.kindergarten_suggestion.services.FacilityService;
import com.pjb.kindergarten_suggestion.services.ProvinceService;
import com.pjb.kindergarten_suggestion.services.SchoolService;
import com.pjb.kindergarten_suggestion.services.SchoolServiceImpl;
import com.pjb.kindergarten_suggestion.services.SchoolTypeService;
import com.pjb.kindergarten_suggestion.services.UtilitiesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SearchSchoolController {
    private final ProvinceService provinceService;
    private final DistrictService districtService;
    private final SchoolService schoolService;
    private final SchoolTypeService schoolTypeService;
    private final FacilityService facilityService;
    private final UtilitiesService utilityService;
    private final ChildAgeService childAgeService;
    public final AuthService authService;

    private static final int PAGE_SIZE = 10;

    @GetMapping("/search")
    public String showSearchPage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "School Search");
        loadReferenceData(model);

        SchoolSearchRequest searchRequest = (SchoolSearchRequest) session.getAttribute("searchRequest");
        SchoolSearchResponse searchResults = (SchoolSearchResponse) session.getAttribute("searchResults");

        if (searchRequest == null) {
            searchRequest = new SchoolSearchRequest();
            searchRequest.setSortBy("RATING");
        }

        if (searchResults != null) {
            populateModelWithSearchResults(model, searchResults, searchRequest, searchRequest.getSortBy());
        }

        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("request", new CounsellingRequest());
        return "pages/parent/search";
    }

    @PostMapping("/search")
    public String processSearch(
            @Valid @ModelAttribute("searchRequest") SchoolSearchRequest searchRequest,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {

        model.addAttribute("request", new CounsellingRequest());

        if (searchRequest.getProvinceId() != null) {
            model.addAttribute("districts", districtService.getDistrictsByProvinceId(searchRequest.getProvinceId()));
        }

        loadReferenceData(model);

        if ((searchRequest.getName() == null || searchRequest.getName().trim().isEmpty()) &&
                (searchRequest.getProvinceId() == null)) {
            model.addAttribute("errorMessage", "Please enter school name or select location to search");
            return "pages/parent/search";
        }
        if (searchRequest.getFeeTo() != null && searchRequest.getFeeFrom() != null
                && searchRequest.getFeeFrom() > searchRequest.getFeeTo()) {
            bindingResult.rejectValue("feeFrom", "error.feeFrom", "Minimum fee cannot be greater than maximum fee");
            return "pages/parent/search";
        }

        try {
            int page = Math.max(0, searchRequest.getPage() - 1);
            searchRequest.setPage(page);
            searchRequest.setSize(PAGE_SIZE);

            SchoolSearchResponse searchResults = schoolService.searchSchools(searchRequest);

            // Adjust current page for display (1-based)
            searchResults.setCurrentPage(searchResults.getCurrentPage() + 1);

            populateModelWithSearchResults(model, searchResults, searchRequest, searchRequest.getSortBy());
            session.setAttribute("searchRequest", searchRequest);
            session.setAttribute("searchResults", searchResults);

            return "redirect:/search";

        } catch (Exception e) {
            log.error("Error processing search request", e);
            model.addAttribute("errorMessage", "An error occurred during the search process.");
            return "error/404";
        }
    }

    @PostMapping("/homepage")
    private String searchByHomepage(@Valid @ModelAttribute("searchRequest") SchoolSearchRequest searchRequest,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {

        model.addAttribute("pageTitle", "Home");
        List<Province> provinces = provinceService.getAllProvinces();
        model.addAttribute("provinces", provinces);
        if ((searchRequest.getName() == null || searchRequest.getName().trim().isEmpty()) &&
                (searchRequest.getProvinceId() == null)) {
            model.addAttribute("errorMessage", "Please enter school name or select location to search");
            return "pages/homepage";
        }

        if (searchRequest.getProvinceId() != null) {
            model.addAttribute("districts", districtService.getDistrictsByProvinceId(searchRequest.getProvinceId()));
        }

        loadReferenceData(model);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please enter valid search criteria.");
            return "pages/homepage";
        }

        try {
            int page = Math.max(0, searchRequest.getPage() - 1);
            searchRequest.setPage(page);
            searchRequest.setSize(PAGE_SIZE);

            SchoolSearchResponse searchResults = schoolService.searchSchools(searchRequest);
            if (searchResults.getTotalElements() == 0) {

                model.addAttribute("errorMessage", "No schools match your criteria, please try again.");
                return "pages/homepage";
            }

            searchResults.setCurrentPage(searchResults.getCurrentPage() + 1);

            populateModelWithSearchResults(model, searchResults, searchRequest, searchRequest.getSortBy());

            model.addAttribute("request", new CounsellingRequest());
            session.setAttribute("searchRequest", searchRequest);
            session.setAttribute("searchResults", searchResults);

            return "redirect:/search";

        } catch (Exception e) {
            log.error("Error processing search request", e);
            model.addAttribute("errorMessage", "An error occurred during the search process.");
            return "error/404";
        }
    }

    private void populateModelWithSearchResults(Model model, SchoolSearchResponse searchResults,
            SchoolSearchRequest searchRequest, String sortBy) {
        model.addAttribute("schools", searchResults.getSchools());
        model.addAttribute("currentPage", searchResults.getCurrentPage());
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("totalItems", searchResults.getTotalElements());
        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("sortBy", sortBy);
    }

    private void loadReferenceData(Model model) {
        model.addAttribute("provinces", provinceService.getAllProvinces());

        model.addAttribute("sortOptions", SchoolServiceImpl.SortOption.values());

        model.addAttribute("schoolTypes", schoolTypeService.getAll());
        model.addAttribute("childAges", childAgeService.getAll());
        model.addAttribute("facilities", facilityService.getAll());
        model.addAttribute("utilities", utilityService.getAll());
    }

}
