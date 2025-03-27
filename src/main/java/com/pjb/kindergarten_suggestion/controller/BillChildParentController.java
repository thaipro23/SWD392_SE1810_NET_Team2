package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.common.enums.BillStatus;
import com.pjb.kindergarten_suggestion.entities.BillChild;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.BillChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/parent/bills")
@RequiredArgsConstructor
public class BillChildParentController {
    private final BillChildService billChildService;

    @GetMapping
    public String listBills(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        User loggedInUser = getLoggedInUser();
        List<BillChild> bills;

        if (date != null) {
            bills = billChildService.findByParentIdAndDate(loggedInUser.getId(), date);
        } else {
            bills = billChildService.findByParentId(loggedInUser.getId());
        }

        model.addAttribute("bills", bills);
        model.addAttribute("selectedDate", date != null ? date : LocalDate.now());
        return "pages/parent/bill-list";
    }

    @GetMapping("/{id}")
    public String viewBill(@PathVariable Long id, Model model) {
        User loggedInUser = getLoggedInUser();
        BillChild bill = billChildService.findById(id);

        // Kiểm tra xem hóa đơn có thuộc về parent này không
        if (!bill.getChild().getParent().getId().equals(loggedInUser.getId())) {
            return "redirect:/parent/bills?error=unauthorized";
        }

        model.addAttribute("bill", bill);
        return "pages/parent/bill-detail";
    }

    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (User) principal;
        }
        throw new RuntimeException("User not found in security context.");
    }
}
