package com.pjb.kindergarten_suggestion.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pjb.kindergarten_suggestion.entities.Facility;
import com.pjb.kindergarten_suggestion.repositories.FacilityRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;
    @Override
    public List<Facility> getAll() {
        return facilityRepository.findAll();
    }
    
}
