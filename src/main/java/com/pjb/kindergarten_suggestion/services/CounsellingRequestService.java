package com.pjb.kindergarten_suggestion.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pjb.kindergarten_suggestion.common.enums.RequestStatus;
import com.pjb.kindergarten_suggestion.dto.CounsellingRequestWithRatingDTO;
import com.pjb.kindergarten_suggestion.entities.CounsellingRequest;
import com.pjb.kindergarten_suggestion.entities.User;

public interface  CounsellingRequestService {
    Page <CounsellingRequest> getAllCounselingRequest(Pageable pageable);

    Page<CounsellingRequest> searchByKeyWord(String key, Pageable pageable);

    Page <CounsellingRequest> getAllRequestBySchoolOwner(long id, Pageable pageable);

    Page <CounsellingRequest> searchByKeyWordInSchool(String key, Pageable pageable, long id);

    CounsellingRequest getRequestById(long id);

    CounsellingRequest EditRequest(CounsellingRequest counsellingRequest);

    Page <CounsellingRequest> getAllRequestReminderBySchoolOwner(long id, Pageable pageable);

    Page <CounsellingRequest> getAllRequestReminder(Pageable pageable, RequestStatus status);

    Page <CounsellingRequest> searchRequestReminderByKeyWordInSchool(String key, Pageable pageable, long id);

    Page <CounsellingRequest> searchRequestReminderByKeyWord(String key, Pageable pageable);

    Page <CounsellingRequest> getAllParentRequest(User parent, Pageable pageable);

    int countByParentAndStatus(User parent, RequestStatus status);

    Page <CounsellingRequestWithRatingDTO> getCounsellingRequestWithRatingByParentId (long parentId, Pageable pageable);
}
