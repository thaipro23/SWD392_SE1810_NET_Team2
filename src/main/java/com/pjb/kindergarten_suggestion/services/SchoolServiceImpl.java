package com.pjb.kindergarten_suggestion.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.pjb.kindergarten_suggestion.dto.AddressDTO;
import com.pjb.kindergarten_suggestion.dto.SchoolDTO;
import com.pjb.kindergarten_suggestion.dto.SchoolSearchRequest;
import com.pjb.kindergarten_suggestion.dto.SchoolSearchResponse;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;

import com.pjb.kindergarten_suggestion.common.enums.EnrollStatus;
import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.enums.SchoolStatus;
import com.pjb.kindergarten_suggestion.common.exception.ResourceNotFoundException;
import com.pjb.kindergarten_suggestion.repositories.SchoolEnrollmentRepository;
import com.pjb.kindergarten_suggestion.repositories.SchoolRepository;
import com.pjb.kindergarten_suggestion.repositories.UserRepository;
import com.pjb.kindergarten_suggestion.entities.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final JavaMailSender mailSender;
    private final SchoolEnrollmentRepository schoolEnrollmentRepository;

    @Override
    public List<School> getSchoolsByStatus(SchoolStatus status) {
        return schoolRepository.findByStatus(status);
    }

    @Override
    public School getSchoolById(Long id) {
        return schoolRepository.findById(id).get();
    }

    @Override
    public Page<School> getSchoolWithPaginationAndKey(Long id, String key, List<String> createdStatus,
            List<String> normalStatus, Pageable pageable) {
        User currentUser = authService.getCurrentUser().get();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        return schoolRepository.findByCreatorStatusAndKeyword(isAdmin, id, createdStatus, normalStatus,
                Normalizer.normalize(key, Normalizer.Form.NFD).replaceAll("\\p{M}", ""), pageable);
    }

    @Transactional
    @Override
    public void ApproveSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        Optional<User> currentUser = authService.getCurrentUser();
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            school.setAcceptor(user);
        }
        school.setStatus(SchoolStatus.APPROVED);
        school.setUpdatedAt(LocalDateTime.now());
        new Thread(() -> sendSchoolApprovalEmail(school.getCreator().getId(), school.getId())).start();
        schoolRepository.save(school);
    }

    @Transactional
    @Override
    public void RejectSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        Optional<User> currentUser = authService.getCurrentUser();
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            school.setAcceptor(user);
        }
        school.setStatus(SchoolStatus.REJECTED);
        school.setUpdatedAt(LocalDateTime.now());
        new Thread(() -> sendSchoolRejectedEmail(school.getCreator().getId())).start();
        schoolRepository.save(school);
    }

    @Transactional
    @Override
    public void PublishSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        Optional<User> currentUser = authService.getCurrentUser();
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            new Thread(() -> sendSchoolPublishEmail(school.getCreator().getId(), school.getName(), user.getFullname(),
                    school.getId())).start();
        }
        school.setPublishedAt(LocalDateTime.now());
        school.setStatus(SchoolStatus.PUBLISHED);
        school.setUpdatedAt(LocalDateTime.now());
        schoolRepository.save(school);
    }

    @Transactional
    @Override
    public void UnPublishSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        school.setStatus(SchoolStatus.UNPUBLISHED);
        school.setUpdatedAt(LocalDateTime.now());
        schoolRepository.save(school);
    }

    @Transactional
    @Override
    public void submitSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        school.setStatus(SchoolStatus.SUBMITTED);
        school.setUpdatedAt(LocalDateTime.now());
        new Thread(() -> sendSchoolSubmittedEmail(school.getCreator().getId(), school.getId())).start();
        schoolRepository.save(school);
    }

    @Override
    public void sendSchoolSubmittedEmail(Long creatorId, Long schoolId) {
        User creatorUser = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId.toString()));
        List<User> adminUsers = userRepository.findByRole(Role.ADMIN);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(creatorUser.getEmail());
            String[] adminEmails = adminUsers.stream()
                    .map(User::getEmail)
                    .toArray(String[]::new);

            helper.setTo(adminEmails);
            helper.setSubject("no-reply-email-KTS-system <School Submit Notification>");

            String baseUrl = "http://localhost:8080";
            String schoolListUrl = baseUrl + "/school-owner/schools/detail/" + schoolId;

            String htmlMsg = "<html>" +
                    "<body style=\"font-family: Arial, sans-serif;\">" +
                    "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                    "<h2 style=\"color: #007bff;\">School Submit Notification</h2>" +
                    "<p>This email is from KTS system,</p>" +
                    "<p>Hi " + "KTS admin" + ",</p>" +
                    "<p>There is a new school that is submitted and waiting for your review.</p>" +
                    "<div style=\"margin: 20px 0;\">" +
                    "<a href=\"" + schoolListUrl + "\" " +
                    "style=\"display: inline-block; padding: 10px 20px; " +
                    "color: white; background-color: #007bff; " +
                    "text-decoration: none; border-radius: 4px;\">" +
                    "Click here</a> to review" +
                    "</div>" +
                    "<p>Thanks & Regards!</p>" +
                    "<p>KTS Team</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send approval notification email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendSchoolApprovalEmail(Long creatorId, Long schoolId) {
        User schoolOwner = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId.toString()));

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(schoolOwner.getEmail());
            helper.setSubject("no-reply-email-KTS-system <School Approval Notification>");

            String baseUrl = "http://localhost:8080";
            String schoolListUrl = baseUrl + "/school-owner/schools/detail/" + schoolId;

            String htmlMsg = "<html>" +
                    "<body style=\"font-family: Arial, sans-serif;\">" +
                    "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                    "<h2 style=\"color: #007bff;\">School Approval Notification</h2>" +
                    "<p>This email is from KTS system,</p>" +
                    "<p>Hi " + schoolOwner.getFullname() + ",</p>" +
                    "<p>Your submitted school has just been approved by KTS admin.</p>" +
                    "<div style=\"margin: 20px 0;\">" +
                    "<a href=\"" + schoolListUrl + "\" " +
                    "style=\"display: inline-block; padding: 10px 20px; " +
                    "color: white; background-color: #007bff; " +
                    "text-decoration: none; border-radius: 4px;\">" +
                    "Click here</a> to view more details." +
                    "</div>" +
                    "<p>Thanks & Regards!</p>" +
                    "<p>KTS Team</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send approval notification email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendSchoolRejectedEmail(Long creatorId) {
        User schoolOwner = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId.toString()));
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(schoolOwner.getEmail());
            helper.setSubject("no-reply-email-KTS-system <School Rejection Notification>");

            String htmlMsg = "<html>" +
                    "<body style=\"font-family: Arial, sans-serif;\">" +
                    "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                    "<h2 style=\"color: #dc3545;\">School Rejection Notification</h2>" +
                    "<p>This email is from KTS system,</p>" +
                    "<p>Hi " + schoolOwner.getFullname() + ",</p>" +
                    "<p>Your submitted school has been rejected by KTS admin.</p>" +
                    "<p>Please contact us at <strong>(+84) 5555-5555</strong> for more details.</p>" +
                    "<p>Thanks & Regards!</p>" +
                    "<p>KTS Team</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send rejection notification email: " + e.getMessage(), e);
        }

    }

    @Override
    public void sendSchoolPublishEmail(Long creatorId, String schoolName, String publisherName, Long schoolId) {
        User schoolOwner = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId.toString()));

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(schoolOwner.getEmail());
            helper.setSubject("no-reply-email-KTS-system <School Publication Notification>");

            String baseUrl = "http://localhost:8080";
            String schoolListUrl = baseUrl + "/school-owner/schools/detail/" + schoolId;

            String htmlMsg = "<html>" +
                    "<body style=\"font-family: Arial, sans-serif;\">" +
                    "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                    "<h2 style=\"color: #28a745;\">School Publication Notification</h2>" +
                    "<p>This email is from KTS system,</p>" +
                    "<p>Hi " + schoolOwner.getFullname() + ",</p>" +
                    "<p>The " + schoolName + " school has just been published by " + publisherName + ".</p>" +
                    "<div style=\"margin: 20px 0;\">" +
                    "<a href=\"" + schoolListUrl + "\" " +
                    "style=\"display: inline-block; padding: 10px 20px; " +
                    "color: white; background-color: #28a745; " +
                    "text-decoration: none; border-radius: 4px;\">" +
                    "Click here</a> to view more details." +
                    "</div>" +
                    "<p>Thanks & Regards!</p>" +
                    "<p>KTS Team</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send publication notification email: " + e.getMessage(), e);
        }
    }

    @Override
    public School saveSchool(School school) {
        return schoolRepository.save(school);
    }

    @Override
    public long getMaximumSchoolId() {
        return schoolRepository.findMaximumSchoolId();
    }

    @Transactional
    @Override
    public void deleteSchool(Long id) {
        schoolRepository.softDelete(id);
    }

    public enum SortOption {
        RATING,
        NEWEST,
        FEE_HIGH_TO_LOW,
        FEE_LOW_TO_HIGH
    }

    @Override
    public SchoolSearchResponse searchSchools(SchoolSearchRequest request) {
        Sort sort = createSort(request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Set<Long> facilityIds = request.getFacilityIds() == null || request.getFacilityIds().isEmpty() ? null
                : request.getFacilityIds();
        Set<Long> utilityIds = request.getUtilityIds() == null || request.getUtilityIds().isEmpty() ? null
                : request.getUtilityIds();
        if (request.getName() != null) {
            request.setName(request.getName().trim());
        }
        Page<School> schoolPage = schoolRepository.searchSchools(
                request.getName(),
                request.getProvinceId(),
                request.getDistrictId(),
                request.getSchoolTypeId(),
                request.getChildAgeId(),
                request.getFeeFrom() != null ? request.getFeeFrom() * 1000000 : null,
                request.getFeeTo() != null ? request.getFeeTo() * 1000000 : null,
                facilityIds,
                utilityIds,
                pageable);
        // Long totalSchools = schoolRepository.countSchools(
        // request.getName(),
        // request.getProvinceId(),
        // request.getDistrictId(),
        // request.getSchoolTypeId(),
        // request.getChildAgeId(),
        // request.getFeeFrom(),
        // request.getFeeTo(),
        // request.getFacilityIds(),
        // request.getUtilityIds()
        // );
        Optional<User> currentUser = authService.getCurrentUser();
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            Map<Long, EnrollStatus> enrollmentStatuses = schoolEnrollmentRepository.findByParentId(user.getId())
                    .stream()
                    .collect(Collectors.toMap(enrollment -> enrollment.getSchool().getId(),
                            SchoolEnrollment::getStatus));
            List<SchoolDTO> schoolDTOs = schoolPage.getContent().stream()
                    .map(school -> {
                        SchoolDTO schoolDTO = convertToDTO(school);
                        EnrollStatus enrollStatus = enrollmentStatuses.getOrDefault(school.getId(),
                                EnrollStatus.UNENROLL);
                        schoolDTO.setEnrollStatus(enrollStatus);
                        return schoolDTO;
                    })
                    .collect(Collectors.toList());
            return SchoolSearchResponse.builder()
                    .schools(schoolDTOs)
                    .totalPages(schoolPage.getTotalPages())
                    .totalElements(schoolPage.getTotalElements())
                    .currentPage(schoolPage.getNumber())
                    .build();
        }

        List<SchoolDTO> schoolDTOs = schoolPage.getContent().stream()
                .map(school -> {
                    SchoolDTO schoolDTO = convertToDTO(school);
                    schoolDTO.setEnrollStatus(null);
                    return schoolDTO;
                })
                .collect(Collectors.toList());
        return SchoolSearchResponse.builder()
                .schools(schoolDTOs)
                .totalPages(schoolPage.getTotalPages())
                .totalElements(schoolPage.getTotalElements())
                .currentPage(schoolPage.getNumber())
                .build();
    }

    @Override
    public Sort createSort(String sortBy) {
        if (sortBy == null) {
            return Sort.by("totalRating").descending()
                    .and(Sort.by("totalRatingCount").descending());
        }

        try {
            SortOption option = SortOption.valueOf(sortBy.toUpperCase());

            switch (option) {
                case RATING:
                    return Sort.by("totalRating").descending()
                            .and(Sort.by("totalRatingCount").descending());
                case NEWEST:
                    return Sort.by("createdAt").descending();
                case FEE_HIGH_TO_LOW:
                    return Sort.by("feeFrom").descending();
                case FEE_LOW_TO_HIGH:
                    return Sort.by("feeFrom").ascending();
                default:
                    return Sort.by("totalRating").descending()
                            .and(Sort.by("totalRatingCount").descending());
            }
        } catch (IllegalArgumentException e) {
            // If invalid sort option provided, fall back to default sorting
            return Sort.by("totalRating").descending()
                    .and(Sort.by("totalRatingCount").descending());
        }
    }

    @Override
    public SchoolDTO convertToDTO(School school) {
        return SchoolDTO.builder()
                .id(school.getId())
                .name(school.getName())
                .schoolType(school.getSchoolType() != null ? school.getSchoolType().getName() : null)
                .email(school.getEmail())
                .phone(school.getPhone())
                .address(school.getAddress() != null ? convertAddressToDTO(school.getAddress()) : null)
                .childAge(school.getChildAge() != null ? school.getChildAge().getName() : null)
                .educationMethod(school.getEducationMethod() != null ? school.getEducationMethod().getName() : null)
                .feeFrom(school.getFeeFrom())
                .feeTo(school.getFeeTo())
                .facilities(school.getFacilities().stream()
                        .map(Facility::getName)
                        .collect(Collectors.toSet()))
                .utilities(school.getUtilities().stream()
                        .map(Utilities::getName)
                        .collect(Collectors.toSet()))
                .description(school.getDescription())
                .images(school.getImages().stream()
                        .map(ImageUrl::getUrl)
                        .collect(Collectors.toList()))
                .rating(school.getRating())
                .totalRatingCount(school.getTotalRatingCount())
                .schoolStatus(school.getStatus())
                .build();
    }

    @Override
    public AddressDTO convertAddressToDTO(Address address) {
        return AddressDTO.builder()
                .province(address.getProvince() != null ? address.getProvince().getName() : null)
                .district(address.getDistrict() != null ? address.getDistrict().getName() : null)
                .wards(address.getWards() != null ? address.getWards().getName() : null)
                .detail(address.getDetail())
                .build();
    }

    @Override
    public String generateSchoolCode(String schoolName) {
        schoolName = removeAccents(schoolName);
        String[] words = schoolName.split("\\s+");
        StringBuilder code = new StringBuilder("SCH-");
        for (String word : words) {
            if (word.isBlank())
                continue;
            code.append(word.charAt(0));
        }
        String codePrefix = code.toString().toUpperCase();
        long lastCode = schoolRepository.getLastSchoolCodeByPrefix(codePrefix) + 1;
        return codePrefix + lastCode;
    }

    private String removeAccents(String input) {
        input = input.replaceAll("đ", "d").replaceAll("Đ", "D");
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents;
    }

    @Override
    public List<School> getSchoolsRequiredByParentId(Long parentId) {
        return schoolRepository.findSchoolsRequiredByParentId(parentId);
    }

    @Override
    public List<School> getMySchoolsRequiredByParentId(Long parentId) {
        User currentUser = authService.getCurrentUser().get();
        Long creatorId = currentUser.getId();
        List<Long> schoolIds = getSchoolByCreatorId(creatorId)
                .stream()
                .map(School::getId)
                .collect(Collectors.toList());
        return schoolRepository.findMySchoolsRequiredByParentId(schoolIds, parentId);
    }

    @Override
    public List<School> getSchoolByCreatorId(Long creatorId) {
        User schoolOwner = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId.toString()));
        return schoolRepository.findByCreator(schoolOwner);
    }
}