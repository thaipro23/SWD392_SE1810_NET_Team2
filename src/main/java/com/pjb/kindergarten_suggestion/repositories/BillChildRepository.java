package com.pjb.kindergarten_suggestion.repositories;

import com.pjb.kindergarten_suggestion.common.enums.BillStatus;
import com.pjb.kindergarten_suggestion.entities.BillChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BillChildRepository extends JpaRepository<BillChild, Long> {
    List<BillChild> findByChild_Parent_Id(Long parentId);

    @Query("SELECT b FROM BillChild b WHERE b.child.parent.id = :parentId AND " +
            "b.dateCreate BETWEEN :startDate AND :endDate")
    List<BillChild> findByParentIdAndDateRange(
            @Param("parentId") Long parentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<BillChild> findByChild_IdAndStatus(Long childId, BillStatus status);
}
