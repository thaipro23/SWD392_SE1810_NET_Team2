package com.pjb.kindergarten_suggestion.repositories;

import com.pjb.kindergarten_suggestion.entities.ChildNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChildNoteRepositoty extends JpaRepository<ChildNote, Long> {
    Optional<ChildNote> findByDateCreateBetween(LocalDateTime start, LocalDateTime end);
    List<ChildNote> findByUser_Id(Long userId);
    List<ChildNote> findByUser_IdAndDateCreateBetween(Long userId, LocalDateTime start, LocalDateTime end);

}
