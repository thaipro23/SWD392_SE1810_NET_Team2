package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.ChildNote;
import com.pjb.kindergarten_suggestion.entities.Parent;
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

    public List<ChildNote> findByTeacher(Long teacherId) {
        return childNoteRepository.findByUser_Id(teacherId);
    }
    @Override
    public List<ChildNote> findByTeacherAndDate(Long teacherId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return childNoteRepository.findByUser_IdAndDateCreateBetween(teacherId, startOfDay, endOfDay);
    }


    public ChildNote findById(Long id) {
        return childNoteRepository.findById(id).orElse(null);
    }

    public void save(ChildNote childNote) {
        childNoteRepository.save(childNote);
    }

    public void delete(Long id) {
        childNoteRepository.deleteById(id);
    }
    @Override
    public Optional<ChildNote> findByDateAndParent(LocalDate date, Long id) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return childNoteRepository.findByDateCreateBetweenAndUser_Id(startOfDay, endOfDay, id);
    }
}
