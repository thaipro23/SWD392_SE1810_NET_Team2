package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.ChildNote;
import com.pjb.kindergarten_suggestion.repositories.ChildNoteRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChildNoteServiceImpl implements ChildNoteService {
    private final ChildNoteRepositoty childNoteRepository;


    @Override
    public Optional<ChildNote> findByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return childNoteRepository.findByDateCreateBetween(startOfDay, endOfDay);
    }
}
