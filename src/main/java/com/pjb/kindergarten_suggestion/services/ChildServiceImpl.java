package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.Child;
import com.pjb.kindergarten_suggestion.repositories.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChildServiceImpl implements ChildService {
    private final ChildRepository childRepository;

    @Override
    public List<Child> findByUser(Long userId) {
        return childRepository.findByTeacher_Id(userId);
    }
    public List<Child> findByUserAndNotEvaluatedToday(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return childRepository.findByUserIdAndNotEvaluatedToday(userId, startOfDay, endOfDay);
    }
}