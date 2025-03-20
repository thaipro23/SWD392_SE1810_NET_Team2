package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.ChildNote;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChildNoteService {
    List<ChildNote> findByTeacher(Long teacherId);
    List<ChildNote> findByTeacherAndDate(Long teacherId, LocalDate date);

    ChildNote findById(Long id);
    void save(ChildNote childNote);
    void delete(Long id);
    Optional<ChildNote> findByDate(LocalDate date);
}