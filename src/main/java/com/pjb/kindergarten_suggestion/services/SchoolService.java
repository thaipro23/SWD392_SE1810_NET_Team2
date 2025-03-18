package com.pjb.kindergarten_suggestion.services;

import java.util.List;

import com.pjb.kindergarten_suggestion.dto.AddressDTO;
import com.pjb.kindergarten_suggestion.dto.SchoolDTO;
import com.pjb.kindergarten_suggestion.dto.SchoolSearchResponse;
import com.pjb.kindergarten_suggestion.entities.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.common.enums.SchoolStatus;
import com.pjb.kindergarten_suggestion.dto.SchoolSearchRequest;
import org.springframework.data.domain.Sort;

public interface SchoolService {
    List<School> getSchoolsByStatus(SchoolStatus status);

    List<School> getSchoolsRequiredByParentId(Long parentId);

    List<School> getMySchoolsRequiredByParentId(Long parentId);

    List<School> getSchoolByCreatorId(Long creatorId);

    School getSchoolById(Long id);

    Page<School> getSchoolWithPaginationAndKey(Long id, String key, List<String> createdStatus,
            List<String> normalStatus, Pageable pageable);

    void ApproveSchool(Long id);

    void RejectSchool(Long id);

    void PublishSchool(Long id);

    void UnPublishSchool(Long id);

    SchoolSearchResponse searchSchools(SchoolSearchRequest request);

    Sort createSort(String sortBy);

    void submitSchool(Long id);

    SchoolDTO convertToDTO(School school);

    AddressDTO convertAddressToDTO(Address address);

    void sendSchoolSubmittedEmail(Long creatorId, Long schoolId);

    void sendSchoolApprovalEmail(Long creatorId, Long schoolId);

    void sendSchoolRejectedEmail(Long creatorId);

    void sendSchoolPublishEmail(Long creatorId, String schoolName, String publisherName, Long schoolId);

    School saveSchool(School school);

    long getMaximumSchoolId();

    void deleteSchool(Long id);

    String generateSchoolCode(String schoolName);
}
