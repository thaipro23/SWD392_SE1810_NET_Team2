package com.pjb.kindergarten_suggestion.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.pjb.kindergarten_suggestion.common.enums.EnrollStatus;
import com.pjb.kindergarten_suggestion.dto.SchoolEnrollmentWithParentRatingDTO;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolEnrollment;
import com.pjb.kindergarten_suggestion.entities.User;

public interface SchoolEnrollmentService {
    List<User> getAllEnrolledParents();

    EnrollStatus getEnrollmentStatusByParentId(Long parentId);

    public SchoolEnrollment findEnrollmentsByParentId(Long parentId);

    SchoolEnrollment enrollUserToSchool(School school, User parent);

    SchoolEnrollment cancelUserEnrollRequest(School school, User parent);

    void UnenrolledById(Long enrollmentId);

    void UncancelledById(Long enrollmentId);

    Page<SchoolEnrollmentWithParentRatingDTO> getAllSchoolEnrolledByParentIdAndStatus(Long parentId,
            EnrollStatus status, int page, int size);

    int countByParentAndStatus(User parent, EnrollStatus status);

    List<SchoolEnrollment> getAllEnrollmentsByParentId(Long parentId);

    public SchoolEnrollment getEnrollmentById(Long id);

    List<SchoolEnrollment> getMySchoolEnrollmentsByParentId(Long parentId);

    List<SchoolEnrollment> getMySchoolCancelledByParentId(Long parentId);

    List<SchoolEnrollment> getSchoolsCancelledByParentId(Long parentId);

    void parentWannaEnrollToSchool(User parent, School school);

    void parentWannaCancelEnrollToSchool(User parent, School school);

    boolean IsUserAllowedToRate(Long parentId, Long schoolId);

    SchoolEnrollment getMyEnrollment(School school);
}
