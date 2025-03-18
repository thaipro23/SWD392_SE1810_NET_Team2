package com.pjb.kindergarten_suggestion.handlers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (statusCode != null && statusCode == 404) {
            return "error/404";
        }

        Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
        String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);

        return "error/error-page";
    }

    @GetMapping("/403")
    public String accessDeniedPage() {
        return "error/403";
    }
    
}