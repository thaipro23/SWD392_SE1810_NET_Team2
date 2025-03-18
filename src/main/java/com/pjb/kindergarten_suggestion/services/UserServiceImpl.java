package com.pjb.kindergarten_suggestion.services;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pjb.kindergarten_suggestion.common.exception.ResourceNotFoundException;
import com.pjb.kindergarten_suggestion.dto.ParentProfileDTO;
import com.pjb.kindergarten_suggestion.entities.Address;
import com.pjb.kindergarten_suggestion.entities.District;
import com.pjb.kindergarten_suggestion.entities.Province;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.Token;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.entities.Wards;
import com.pjb.kindergarten_suggestion.repositories.AddressRepository;
import com.pjb.kindergarten_suggestion.repositories.TokenRepository;
import com.pjb.kindergarten_suggestion.repositories.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final TokenRepository resetTokenRepository;

    private final JavaMailSender mailSender;

    private final AddressRepository addressRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthService authService;

    private final SchoolService schoolService;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void sendMailCreateUser(String email, String username, String password) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Tài Khoản Kindergarten Suggestion Của Bạn Đã Được Tạo");

            String htmlMsg = "<html>" +
                    "<body style=\"font-family: Arial, sans-serif;\">" +
                    "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                    "<h2 style=\"color: #007bff;\">Thông Tin Tài Khoản Mới Tại Kindergarten Suggestion</h2>" +
                    "<p>Xin chào,</p>" + email +
                    "<p>Tài khoản của bạn đã được tạo thành công. Dưới đây là thông tin đăng nhập của bạn:</p>" +
                    "<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;\">" +
                    "<p><strong>Email:</strong> " + email + "</p>" +
                    "<p><strong>Tên đăng nhập:</strong> " + username + "</p>" +
                    "<p><strong>Mật khẩu:</strong> " + password + "</p>" +
                    "</div>" +
                    "<p style=\"margin-top: 20px; color: #6c757d;\">Nếu bạn không yêu cầu tạo tài khoản này, " +
                    "vui lòng liên hệ với quản trị viên ngay lập tức.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendResetPasswordEmail(String email, HttpServletRequest request) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("The email address doesn’t exist. Please try again.");
        }

        String token = UUID.randomUUID().toString();
        Token resetToken = new Token(token, user);
        resetTokenRepository.save(resetToken);

        String baseUrl = "http://localhost:8080";
        String resetUrl = baseUrl + "/auth/reset-password?token=" + token;
        LocalDateTime expiryDate = resetToken.getExpiryDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedExpiryDate = expiryDate.format(formatter);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);

            helper.setSubject("Yêu Cầu Đặt lại mật khẩu Kindergarten Suggestion");
            String htmlMsg = "<html>" +
                    "<body style=\"font-family: Arial, sans-serif;\">" +
                    "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 10px;\">"
                    +
                    "<h2 style=\"color: #007bff;\">Đặt Lại Mật Khẩu Kindergarten Suggestion</h2>" +
                    "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu. Nhấn vào nút bên dưới để xác nhận đổi mật khẩu:</p>"
                    +
                    "<div style=\"text-align: center; margin: 20px 0;\">" +
                    "<a href=\"" + resetUrl
                    + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #007bff; text-decoration: none; border-radius: 4px;\">Xác Nhận Đổi Mật Khẩu</a>"
                    +
                    "</div>" +
                    "<p style=\"color: #6c757d;\">Lưu ý: Mã xác thực sẽ hết hạn vào lúc <strong>" + formattedExpiryDate
                    + "</strong>.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendActivationEmail(String email, HttpServletRequest request) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("The email address doesn’t exist. Please try again.");
        }

        String token = UUID.randomUUID().toString();
        Token activationToken = new Token(token, user);
        resetTokenRepository.save(activationToken);

        String baseUrl = "http://localhost:8080";
        String activationUrl = baseUrl + "/auth/activate-account?token=" + token;
        LocalDateTime expiryDate = activationToken.getExpiryDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedExpiryDate = expiryDate.format(formatter);
        try {
            // Tạo email MIME message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Link Kích hoạt tài khoản Kindergarten Suggestion");

            String htmlMsg = "<html>" +
                    "<body style=\"font-family: Arial, sans-serif;\">" +
                    "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 10px;\">"
                    +
                    "<h2 style=\"color: #28a745;\">Kích Hoạt Tài Khoản Kindergarten Suggestion</h2>" +
                    "<p>Cảm ơn bạn đã đăng ký tài khoản tại Kindergarten Suggestion." +
                    "</br> Đây là username: " + user.getUsername() +
                    "</br> Vui lòng nhấn vào nút bên dưới để kích hoạt tài khoản của bạn:</p>"
                    +
                    "<div style=\"text-align: center; margin: 20px 0;\">" +
                    "<a href=\"" + activationUrl
                    + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #28a745; text-decoration: none; border-radius: 4px;\">Kích Hoạt Tài Khoản</a>"
                    +
                    "</div>" +
                    "<p style=\"color: #6c757d;\">Lưu ý: Mã xác thực sẽ hết hạn vào lúc <strong>" + formattedExpiryDate
                    + "</strong>.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?!.*\\s).{7,}$");
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Override
    public User updateParentProfile(String username, ParentProfileDTO profileDTO) {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        handleAddressUpdate(user, profileDTO);
        updateUserInfo(user, profileDTO);
        user.setUpdatedAt(LocalDateTime.now());
        return updateUser(user);
    }

    private void handleAddressUpdate(User user, ParentProfileDTO profileDTO) {
        Address currentAddress = user.getAddress();
        Address newAddress = profileDTO.getAddress();

        if (currentAddress == null) {
            // addressCurrent is null
            if (newAddress.getProvince().getName() == null) {
                newAddress.setProvince(null);
            }
            if (newAddress.getDistrict().getName() == null) {
                newAddress.setDistrict(null);
            }
            if (newAddress.getWards().getName() == null) {
                newAddress.setWards(null);
            }
            Address savedAddress = addressRepository.save(newAddress);
            user.setAddress(savedAddress);
            return;
        }

        if (isAddressChanged(currentAddress, newAddress)) {
            // new address
            currentAddress.setDetail(newAddress.getDetail());
            if (newAddress.getDistrict().getName() == null) {
                currentAddress.setDistrict(null);
            } else
                currentAddress.setDistrict(newAddress.getDistrict());
            if (newAddress.getProvince().getName() == null) {
                currentAddress.setProvince(null);
            } else
                currentAddress.setProvince(newAddress.getProvince());
            if (newAddress.getWards().getName() == null) {
                currentAddress.setWards(null);
            } else
                currentAddress.setWards(newAddress.getWards());
            user.setAddress(currentAddress);
        } else {
            // no change address
            if (currentAddress.getDistrict().getName() == null) {
                currentAddress.setDistrict(null);
            }
            if (currentAddress.getProvince().getName() == null) {
                currentAddress.setProvince(null);
            }
            if (currentAddress.getWards().getName() == null) {
                currentAddress.setWards(null);
            }
            user.setAddress(currentAddress);
        }
    }

    private boolean isAddressChanged(Address current, Address newAddress) {
        if (current == null)
            return true;
        if (current.getProvince() == null) {
            current.setProvince(new Province(0, null));
            current.setWards(new Wards(0, null, null));
        }
        if (current.getDistrict() == null) {
            current.setDistrict(new District(0, null, null));
        }
        if (current.getWards() == null) {
            current.setWards(new Wards(0, null, null));
        }
        return current.getProvince().getId() != newAddress.getProvince().getId() ||
                current.getDistrict().getId() != newAddress.getDistrict().getId() ||
                current.getWards().getId() != newAddress.getWards().getId() ||
                !Objects.equals(current.getDetail(), newAddress.getDetail());
    }

    private void updateUserInfo(User user, ParentProfileDTO profileDTO) {
        user.setFullname(profileDTO.getFullName().trim().replaceAll("\\s+", " "));
        if (profileDTO.getBirthDate() == null) {
            user.setBirthDate(null);
        } else {
            user.setBirthDate(profileDTO.getBirthDate());
        }
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            if (!isExistPhone(profileDTO.getPhone()))
                user.setPhone(profileDTO.getPhone());
            else {
                throw new IllegalArgumentException("Phone number is exist!");
            }
        } else {
            if (user.getPhone().equals(profileDTO.getPhone())) {
            } else {
                user.setPhone(profileDTO.getPhone());
            }
        }
    }

    @Override
    public ParentProfileDTO UserToParentProfileDTO(User user) {
        ParentProfileDTO profileDTO = new ParentProfileDTO();
        profileDTO.setFullName(user.getFullname());
        profileDTO.setUsername(user.getUsername());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhone(user.getPhone());
        profileDTO.setBirthDate(user.getBirthDate());
        profileDTO.setRole(user.getRole());
        profileDTO.setActive(user.isActive());
        profileDTO.setAddress(user.getAddress());
        return profileDTO;
    }

    @Override
    public void updatePasswordReset(String token, String password) {
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException(
                    "Password must contain at least one number, one numeral, and seven characters.");
        }

        Token resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            throw new IllegalArgumentException("This link has expired. Please go back to Homepage and try again.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        resetTokenRepository.delete(resetToken);
    }

    @Override
    public boolean isTokenValid(String token) {
        Token resetToken = resetTokenRepository.findByToken(token);
        return resetToken != null && !resetToken.isExpired();
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public Page<User> getUserByKeyWithPagination(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsersByKeywordNative(
                Normalizer.normalize(keyword, Normalizer.Form.NFD).replaceAll("\\p{M}", ""), pageable);
    }

    @Override
    public Page<User> getParentByKeyWithPagination(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchParentUsersByKeywordNative(keyword, pageable);
    }

    @Override
    public Page<User> getUserWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findUserByRoles(pageable);
    }

    @Override
    public Page<User> getParentWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findParentUsers(pageable);
    }

    @Override
    public Page<User> getParentsInMySchool(int page, int size) {
        User currentUser = authService.getCurrentUser().get();
        Long creatorId = currentUser.getId();
        List<Long> schoolIds = schoolService.getSchoolByCreatorId(creatorId)
                .stream()
                .map(School::getId)
                .collect(Collectors.toList());
        if (schoolIds.isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findParentsBySchoolIds(schoolIds, pageable);
    }

    @Override
    public Page<User> getParentByKeyInMySchool(String keyword, int page, int size) {
        User currentUser = authService.getCurrentUser().get();
        Long creatorId = currentUser.getId();
        List<Long> schoolIds = schoolService.getSchoolByCreatorId(creatorId)
                .stream()
                .map(School::getId)
                .collect(Collectors.toList());
        if (schoolIds.isEmpty()) {
            return Page.empty();
        }
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchParentInMySchoolByKeyword(schoolIds, keyword, pageable);
    }

    public Long getUserCountByUsernamePrefix(String username) {
        return userRepository.getLastUsernameByUsernamePrefix(username);
    }

    @Override
    public User updateUser(User user) {
        authService.checkAndUpdateCurrentUser(user);
        return userRepository.save(user);
    }

    @Override
    public boolean isCurrentPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        String hashPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashPassword);
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void activateAccount(String tokenValue) {
        Optional<Token> tokenOptional = Optional.ofNullable(resetTokenRepository.findByToken(tokenValue));

        if (!tokenOptional.isPresent() || tokenOptional.get().isExpired()) {
            throw new IllegalArgumentException("This link has expired or is invalid. Please try again.");
        }

        Token resetToken = tokenOptional.get();
        User user = resetToken.getUser();

        user.setActive(true);
        userRepository.save(user);

        resetTokenRepository.delete(resetToken);
    }

    @Override
    public boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isExistPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByPhoneAndIdNot(String phone, long id) {
        return userRepository.existsByPhoneAndIdNot(phone, id);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, long id) {
        return userRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User does not existed!"));
        user.setDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
