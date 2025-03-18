package com.pjb.kindergarten_suggestion.repositories;

import com.pjb.kindergarten_suggestion.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByToken(String token);

}
