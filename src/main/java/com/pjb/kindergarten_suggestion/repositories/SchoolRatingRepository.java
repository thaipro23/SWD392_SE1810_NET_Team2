package com.pjb.kindergarten_suggestion.repositories;

import com.pjb.kindergarten_suggestion.dto.SchoolAvgRating;
import com.pjb.kindergarten_suggestion.dto.SchoolRatingDTO;
import com.pjb.kindergarten_suggestion.entities.SchoolRating;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRatingRepository extends JpaRepository<SchoolRating, Long> {
    List<SchoolRating> findBySchoolIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long schoolId, LocalDateTime fromDate,
            LocalDateTime toDate);

    long countBySchoolId(Long schoolId);

    List<SchoolRating> findBySchoolId(Long schoolId);

    @Query("""
            SELECT new com.pjb.kindergarten_suggestion.dto.SchoolRatingDTO(
                parent.fullname, sr.createdAt, sr.feedback, sr.rating1, sr.rating2, sr.rating3, sr.rating4, sr.rating5)
            FROM SchoolRating sr
            WHERE sr.school.id = ?1
            ORDER BY sr.createdAt DESC""")
    Page<SchoolRatingDTO> findBySchoolIdOrderByCreatedAtDesc(Long schoolId, Pageable pageable);

    @Query("""
            SELECT new com.pjb.kindergarten_suggestion.dto.SchoolRatingDTO(
                parent.fullname, sr.createdAt, sr.feedback, sr.rating1, sr.rating2, sr.rating3, sr.rating4, sr.rating5)
            FROM SchoolRating sr
            WHERE sr.school.id =:schoolId
            AND ROUND((sr.rating1 + sr.rating2 + sr.rating3 + sr.rating4 + sr.rating5) / 5.0) =:stars
            ORDER BY sr.createdAt DESC""")
    Page<SchoolRatingDTO> findBySchoolIdOrderByCreatedAtDesc(Long schoolId, Pageable pageable, Integer stars);

    SchoolRating findFirstByParentId(Long schoolId);

    Optional<SchoolRating> findByParentIdAndSchoolId(Long parentId, Long schoolId);

    @Query("""
            SELECT new com.pjb.kindergarten_suggestion.dto.SchoolAvgRating(
               CAST(COALESCE(AVG(r.rating1), 0) AS BYTE),
                CAST(COALESCE(AVG(r.rating2), 0) AS BYTE),
                CAST(COALESCE(AVG(r.rating3), 0) AS BYTE),
                CAST(COALESCE(AVG(r.rating4), 0) AS BYTE),
                CAST(COALESCE(AVG(r.rating5), 0) AS BYTE)
            )
            FROM SchoolRating r WHERE r.school.id = ?1""")
    SchoolAvgRating getAvgRatingBySchoolId(Long schoolId);

    @Query("""
            SELECT sr
            FROM SchoolRating sr
            WHERE sr.feedback IS NOT NULL and sr.feedback not like ''
            ORDER BY (sr.rating1 + sr.rating2 + sr.rating3 + sr.rating4 + sr.rating5) / 5.0 DESC,
                     sr.createdAt DESC
            """)
    List<SchoolRating> findTopRatings(Pageable pageable);

}
