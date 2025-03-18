package com.pjb.kindergarten_suggestion.controller;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.dto.UserDTO;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
public class UserDetailController {
    private final UserService userService;
    
    @GetMapping(AppRoutes.ADMIN_USER_DETAIL)
    public String getUserDetail(Model model, @PathVariable long id) {
        model.addAttribute("pageTitle", "User Detail");
        User user=this.userService.findUserById(id);
        model.addAttribute("user", user);
        return "pages/admin/users/user-detail";
    }

    @GetMapping(AppRoutes.ADMIN_USER_EDIT)
    public String getEditUserDetail(Model model, @PathVariable long id) {
        model.addAttribute("pageTitle", "Edit User Detail");
        User user = this.userService.findUserById(id);
        model.addAttribute("user", user);
        return "pages/admin/users/edit-user-detail";
    }

    @PostMapping(AppRoutes.ADMIN_USER_EDIT)
    public String updateUserDetail(@Valid @ModelAttribute("user") UserDTO userDTO, 
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (userService.existsByEmailAndIdNot(userDTO.getEmail(), userDTO.getId())) {
            bindingResult.rejectValue("email", "error.email", "Email already exist");
        }
        if(userService.existsByPhoneAndIdNot(userDTO.getPhone(),userDTO.getId())){
            bindingResult.rejectValue("phone", "error.phone", "Phone number already exist");
        }                
        if (bindingResult.hasErrors()) {
            return "pages/admin/users/edit-user-detail";
        }
    
        try {
            User userUpdate = this.userService.findUserById(userDTO.getId());
            if (userUpdate != null) {               
                String nameDTO= removeDiacritics(userDTO.getFullname());
                String nameupdate=removeDiacritics(userUpdate.getFullname());
                String newUserName = generateUsername(nameDTO);
                if(generateUsernamePrefix(nameDTO).equals(generateUsernamePrefix(nameupdate))){
                    newUserName=userDTO.getUsername();
                }
                
                userUpdate.setUsername(newUserName);
                userUpdate.setFullname(userDTO.getFullname());
                userUpdate.setEmail(userDTO.getEmail());
                userUpdate.setBirthDate(userDTO.getBirthDate());
                userUpdate.setPhone(userDTO.getPhone());
                userUpdate.setRole(userDTO.getRole());
                userUpdate.setUpdatedAt(LocalDateTime.now());
                
                this.userService.updateUser(userUpdate);
                redirectAttributes.addFlashAttribute("message", "Change has been successfully updated");
            } else {
                redirectAttributes.addFlashAttribute("message", "User not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred while updating the user.");
        }
        return "redirect:"+AppRoutes.ADMIN_USER_EDIT;
    }

    @PostMapping("/admin/users/activate/{id}")
    public ResponseEntity<String> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok("User activated successfully");
    }

    @PostMapping("/admin/users/deactivate/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }

    public static String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
    private String generateUsername(String fullName) {
        String[] baseName = fullName.trim().toLowerCase().split("\\s+");;

        String firstName = baseName[baseName.length - 1].substring(0, 1).toUpperCase() +
                baseName[baseName.length - 1].substring(1);
        StringBuilder initials = new StringBuilder(firstName);

        for (int i = 0; i < baseName.length - 1; i++) {
            initials.append(baseName[i].substring(0, 1).toUpperCase());
        }

        String usernamePrefix = initials.toString();
        Long userCount = userService.getUserCountByUsernamePrefix(usernamePrefix);

        Long incrementalNumber = userCount + 1;
        return usernamePrefix + incrementalNumber;
    }

    private static String generateUsernamePrefix(String fullname){
        String[] baseName = fullname.trim().toLowerCase().split("\\s+");;

        String firstName = baseName[baseName.length - 1].substring(0, 1).toUpperCase() +
                baseName[baseName.length - 1].substring(1);
        StringBuilder initials = new StringBuilder(firstName);

        for (int i = 0; i < baseName.length - 1; i++) {
            initials.append(baseName[i].substring(0, 1).toUpperCase());
        }

        String usernamePrefix = initials.toString();
        return usernamePrefix;
    }
    
}
