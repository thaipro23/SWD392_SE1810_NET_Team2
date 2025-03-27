package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.common.enums.Role;
import com.pjb.kindergarten_suggestion.entities.BillChild;
import com.pjb.kindergarten_suggestion.entities.Child;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.BillChildService;
import com.pjb.kindergarten_suggestion.services.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/bills")
@RequiredArgsConstructor
public class BillChildAdminController {
    private final BillChildService billChildService;
    private final ChildService childService;

    @GetMapping
    public String listBills(Model model) {
        List<BillChild> bills = billChildService.findAll(); // Tất cả hóa đơn
        model.addAttribute("bills", bills);
        return "pages/admin/bill-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<Child> children = childService.findAll();
        BillChild billChild = new BillChild();
        billChild.setChild(new Child()); // Khởi tạo child để tránh null
        model.addAttribute("billChild", billChild);
        model.addAttribute("child", children);
        return "pages/admin/bill-create";
    }


    @PostMapping("/create")
    public String createBill(@ModelAttribute BillChild billChild, RedirectAttributes redirectAttributes) {
        User loggedInUser = getLoggedInUser();
        billChild.setUser(loggedInUser);

        BillChild savedBill = billChildService.createBill(billChild);
        redirectAttributes.addFlashAttribute("success", "Bill created successfully!");
        return "redirect:/admin/bills";
    }

    @GetMapping("/{id}")
    public String viewBill(@PathVariable Long id, Model model) {
        BillChild bill = billChildService.findById(id);
        model.addAttribute("bill", bill);
        return "pages/admin/bill-detail";
    }

    @GetMapping("/child/{childId}")
    @ResponseBody
    public User getParentByChildId(@PathVariable Long childId) {
        Child child = childService.findById(childId);
        return child.getParent();
    }

    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (User) principal;
        }
        throw new RuntimeException("User not found in security context.");
    }
}
