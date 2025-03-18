package com.pjb.kindergarten_suggestion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pjb.kindergarten_suggestion.entities.District;
import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findByProvinceId(int provinceId);
}
