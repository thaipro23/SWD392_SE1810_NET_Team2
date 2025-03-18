package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.entities.FAQ;
import com.pjb.kindergarten_suggestion.services.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
public class MessFAQController {
    @Autowired
    private FaqService faqService;

    @GetMapping
    public ResponseEntity<List<FAQ>> getAllFAQs() {
        List<FAQ> faqs = faqService.getAllIsTrue();
        return ResponseEntity.ok(faqs);
    }
}
