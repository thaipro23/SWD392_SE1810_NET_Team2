package com.pjb.kindergarten_suggestion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.common.enums.Role;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phoneNumber")
    boolean existsByPhone(@Param("phoneNumber") String phoneNumber);

    boolean existsByPhoneAndIdNot(String phone, long id);

    boolean existsByEmailAndIdNot(String email, long id);

    boolean existsByUsername(String username);

    @Query(value = """
            SELECT * FROM user u
            WHERE (LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND u.is_deleted = 0 ORDER BY u.created_at DESC
            """, nativeQuery = true)
    Page<User> searchUsersByKeywordNative(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false ORDER BY u.createdAt DESC")
    Page<User> findUserByRoles(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.username LIKE ?1%")
    Long getUserCountByUsernamePrefix(String usernamePrefix);

    @Query(value = """
            SELECT * FROM user u
            WHERE (LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND u.is_deleted = 0
            AND u.role = 'PARENT'
            """, nativeQuery = true)
    Page<User> searchParentUsersByKeywordNative(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.role = 'PARENT'")
    Page<User> findParentUsers(Pageable pageable);

    @Query("""
                   SELECT COALESCE(MAX(CAST(SUBSTRING(u.username, LENGTH(:usernamePrefix) + 1) AS int)), 0)
                   FROM User u
                   WHERE u.username LIKE CONCAT(:usernamePrefix, '%')
            """)
    Long getLastUsernameByUsernamePrefix(String usernamePrefix);

    List<User> findByRole(Role role);

    @Query("""
                SELECT DISTINCT u FROM User u
                JOIN SchoolEnrollment se ON se.parent = u AND se.school.id IN :schoolIds
                WHERE (se IS NOT NULL)
            """)
    Page<User> findParentsBySchoolIds(@Param("schoolIds") List<Long> schoolIds, Pageable pageable);

    @Query("""
                SELECT DISTINCT u FROM User u
                JOIN SchoolEnrollment se ON se.parent = u
                    AND se.school.id IN :schoolIds
                WHERE (se IS NOT NULL)
                    AND (LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<User> searchParentInMySchoolByKeyword(
            @Param("schoolIds") List<Long> schoolIds,
            @Param("keyword") String keyword,
            Pageable pageable);
}
