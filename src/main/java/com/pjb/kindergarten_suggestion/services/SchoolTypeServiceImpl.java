package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.SchoolType;
import com.pjb.kindergarten_suggestion.repositories.SchoolTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SchoolTypeServiceImpl implements SchoolTypeService{
    private final SchoolTypeRepository schoolTypeRepository;

    @Override
    public List<SchoolType> getAll() {
        return schoolTypeRepository.findAll();
    }
}
