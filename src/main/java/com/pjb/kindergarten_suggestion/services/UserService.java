package com.pjb.kindergarten_suggestion.services;

import org.springframework.data.domain.Page;

import com.pjb.kindergarten_suggestion.dto.ParentProfileDTO;
import com.pjb.kindergarten_suggestion.entities.User;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    User findByEmail(String email);

    User createUser(User user);

    void sendResetPasswordEmail(String email, HttpServletRequest request);

    boolean isTokenValid(String token);

    void updatePasswordReset(String token, String password);

    public void sendActivationEmail(String email, HttpServletRequest request);

    boolean isValidPassword(String password);

    Page<User> getUserByKeyWithPagination(String keyword, int page, int size);

    User findUserById(long id);

    User updateUser(User user);

    User getUserByUsername(String username);

    User updateParentProfile(String username, ParentProfileDTO profileDTO);

    ParentProfileDTO UserToParentProfileDTO(User user);

    Long getUserCountByUsernamePrefix(String username);

    void sendMailCreateUser(String email, String yourNewAccount, String s);

    Page<User> getUserWithPagination(int page, int size);

    boolean isCurrentPassword(User user, String currentPassword);

    void updatePassword(User user, String newPassword);

    void activateUser(Long id);

    void deactivateUser(Long id);

    void activateAccount(String tokenValue);

    boolean isExistEmail(String email);

    boolean isExistPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, long id);

    boolean existsByEmailAndIdNot(String email, long id);

    void deleteUserById(Long id);

    Page<User> getParentByKeyWithPagination(String keyword, int page, int size);

    Page<User> getParentWithPagination(int page, int size);

    Page<User> getParentsInMySchool(int page, int size);

    Page<User> getParentByKeyInMySchool(String keyword, int page, int size);
}
