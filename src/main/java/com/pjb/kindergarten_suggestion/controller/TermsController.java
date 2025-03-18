package com.pjb.kindergarten_suggestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TermsController {
    @GetMapping("/terms")
    public String showTermsAndConditions(Model model) {
        model.addAttribute("pageTitle", "Terms and Conditions");
        return "pages/terms-and-conditions";
    }
}
