package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.Child;
import com.pjb.kindergarten_suggestion.repositories.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChildServiceImpl implements ChildService {
    private final ChildRepository childRepository;

    @Override
    public List<Child> findByUser(Long userId) {
        return childRepository.findByUser_Id(userId);
    }

}