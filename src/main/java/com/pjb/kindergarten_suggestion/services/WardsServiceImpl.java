package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.Wards;
import com.pjb.kindergarten_suggestion.repositories.WardsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WardsServiceImpl implements WardsService {
    
    private final WardsRepository wardsRepository;

    @Override
    public List<Wards> getAllWards() {
        return this.wardsRepository.findAll();
    }

    @Override
    public Wards getWardsById(int id) {
        return this.wardsRepository.findById(id).get();
    }

    @Override
    public List<Wards> getWardsByDistrictId(int districtId) {
        return this.wardsRepository.findByDistrictId(districtId);
    }

}
