package com.pjb.kindergarten_suggestion.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Arrays;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.common.enums.SchoolStatus;
import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.entities.School;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.AuthService;
import com.pjb.kindergarten_suggestion.services.SchoolService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SchoolListController {
    private final SchoolService schoolService;
    private final AuthService authService;

    @GetMapping(AppRoutes.ADMIN_SCHOOL_MANAGEMENT)
    public String schoolList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model) {
        model.addAttribute("pageTitle", "School Management");
        int actualPage = page - 1;
        // Kiểm tra và điều chỉnh giá trị của page nếu vượt quá giới hạn
        if (actualPage < 0) {
            actualPage = 0;
            page = 1;
        }
        Pageable pageable = PageRequest.of(actualPage, size);
        Optional<User> currentUser = authService.getCurrentUser();
        User user = currentUser.get();
        // Xác định danh sách trạng thái dựa trên vai trò và chuyển đổi thành chuỗi
        List<String> nomalStatuses = user.getRole().equals(Role.ADMIN)
                ? Arrays.asList(SchoolStatus.SUBMITTED, SchoolStatus.APPROVED, SchoolStatus.REJECTED,
                        SchoolStatus.PUBLISHED, SchoolStatus.UNPUBLISHED).stream()
                        .map(SchoolStatus::name) // Lấy tên của enum
                        .collect(Collectors.toList())
                : Arrays.asList(SchoolStatus.SAVED, SchoolStatus.SUBMITTED, SchoolStatus.APPROVED,
                        SchoolStatus.REJECTED, SchoolStatus.PUBLISHED, SchoolStatus.UNPUBLISHED).stream()
                        .map(SchoolStatus::name)
                        .collect(Collectors.toList());
        List<String> createdStatus = Arrays.asList(SchoolStatus.SAVED, SchoolStatus.SUBMITTED, SchoolStatus.APPROVED,
        SchoolStatus.REJECTED, SchoolStatus.PUBLISHED, SchoolStatus.UNPUBLISHED).stream()
        .map(SchoolStatus::name)
        .collect(Collectors.toList());
        // Xử lý tìm kiếm và phân trang
        Page<School> schoolsPage;
        if (user.getRole() == Role.ADMIN) {
            schoolsPage = schoolService.getSchoolWithPaginationAndKey(user.getId(),keyword,createdStatus,nomalStatuses,pageable);
        } else {
            schoolsPage = schoolService.getSchoolWithPaginationAndKey(user.getId(),keyword,createdStatus,nomalStatuses,pageable);
        }
        int totalPages = schoolsPage.getTotalPages();
        // Kiểm tra và điều chỉnh giá trị của page nếu vượt quá giới hạn
        if (totalPages > 0 && actualPage >= totalPages) {
            actualPage = totalPages - 1; // Đặt về trang cuối cùng nếu vượt quá
            page = totalPages;
            if (user.getRole() == Role.ADMIN) {
                schoolsPage = schoolService.getSchoolWithPaginationAndKey(user.getId(),keyword,createdStatus,nomalStatuses,pageable);
            } else {
                schoolsPage = schoolService.getSchoolWithPaginationAndKey(user.getId(),keyword,createdStatus,nomalStatuses,pageable);
            }
        }
        // Thêm các thông tin cần thiết vào model
        model.addAttribute("schoolsPage", schoolsPage);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        // Thông báo khi không có người dùng nào được tìm thấy
        if (schoolsPage == null || schoolsPage.getTotalElements() == 0) {
            model.addAttribute("totalPages", 1);
        } else {
            model.addAttribute("totalPages", schoolsPage.getTotalPages());
        }
        return "pages/school/school-management";
    }

    @GetMapping(AppRoutes.SCHOOL_OWNER_SCHOOL)
    public String schoolListOwner(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model) {
        return schoolList(page, size, keyword, model);
    }

    // @DeleteMapping("/admin/users/{userId}")
    // @Transactional
    // public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    // userService.deleteUserById(userId);
    // return ResponseEntity.noContent().build();
    // }
}
