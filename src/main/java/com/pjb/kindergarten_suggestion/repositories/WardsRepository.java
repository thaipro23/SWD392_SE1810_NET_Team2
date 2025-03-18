package com.pjb.kindergarten_suggestion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pjb.kindergarten_suggestion.entities.Wards;
import java.util.List;

public interface WardsRepository extends JpaRepository<Wards, Integer> {
    List<Wards> findByDistrictId(int districtId);
}
