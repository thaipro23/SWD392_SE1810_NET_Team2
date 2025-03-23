package com.pjb.kindergarten_suggestion.repositories;

import com.pjb.kindergarten_suggestion.entities.ChildNote;
import com.pjb.kindergarten_suggestion.entities.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChildNoteRepositoty extends JpaRepository<ChildNote, Long> {
    @Query("SELECT cn FROM ChildNote cn " +
            "WHERE cn.dateCreate BETWEEN :start AND :end " +
            "AND cn.student.parent.id = :parentId")
    Optional<ChildNote> findByDateCreateBetweenAndParentId(@Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end,
                                                           @Param("parentId") Long parentId);
    List<ChildNote> findByUser_Id(Long userId);
    List<ChildNote> findByUser_IdAndDateCreateBetween(Long userId, LocalDateTime start, LocalDateTime end);

}
