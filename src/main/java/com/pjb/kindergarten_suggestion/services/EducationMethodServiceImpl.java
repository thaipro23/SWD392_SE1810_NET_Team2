package com.pjb.kindergarten_suggestion.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pjb.kindergarten_suggestion.entities.EducationMethod;
import com.pjb.kindergarten_suggestion.repositories.EducationMethodRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EducationMethodServiceImpl implements EducationMethodService {
    private final EducationMethodRepository educationMethodRepository;
    @Override
    public List<EducationMethod> getAll() {
        return educationMethodRepository.findAll();
    }
}
