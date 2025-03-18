package com.pjb.kindergarten_suggestion.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pjb.kindergarten_suggestion.common.enums.SchoolStatus;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.User;

public interface SchoolRepository extends JpaRepository<School, Long> {
        List<School> findByStatus(SchoolStatus status);

        Optional<School> findById(long id);

    @Query(value = """
                        SELECT s.*, 
                CASE
                        WHEN :isAdmin = true AND s.creator = :userId AND s.status IN (:createdStatuses) THEN 0
                        ELSE 1
                END AS display_rank
                FROM School s
                WHERE 
                (:isAdmin = false AND s.creator = :userId AND s.status IN (:normalStatuses))
                OR
                (:isAdmin = true AND (
                        (s.creator = :userId AND s.status IN (:createdStatuses))
                        OR
                        (s.creator != :userId AND s.status IN (:normalStatuses))
                ))
                AND (
                LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
                ORDER BY 
                CASE
                        WHEN :isAdmin = true AND s.creator = :userId AND s.status IN (:createdStatuses) THEN 0
                        ELSE 1
                END,
                s.updated_at DESC
            """, nativeQuery = true)
    Page<School> findByCreatorStatusAndKeyword(
        @Param("isAdmin") boolean isAdmin,
        @Param("userId") Long userId,
        @Param("createdStatuses") List<String> createdStatuses,
        @Param("normalStatuses") List<String> normalStatuses,
        @Param("keyword") String keyword,
        Pageable pageable);
    
    @Query(value = "SELECT MAX(s.id) FROM School s")
    long findMaximumSchoolId();

        @Modifying
        @Transactional
        @Query("UPDATE School s SET s.status = 'DELETED' WHERE s.id = :id")
        void softDelete(@Param("id") Long id);

        @Query("SELECT DISTINCT s FROM School s " +
                        "LEFT JOIN s.address a " +
                        "LEFT JOIN s.facilities f " +
                        "LEFT JOIN s.utilities u " +
                        "WHERE " +
                        "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
                        "(:provinceId IS NULL OR a.province.id = :provinceId) AND " +
                        "(:districtId IS NULL OR a.district.id = :districtId) AND " +
                        "(:schoolTypeId IS NULL OR s.schoolType.id = :schoolTypeId) AND " +
                        "(:childAgeId IS NULL OR s.childAge.id = :childAgeId) AND " +
                        "((:feeFrom IS NULL OR s.feeFrom >= :feeFrom) AND (:feeTo IS NULL OR s.feeTo <= :feeTo)) AND " +
                        "(:facilityIds IS NULL OR f.id IN (:facilityIds)) AND " +
                        "(:utilityIds IS NULL OR u.id IN (:utilityIds)) AND " +
                        "s.status = 'PUBLISHED'")
        Page<School> searchSchools(
                        @Param("name") String name,
                        @Param("provinceId") Integer provinceId,
                        @Param("districtId") Integer districtId,
                        @Param("schoolTypeId") Long schoolTypeId,
                        @Param("childAgeId") Long childAgeId,
                        @Param("feeFrom") Long feeFrom,
                        @Param("feeTo") Long feeTo,
                        @Param("facilityIds") Set<Long> facilityIds,
                        @Param("utilityIds") Set<Long> utilityIds,
                        Pageable pageable);
        // @Query("SELECT DISTINCT s FROM School s " +
        // "LEFT JOIN s.address a " +
        // "WHERE " +
        // "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
        // "(:provinceId IS NULL OR a.province.id = :provinceId) AND " +
        // "(:districtId IS NULL OR a.district.id = :districtId) AND " +
        // "s.status = 'PUBLISHED'")
        // Page<School> searchSchools(
        // @Param("name") String name,
        // @Param("provinceId") Integer provinceId,
        // @Param("districtId") Integer districtId,
        // Pageable pageable
        // );

        @Query("""
                        SELECT COALESCE(MAX(CAST(SUBSTRING(s.schoolCode, LENGTH(:schoolCodePrefix) + 1) AS int)), 0)
                        FROM School s
                        WHERE s.schoolCode LIKE CONCAT(:schoolCodePrefix, '%')
                        """)
        Long getLastSchoolCodeByPrefix(String schoolCodePrefix);

        @Query("""
                        SELECT s FROM School s
                        JOIN SchoolEnrollment se ON se.school = s
                        WHERE se.parent.id = :parentId
                        AND s.status = SchoolStatus.PUBLISHED
                        AND (se.status= EnrollStatus.PENDING)
                        """)
        List<School> findSchoolsRequiredByParentId(@Param("parentId") Long parentId);

        List<School> findByCreator(User creator);

        @Query("""
                        SELECT s FROM School s JOIN SchoolEnrollment se
                        ON se.school = s
                        WHERE se.parent.id = :parentId
                        AND s.status = SchoolStatus.PUBLISHED
                        AND s.id IN :schoolIds
                        AND (se.status= EnrollStatus.PENDING)
                        """)

        List<School> findMySchoolsRequiredByParentId(@Param("schoolIds") List<Long> schoolIds,
                        @Param("parentId") Long parentId);
}
