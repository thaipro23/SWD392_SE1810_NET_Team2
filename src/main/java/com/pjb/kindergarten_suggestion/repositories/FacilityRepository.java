package com.pjb.kindergarten_suggestion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pjb.kindergarten_suggestion.entities.Facility;


public interface FacilityRepository extends JpaRepository<Facility, Long> {

}
