package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.ChildNote;

import java.time.LocalDate;
import java.util.Optional;

public interface ChildNoteService {
    Optional<ChildNote> findByDate(LocalDate date);
}
