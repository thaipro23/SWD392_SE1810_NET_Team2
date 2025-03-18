package com.pjb.kindergarten_suggestion.services;

import java.util.Optional;

import com.pjb.kindergarten_suggestion.dto.RegisterDTO;
import com.pjb.kindergarten_suggestion.entities.User;

public interface AuthService {
    Optional<User> getCurrentUser();
    void checkAndUpdateCurrentUser(User user);
    User RegisterDTOtoUser(RegisterDTO registerDTO);
    void handleSaveUsers(User user);
}
