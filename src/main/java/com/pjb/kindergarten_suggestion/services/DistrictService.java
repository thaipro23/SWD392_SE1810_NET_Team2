package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.District;

import java.util.List;

public interface DistrictService {
    List<District> getAllDistricts();

    District getDistrictById(int id);

    List<District> getDistrictsByProvinceId(int id);
}
