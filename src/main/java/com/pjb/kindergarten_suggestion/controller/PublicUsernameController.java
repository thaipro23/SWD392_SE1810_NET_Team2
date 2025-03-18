package com.pjb.kindergarten_suggestion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pjb.kindergarten_suggestion.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicUsernameController {
    private final UserService userService;

    @GetMapping("/username/check-prefix")
    public ResponseEntity<Long> countUsernamePrefix(@RequestParam String prefix) {
        Long userCount = userService.getUserCountByUsernamePrefix(prefix);
        return ResponseEntity.ok(userCount);
    }
}