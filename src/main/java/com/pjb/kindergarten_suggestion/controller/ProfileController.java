package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.dto.ParentProfileDTO;
import com.pjb.kindergarten_suggestion.entities.*;
import com.pjb.kindergarten_suggestion.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;
    private final ProvinceService provinceService;
    private final DistrictService districtService;
    private final WardsService wardsService;

    @GetMapping(AppRoutes.PROFILE)
    public String viewProfile(Model model, Principal principal) {
        model.addAttribute("pageTitle", "Profile");
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            throw new AccessDeniedException("You can access this page");
        }
        ParentProfileDTO user_change = userService.UserToParentProfileDTO(currentUser);
        Address currentAddress = currentUser.getAddress();
        if (currentAddress != null) {
            Integer provinceId = currentAddress.getProvince() != null ? currentAddress.getProvince().getId() : null;
            Integer districtId = currentAddress.getDistrict() != null ? currentAddress.getDistrict().getId() : null;
            Integer wardId = currentAddress.getWards() != null ? currentAddress.getWards().getId() : null;
            populateModelWithAddressData(model, provinceId, districtId, wardId);
        } else {
            populateModelWithAddressData(model, null, null, null); // dropdowns
        }
        model.addAttribute("user", user_change);
        return "pages/auth/profile";
    }

    @PostMapping(AppRoutes.PROFILE)
    public String updateProfile(@Valid @ModelAttribute("user") ParentProfileDTO profileDTO,
            BindingResult result, Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam(name = "wardId", required = false) Integer wardId,
            @RequestParam(name = "provinceId", required = false) Integer provinceId,
            @RequestParam(name = "districtId", required = false) Integer districtId,
            Principal principal) {
        if (result.hasErrors()) {
            return backToProfile(provinceId, wardId, districtId, profileDTO, model);
        }
        try {
            Address newAddress = buildAddressFromIds(provinceId, districtId, wardId,
                    profileDTO.getAddress().getDetail());
            profileDTO.setAddress(newAddress);
            userService.updateParentProfile(principal.getName(), profileDTO);
            redirectAttributes.addFlashAttribute("message", "Update successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:" + AppRoutes.PROFILE;
    }

    private String backToProfile(Integer provinceId, Integer wardId, Integer districtId, ParentProfileDTO profileDTO,
            Model model) {
        Address newAddress = buildAddressFromIds(provinceId, districtId, wardId, profileDTO.getAddress().getDetail());
        profileDTO.setAddress(newAddress);
        model.addAttribute("user", profileDTO);
        populateModelWithAddressData(model, provinceId, districtId, wardId); // dropdowns
        return "pages/auth/profile";
    }

    private Address buildAddressFromIds(Integer provinceId, Integer districtId, Integer wardId, String detail) {
        Province province;
        Wards ward;
        District district;
        if (provinceId == null) {
            province = new Province();
        } else {
            province = provinceService.getProvinceById(provinceId) == null ? new Province()
                    : provinceService.getProvinceById(provinceId);
        }
        if (wardId == null) {
            ward = new Wards();
        } else {
            ward = wardsService.getWardsById(wardId) == null ? new Wards()
                    : wardsService.getWardsById(wardId);
        }
        if (districtId == null) {
            district = new District();
        } else {
            district = districtService.getDistrictById(districtId) == null ? new District()
                    : districtService.getDistrictById(districtId);
        }
        Address address = new Address();
        address.setProvince(province);
        address.setDistrict(district);
        address.setWards(ward);
        address.setDetail(detail);
        return address;
    }

    private void populateModelWithAddressData(Model model, Integer provinceId, Integer districtId, Integer wardId) {
        List<Province> provinces = provinceService.getAllProvinces();
        model.addAttribute("provinces", provinces);
        if (provinceId != null) {
            List<District> districts = districtService.getDistrictsByProvinceId(provinceId);
            model.addAttribute("districts", districts);

            if (districtId != null) {
                List<Wards> wards = wardsService.getWardsByDistrictId(districtId);
                model.addAttribute("wards", wards);
            } else {
                model.addAttribute("wards", List.of());
            }
        } else {
            model.addAttribute("districts", List.of());
            model.addAttribute("wards", List.of());
        }
    }

    @GetMapping(AppRoutes.CHANGE_PASSWORD)
    public String getChangePasswordPage(Model model) {
        model.addAttribute("pageTitle", "Change Password");
        return "pages/auth/change-password";
    }

    @PostMapping(AppRoutes.CHANGE_PASSWORD)
    public String handleChangePassword(Model model, Principal principal,
            RedirectAttributes redirectAttributes,
            @RequestParam("curPass") String curPass,
            @RequestParam("newPass") String newPass,
            @RequestParam("cfPass") String cfPass) {
        try {
            String username = principal.getName();
            User currentUser = userService.getUserByUsername(username);
            if (currentUser == null) {
                throw new AccessDeniedException("You can access this page");
            }
            if (userService.isCurrentPassword(currentUser, curPass)) {
                if (!newPass.matches("^(?=.*\\d)(?=.*[a-zA-Z]).{7,}$")) {
                    throw new Exception();
                }
                if (newPass.equals(cfPass)) {
                    userService.updatePassword(currentUser, newPass);
                    redirectAttributes.addFlashAttribute("message", "Password changed successfully");
                } else {
                    redirectAttributes.addFlashAttribute("error", "New password and confirm password don't match");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "New password must contain at least one number, one numeral, and seven characters.");
        }
        return "redirect:" + AppRoutes.CHANGE_PASSWORD;
    }
}
