package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.District;
import com.pjb.kindergarten_suggestion.repositories.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {
    
    private final DistrictRepository districtRepository;

    @Override
    public List<District> getAllDistricts() {
        return this.districtRepository.findAll();
    }

    @Override
    public District getDistrictById(int id) {
        return this.districtRepository.findById(id).get();
    }

    @Override
    public List<District> getDistrictsByProvinceId(int provinceId) {
        return this.districtRepository.findByProvinceId(provinceId);
    }

}
