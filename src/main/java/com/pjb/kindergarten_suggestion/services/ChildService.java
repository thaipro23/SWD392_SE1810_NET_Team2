package com.pjb.kindergarten_suggestion.services;

import com.pjb.kindergarten_suggestion.entities.Child;

import java.util.List;

public interface ChildService {
    List<Child> findByUser(Long userId);
}
