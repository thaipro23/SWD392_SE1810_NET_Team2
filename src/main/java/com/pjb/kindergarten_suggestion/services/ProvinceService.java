package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.Province;

import java.util.List;

public interface ProvinceService {
    List<Province> getAllProvinces();

    Province getProvinceById(int id);
}
