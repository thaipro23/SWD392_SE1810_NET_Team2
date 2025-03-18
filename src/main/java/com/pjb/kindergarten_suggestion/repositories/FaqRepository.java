package com.pjb.kindergarten_suggestion.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pjb.kindergarten_suggestion.entities.FAQ;

import java.util.List;

public interface FaqRepository extends JpaRepository<FAQ, Long> {
    @Query(value = """
            SELECT * FROM faq f
            WHERE (LOWER(f.question) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(f.answer) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """, nativeQuery = true)
    Page<FAQ> searchFaqByKeywordNative(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT f FROM FAQ f ORDER BY f.id DESC")
    Page<FAQ> findFaq(Pageable pageable);
    @Query("SELECT f FROM FAQ f WHERE f.status = true")
    List<FAQ> findFaqByStatusTrue();
}
