package com.pjb.kindergarten_suggestion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pjb.kindergarten_suggestion.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("""
        SELECT new com.pjb.kindergarten_suggestion.entities.Address(w.district.province ,w.district, w)
        FROM Wards w
        WHERE w.id = ?1
    """)
    Address getAddressByWardsId(int id);
}
