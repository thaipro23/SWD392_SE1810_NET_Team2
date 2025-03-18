package com.pjb.kindergarten_suggestion.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pjb.kindergarten_suggestion.entities.ChildAge;
import com.pjb.kindergarten_suggestion.repositories.ChildAgeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChildAgeServiceImpl implements ChildAgeService {
    private final ChildAgeRepository childAgeRepository;
    @Override
    public List<ChildAge> getAll() {
        return childAgeRepository.findAll();
    }
    
}
