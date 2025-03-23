package com.pjb.kindergarten_suggestion.repositories;

import com.pjb.kindergarten_suggestion.entities.ChildNote;
import com.pjb.kindergarten_suggestion.entities.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChildNoteRepositoty extends JpaRepository<ChildNote, Long> {
    Optional<ChildNote> findByDateCreateBetweenAndUser_Id(LocalDateTime start, LocalDateTime end, Long id);
    List<ChildNote> findByUser_Id(Long userId);
    List<ChildNote> findByUser_IdAndDateCreateBetween(Long userId, LocalDateTime start, LocalDateTime end);

}
