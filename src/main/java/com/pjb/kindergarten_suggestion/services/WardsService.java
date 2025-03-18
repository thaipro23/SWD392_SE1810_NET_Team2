package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.Wards;

import java.util.List;

public interface WardsService {
    List<Wards> getAllWards();

    Wards getWardsById(int id);

    List<Wards> getWardsByDistrictId(int id);
}
