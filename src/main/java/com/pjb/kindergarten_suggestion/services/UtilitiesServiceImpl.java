package com.pjb.kindergarten_suggestion.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pjb.kindergarten_suggestion.entities.Utilities;
import com.pjb.kindergarten_suggestion.repositories.UtilitiesRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UtilitiesServiceImpl implements UtilitiesService {
    private final UtilitiesRepository utilitiesRepository;
    @Override
    public List<Utilities> getAll() {
        return utilitiesRepository.findAll();
    }
    
}
