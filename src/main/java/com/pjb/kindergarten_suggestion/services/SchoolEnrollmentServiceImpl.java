package com.pjb.kindergarten_suggestion.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pjb.kindergarten_suggestion.common.enums.EnrollStatus;
import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.exception.EnrollmentAlreadyExistsException;
import com.pjb.kindergarten_suggestion.common.exception.RequestAcceptedException;
import com.pjb.kindergarten_suggestion.common.exception.RequestCancelledException;
import com.pjb.kindergarten_suggestion.dto.SchoolEnrollmentWithParentRatingDTO;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolEnrollment;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.repositories.SchoolEnrollmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolEnrollmentServiceImpl implements SchoolEnrollmentService {
    private final SchoolEnrollmentRepository schoolEnrollmentRepository;
    private final AuthService authService;
    private final SchoolService schoolService;

    @Override
    public List<User> getAllEnrolledParents() {
        return schoolEnrollmentRepository.findAllEnrolledParents(EnrollStatus.ENROLL);
    }

    @Override
    public EnrollStatus getEnrollmentStatusByParentId(Long parentId) {
        User currentUser = authService.getCurrentUser().get();
        Long creatorId = currentUser.getId();
        if (currentUser.getRole().equals(Role.SCHOOL_OWNER)) {
            List<Long> schoolIds = schoolService.getSchoolByCreatorId(creatorId)
                    .stream()
                    .map(School::getId)
                    .collect(Collectors.toList());
            List<SchoolEnrollment> enrollments = schoolEnrollmentRepository.findAllByParentIdAndStatusInMySchools(
                    parentId,
                    schoolIds);
            for (SchoolEnrollment enrollment : enrollments) {
                if (enrollment.getStatus().equals(EnrollStatus.ENROLL))
                    return EnrollStatus.ENROLL;
            }
            return EnrollStatus.UNENROLL;
        } else if (currentUser.getRole().equals(Role.ADMIN)) {
            return schoolEnrollmentRepository.findAllByParentIdAndStatus(parentId, EnrollStatus.ENROLL).size() > 0
                    ? EnrollStatus.ENROLL
                    : EnrollStatus.UNENROLL;
        } else {
            return null;
        }
    }

    @Override
    public SchoolEnrollment findEnrollmentsByParentId(Long parentId) {
        return schoolEnrollmentRepository.findFirstByParentIdAndStatus(parentId, EnrollStatus.ENROLL);
    }

    @Override
    public SchoolEnrollment enrollUserToSchool(School school, User parent) {
        Optional<SchoolEnrollment> existingEnrollment = schoolEnrollmentRepository
                .findByParentAndSchool(parent, school);
        if (existingEnrollment.isPresent()) {
            if (existingEnrollment.get().getStatus().equals(EnrollStatus.PENDING)) {
                SchoolEnrollment enrollment = existingEnrollment.get();
                enrollment.setStatus(EnrollStatus.ENROLL);
                enrollment.setUpdatedAt(LocalDateTime.now());
                enrollment.setEnrolledDate(LocalDate.now());
                return schoolEnrollmentRepository.save(enrollment);
            }
            throw new EnrollmentAlreadyExistsException("Parent has enrolled to this school!");
        }
        else{
            throw new RequestAcceptedException("Cannot accept the request!");
        }
    }

    @Override
    public SchoolEnrollment cancelUserEnrollRequest(School school, User parent) {
        Optional<SchoolEnrollment> existingEnrollment = schoolEnrollmentRepository
                .findByParentAndSchool(parent, school);
        if (existingEnrollment.isPresent()) {
            if (!existingEnrollment.get().getStatus().equals(EnrollStatus.CANCELLED)) {
                SchoolEnrollment enrollment = existingEnrollment.get();
                enrollment.setStatus(EnrollStatus.CANCELLED);
                enrollment.setUpdatedAt(LocalDateTime.now());
                return schoolEnrollmentRepository.save(enrollment);
            }
            throw new EnrollmentAlreadyExistsException("Parent has enrolled to this school!");
        }
        else{
            throw new RequestCancelledException("Cannot cancel the request!");
        }
    }

    @Override
    public void UnenrolledById(Long enrollmentId) {
        SchoolEnrollment enrollment = schoolEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with id: " + enrollmentId));

        enrollment.setStatus(EnrollStatus.UNENROLL);
        enrollment.setUpdatedAt(LocalDateTime.now());
        schoolEnrollmentRepository.save(enrollment);
    }

    @Override
    public void UncancelledById(Long enrollmentId) {
        SchoolEnrollment enrollment = schoolEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with id: " + enrollmentId));

        enrollment.setStatus(EnrollStatus.PENDING);
        enrollment.setUpdatedAt(LocalDateTime.now());
        schoolEnrollmentRepository.save(enrollment);
    }

    @Override
    public Page<SchoolEnrollmentWithParentRatingDTO> getAllSchoolEnrolledByParentIdAndStatus(Long parentId,
            EnrollStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return schoolEnrollmentRepository.findAllSchoolEnrolledbyParentIdAndStatus(parentId, status, pageable);
    }

    @Override
    public int countByParentAndStatus(User parent, EnrollStatus status) {
        return schoolEnrollmentRepository.countByParentAndStatus(parent, status);
    }

    @Override
    public List<SchoolEnrollment> getAllEnrollmentsByParentId(Long parentId) {
        return schoolEnrollmentRepository.findAllByParentIdAndStatus(parentId, EnrollStatus.ENROLL);
    }

    @Override
    public List<SchoolEnrollment> getMySchoolEnrollmentsByParentId(Long parentId) {
        User currentUser = authService.getCurrentUser().get();
        Long creatorId = currentUser.getId();
        List<Long> schoolIds = schoolService.getSchoolByCreatorId(creatorId)
                .stream()
                .map(School::getId)
                .collect(Collectors.toList());
        return schoolEnrollmentRepository.findMySchoolEnrollmentByParentIdAndStatus(parentId, schoolIds,
                EnrollStatus.ENROLL);
    }

    @Override
    public List<SchoolEnrollment> getMySchoolCancelledByParentId(Long parentId) {
        User currentUser = authService.getCurrentUser().get();
        Long creatorId = currentUser.getId();
        List<Long> schoolIds = schoolService.getSchoolByCreatorId(creatorId)
                .stream()
                .map(School::getId)
                .collect(Collectors.toList());
        return schoolEnrollmentRepository.findMySchoolEnrollmentByParentIdAndStatus(parentId, schoolIds,
                EnrollStatus.CANCELLED);
    }

    @Override
    public List<SchoolEnrollment> getSchoolsCancelledByParentId(Long parentId) {
        return schoolEnrollmentRepository.findSchoolsEnrollmentByParentIdAndStatus(parentId,
                EnrollStatus.CANCELLED);
    }

    @Override
    public SchoolEnrollment getEnrollmentById(Long id) {
        return schoolEnrollmentRepository.findById(id)
                .orElse(null);
    }

    @Override
    public void parentWannaEnrollToSchool(User parent, School school) {
        Optional<SchoolEnrollment> existingEnrollment = schoolEnrollmentRepository
                .findByParentAndSchool(parent, school);
        SchoolEnrollment enrollment;
        if (existingEnrollment.isPresent()) {
            enrollment = existingEnrollment.get();
            enrollment.setStatus(EnrollStatus.PENDING);
            enrollment.setUpdatedAt(LocalDateTime.now());
        } else {
            enrollment = new SchoolEnrollment();
            enrollment.setParent(parent);
            enrollment.setSchool(school);
            enrollment.setCreatedAt(LocalDateTime.now());
            enrollment.setUpdatedAt(LocalDateTime.now());
            enrollment.setStatus(EnrollStatus.PENDING);
        }
        schoolEnrollmentRepository.save(enrollment);
    }

    @Override
    public void parentWannaCancelEnrollToSchool(User parent, School school) {
        Optional<SchoolEnrollment> existingEnrollment = schoolEnrollmentRepository
                .findByParentAndSchool(parent, school);
        SchoolEnrollment enrollment = existingEnrollment.get();
        if (enrollment.getStatus().equals(EnrollStatus.PENDING)) {
            schoolEnrollmentRepository.delete(enrollment);
        } else if (enrollment.getStatus().equals(EnrollStatus.ENROLL)) {
            throw new RequestAcceptedException("Cannot cancel because already enrolled");
        } else if (enrollment.getStatus().equals(EnrollStatus.CANCELLED)) {
            throw new RequestCancelledException("Cannot cancel because already cancelled");
        }
    }

    @Override
    public boolean IsUserAllowedToRate(Long parentId, Long schoolId) {
        boolean canRate = schoolEnrollmentRepository.IsUserAllowedToRate(parentId, schoolId, EnrollStatus.ENROLL);
        return canRate;
    }

    @Override
    public SchoolEnrollment getMyEnrollment(School school) {
        Optional<User> currentUser = authService.getCurrentUser();
        if (currentUser.isPresent()) {
            Optional<SchoolEnrollment> schoolEnrollment = schoolEnrollmentRepository
                    .findByParentAndSchool(currentUser.get(), school);
            if (schoolEnrollment.isPresent())
                return schoolEnrollment.get();
            else
                return SchoolEnrollment.builder()
                        .status(EnrollStatus.UNENROLL)
                        .build();
        } else {
            return null;
        }
    }
}
