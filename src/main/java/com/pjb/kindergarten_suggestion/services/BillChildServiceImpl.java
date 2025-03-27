package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.common.enums.BillStatus;
import com.pjb.kindergarten_suggestion.entities.BillChild;
import com.pjb.kindergarten_suggestion.repositories.BillChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillChildServiceImpl implements BillChildService {
    private final BillChildRepository billChildRepository;

    @Override
    public BillChild createBill(BillChild billChild) {
        billChild.setDateCreate(LocalDateTime.now());
        billChild.setStatus(BillStatus.UNPAID);
        return billChildRepository.save(billChild);
    }

    @Override
    public List<BillChild> findByParentId(Long parentId) {
        return billChildRepository.findByChild_Parent_Id(parentId);
    }

    @Override
    public List<BillChild> findByParentIdAndDate(Long parentId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return billChildRepository.findByParentIdAndDateRange(parentId, startOfDay, endOfDay);
    }

    @Override
    public List<BillChild> findByChildIdAndStatus(Long childId, BillStatus status) {
        return billChildRepository.findByChild_IdAndStatus(childId, status);
    }

    @Override
    public BillChild findById(Long id) {
        return billChildRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + id));
    }

    @Override
    public BillChild updateBill(BillChild billChild) {
        return billChildRepository.save(billChild);
    }
    @Override
    public List<BillChild> findAll(){
        return billChildRepository.findAll();
    }
}
