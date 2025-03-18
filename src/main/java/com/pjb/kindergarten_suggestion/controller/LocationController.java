package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.entities.District;
import com.pjb.kindergarten_suggestion.entities.Wards;
import com.pjb.kindergarten_suggestion.services.DistrictService;
import com.pjb.kindergarten_suggestion.services.WardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class LocationController {

    private final DistrictService districtService;
    private final WardsService wardsService;

    @GetMapping("/districts")
    public ResponseEntity<List<District>> getDistrictsByProvince(@RequestParam Integer provinceId) {
        List<District> districts = districtService.getDistrictsByProvinceId(provinceId);
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/wards")
    public ResponseEntity<List<Wards>> getWardsByDistrict(@RequestParam Integer districtId) {
        List<Wards> wards = wardsService.getWardsByDistrictId(districtId);
        return ResponseEntity.ok(wards);
    }
}