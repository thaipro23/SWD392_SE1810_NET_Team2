package com.pjb.kindergarten_suggestion.repositories;

import com.pjb.kindergarten_suggestion.entities.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findByUser_Id(Long userId);

}
