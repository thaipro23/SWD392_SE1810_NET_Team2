package com.pjb.kindergarten_suggestion.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class UserListController {
    private final UserService userService;

    @GetMapping(AppRoutes.ADMIN_USER_MANAGEMENT)
    public String userList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model) {
        model.addAttribute("pageTitle", "User Management");
        int actualPage = page - 1;
        if (actualPage < 0) {
            actualPage = 0;
            page = 1;
        }
        Page<User> usersPage = (keyword != null && !keyword.isEmpty())
                ? userService.getUserByKeyWithPagination(keyword, actualPage, size)
                : userService.getUserWithPagination(actualPage, size);

        int totalPages = usersPage.getTotalPages();
        if (totalPages > 0 && actualPage >= totalPages) {
            actualPage = totalPages - 1; 
            page = totalPages;
            usersPage = (keyword != null && !keyword.isEmpty())
                    ? userService.getUserByKeyWithPagination(keyword, actualPage, size)
                    : userService.getUserWithPagination(actualPage, size);
        }
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("totalPages", totalPages);
        }
        if (usersPage == null || usersPage.getTotalElements() == 0) {
            model.addAttribute("noUsersMessage", "No results found.");
            model.addAttribute("totalPages", 1);
        } else {
            model.addAttribute("totalPages", usersPage.getTotalPages());
        }
        return "pages/admin/users/user-management";
    }

    @DeleteMapping("/admin/users/{userId}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
    // @GetMapping("/admin/users")
    // public String getAllUsers(Model model) {
    // List<User> allUsers = userService.getAllUsers();
    // model.addAttribute("allUsers", allUsers);
    // return "pages/admin/users/user-management";
    // }

}
