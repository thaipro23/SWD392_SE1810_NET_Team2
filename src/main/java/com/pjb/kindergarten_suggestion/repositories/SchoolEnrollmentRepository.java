package com.pjb.kindergarten_suggestion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pjb.kindergarten_suggestion.common.enums.EnrollStatus;
import com.pjb.kindergarten_suggestion.dto.SchoolEnrollmentWithParentRatingDTO;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.SchoolEnrollment;
import com.pjb.kindergarten_suggestion.entities.User;

public interface SchoolEnrollmentRepository extends JpaRepository<SchoolEnrollment, Long> {
        @Query("SELECT se.parent FROM SchoolEnrollment se WHERE se.status = :status")
        List<User> findAllEnrolledParents(@Param("status") EnrollStatus status);

        SchoolEnrollment findFirstByParentId(long parentId);

        List<SchoolEnrollment> findAllByParentId(Long parentId);

        SchoolEnrollment findFirstByParentIdAndStatus(Long parentId, EnrollStatus enrollStatus);

        List<SchoolEnrollment> findAllByParentIdAndStatus(Long parentId, EnrollStatus enrollStatus);

        @Query("""
                            SELECT se FROM SchoolEnrollment se
                            WHERE se.parent.id = :parentId
                            AND se.school.id IN :schoolIds
                            AND (se.status = 'ENROLL'
                            OR se.status = 'PENDING'
                            OR se.status = 'CANCELLED')
                        """)
        List<SchoolEnrollment> findAllByParentIdAndStatusInMySchools(Long parentId,
                        @Param("schoolIds") List<Long> schoolIds);

        @Query("""
                        SELECT new com.pjb.kindergarten_suggestion.dto.SchoolEnrollmentWithParentRatingDTO(
                        se,
                        coalesce(avg((sr.rating1 + sr.rating2 + sr.rating3 + sr.rating4 + sr.rating5)/ 5),0.0),
                        coalesce(count(sr),0)
                        )
                        FROM SchoolEnrollment se
                        LEFT JOIN SchoolRating sr ON se.school = sr.school AND se.parent = sr.parent
                        WHERE se.parent.id = :parentId AND se.status = :status
                        group by se
                        order by se.enrolledDate desc
                        """)
        Page<SchoolEnrollmentWithParentRatingDTO> findAllSchoolEnrolledbyParentIdAndStatus(
                        @Param("parentId") long parentId,
                        @Param("status") EnrollStatus status, Pageable pageable);

        int countByParentAndStatus(User parent, EnrollStatus status);

        Optional<SchoolEnrollment> findByParentAndSchool(User parent, School school);

        @Query("""
                            SELECT se FROM SchoolEnrollment se
                            WHERE se.parent.id = :parentId
                            AND se.school.id IN :schoolIds
                            AND se.status = :status
                        """)
        List<SchoolEnrollment> findMySchoolEnrollmentByParentIdAndStatus(@Param("parentId") Long parentId,
                        @Param("schoolIds") List<Long> schoolIds,
                        @Param("status") EnrollStatus status);

        @Query("""
                            SELECT se FROM SchoolEnrollment se
                            WHERE se.parent.id = :parentId
                            AND se.status = :status
                        """)
        List<SchoolEnrollment> findSchoolsEnrollmentByParentIdAndStatus(@Param("parentId") Long parentId,
                        @Param("status") EnrollStatus status);

        @Query("SELECT e FROM SchoolEnrollment e WHERE e.parent.id = :parentId")
        List<SchoolEnrollment> findByParentId(@Param("parentId") Long parentId);

        @Query("""
                SELECT COUNT(se) > 0 FROM SchoolEnrollment se 
                WHERE se.parent.id = :parentId 
                AND se.school.id = :schoolId 
                AND se.status = :status 
                """)
        boolean IsUserAllowedToRate(
                @Param("parentId") Long parentId, 
                @Param("schoolId") Long schoolId,
                @Param("status") EnrollStatus status
                );
        }
