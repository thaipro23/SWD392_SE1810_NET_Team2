package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.Province;
import com.pjb.kindergarten_suggestion.repositories.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;

    @Override
    public List<Province> getAllProvinces() {
        return this.provinceRepository.findAll();
    }

    @Override
    public Province getProvinceById(int id) {
        return this.provinceRepository.findById(id).get();
    }
}
