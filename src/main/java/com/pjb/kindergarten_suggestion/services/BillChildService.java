package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.common.enums.BillStatus;
import com.pjb.kindergarten_suggestion.entities.BillChild;

import java.time.LocalDate;
import java.util.List;

public interface BillChildService {
    BillChild createBill(BillChild billChild);
    List<BillChild> findByParentId(Long parentId);
    List<BillChild> findByParentIdAndDate(Long parentId, LocalDate date);
    List<BillChild> findByChildIdAndStatus(Long childId, BillStatus status);
    BillChild findById(Long id);
    BillChild updateBill(BillChild billChild);
    List<BillChild> findAll();
}
