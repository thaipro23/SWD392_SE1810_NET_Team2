package com.pjb.kindergarten_suggestion.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pjb.kindergarten_suggestion.common.enums.RequestStatus;
import com.pjb.kindergarten_suggestion.dto.CounsellingRequestWithRatingDTO;
import com.pjb.kindergarten_suggestion.entities.CounsellingRequest;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.repositories.CounsellingRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CounsellingRequestServiceImpl implements CounsellingRequestService{

    public final CounsellingRequestRepository counsellingRequestRepository;

    @Override
    public Page<CounsellingRequest> getAllCounselingRequest(Pageable page) {
        return this.counsellingRequestRepository.findAll(page);
    }

    @Override
    public Page<CounsellingRequest> searchByKeyWord(String key, Pageable pageable) {
        return this.counsellingRequestRepository.findByKeyword(key, pageable);
    }

    @Override
    public Page<CounsellingRequest> getAllRequestBySchoolOwner(long id, Pageable pageable) {
        return this.counsellingRequestRepository.findBySchoolCreatorId(id, pageable);
    }

    @Override
    public Page<CounsellingRequest> searchByKeyWordInSchool(String key, Pageable pageable, long id) {
        return this.counsellingRequestRepository.findByKeywordInSchool(key, pageable, id);
    }

    @Override
    public CounsellingRequest getRequestById(long id) {
        return this.counsellingRequestRepository.findById(id);
    }

    @Override
    public CounsellingRequest EditRequest(CounsellingRequest counsellingRequest) {
        return this.counsellingRequestRepository.save(counsellingRequest);
    }

    @Override
    public Page<CounsellingRequest> getAllRequestReminderBySchoolOwner(long id, Pageable pageable) {
        return this.counsellingRequestRepository.findRequestReminderBySchoolCreator(id, pageable);
    }

    @Override
    public Page<CounsellingRequest> getAllRequestReminder(Pageable pageable, RequestStatus status) {
        return this.counsellingRequestRepository.findAllByStatus(pageable, status);
    }

    @Override
    public Page<CounsellingRequest> searchRequestReminderByKeyWordInSchool(String key, Pageable pageable, long id) {
        return this.counsellingRequestRepository.findRequestReminderByKeywordInSchool(key, pageable, id);
    }

    @Override
    public Page<CounsellingRequest> searchRequestReminderByKeyWord(String key, Pageable pageable) {
        return this.counsellingRequestRepository.findRequestReminderByKeyword(key, pageable);
    }

    @Override
    public Page<CounsellingRequest> getAllParentRequest(User parent, Pageable pageable) {
        return this.counsellingRequestRepository.findByParent(parent, pageable);
    }

    @Override
    public int countByParentAndStatus(User parent, RequestStatus status) {
        return this.counsellingRequestRepository.countByParentAndStatus(parent, status);
    }

    @Override
    public Page<CounsellingRequestWithRatingDTO> getCounsellingRequestWithRatingByParentId(long parentId,
            Pageable pageable) {
        return this.counsellingRequestRepository.getCounsellingRequestWithRatingByParentId(parentId, pageable);
    }
    
}
