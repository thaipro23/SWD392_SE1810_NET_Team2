package com.pjb.kindergarten_suggestion.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pjb.kindergarten_suggestion.common.enums.RequestStatus;
import com.pjb.kindergarten_suggestion.dto.CounsellingRequestWithRatingDTO;
import com.pjb.kindergarten_suggestion.entities.CounsellingRequest;
import com.pjb.kindergarten_suggestion.entities.User;


public interface CounsellingRequestRepository extends JpaRepository<CounsellingRequest, Long> {
    Page<CounsellingRequest> findAll(Pageable page);

    @Query("""
        SELECT u FROM CounsellingRequest u WHERE 
        u.fullname LIKE %:keyword% OR 
        u.email LIKE %:keyword% OR 
        u.phone LIKE %:keyword% order by u.createdAt desc""")
    Page<CounsellingRequest> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<CounsellingRequest> findBySchoolCreatorId(Long userId, Pageable pageable);

    @Query("""
            SELECT cr FROM CounsellingRequest cr JOIN cr.school s 
            WHERE s.creator.id = :userId AND cr.status ='OPEN'order by cr.createdAt desc""")
    Page<CounsellingRequest> findRequestReminderBySchoolCreator(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT cr FROM CounsellingRequest cr JOIN cr.school s 
        WHERE s.creator.id = :userId AND (cr.fullname LIKE %:keyword% 
        OR cr.email LIKE %:keyword% 
        OR cr.phone LIKE %:keyword%) order by cr.createdAt desc""")
    Page<CounsellingRequest> findByKeywordInSchool(@Param("keyword") String keyword, Pageable pageable, @Param("userId") Long userId);

    CounsellingRequest findById(long id);

    CounsellingRequest save (CounsellingRequest counsellingRequest);

    Page<CounsellingRequest> findAllByStatus(Pageable page, RequestStatus status);

    @Query("""
        SELECT cr FROM CounsellingRequest cr JOIN cr.school s 
        WHERE s.creator.id = :userId AND cr.status ='OPEN' AND (cr.fullname LIKE %:keyword% 
        OR cr.email LIKE %:keyword% 
        OR cr.phone LIKE %:keyword%)
        order by cr.createdAt desc
        """)
    Page<CounsellingRequest> findRequestReminderByKeywordInSchool(@Param("keyword") String keyword, Pageable pageable, @Param("userId") Long userId);

    @Query("""
        SELECT u FROM CounsellingRequest u 
        WHERE u.status ='OPEN' AND
        ( u.fullname LIKE %:keyword% OR 
        u.email LIKE %:keyword% OR 
        u.phone LIKE %:keyword% )
        order by u.createdAt desc
        """)
    Page<CounsellingRequest> findRequestReminderByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<CounsellingRequest> findByParent(User parent, Pageable pageable);

    int countByParentAndStatus(User parent,RequestStatus status);

    @Query("""
            select new com.pjb.kindergarten_suggestion.dto.CounsellingRequestWithRatingDTO(
            cr,
            coalesce(avg((sr.rating1 + sr.rating2 + sr.rating3 + sr.rating4 + sr.rating5)/ 5),0.0),
            coalesce(count(sr),0)
            )
            from CounsellingRequest cr
            left join SchoolRating sr on cr.school = sr.school
            where cr.parent.id = :parentId
            group by cr
            order by cr.createdAt desc
            """)
    Page<CounsellingRequestWithRatingDTO> getCounsellingRequestWithRatingByParentId(@Param("parentId") long patentId, Pageable pageable);
}
