package com.pjb.kindergarten_suggestion.repositories;

import com.pjb.kindergarten_suggestion.entities.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findByTeacher_Id(Long userId);
    @Query("SELECT c FROM Child c WHERE c.teacher.id = :userId AND " +
            "NOT EXISTS (SELECT cn FROM ChildNote cn WHERE cn.student.id = c.id AND " +
            "cn.dateCreate BETWEEN :startOfDay AND :endOfDay)")
    List<Child> findByUserIdAndNotEvaluatedToday(@Param("userId") Long userId,
                                                 @Param("startOfDay") LocalDateTime startOfDay,
                                                 @Param("endOfDay") LocalDateTime endOfDay);

}
