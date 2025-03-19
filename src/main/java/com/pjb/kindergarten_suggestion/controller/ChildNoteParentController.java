package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.entities.ChildNote;
import com.pjb.kindergarten_suggestion.services.ChildNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/parent/child-notes")
@RequiredArgsConstructor
public class ChildNoteParentController {
    private final ChildNoteService childNoteService;

    @GetMapping
    public String getChildNoteByDate(@RequestParam(required = false) String date, Model model) {
        LocalDate selectedDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        Optional<ChildNote> childNoteOpt = childNoteService.findByDate(selectedDate);

        if (childNoteOpt.isEmpty()) {
            model.addAttribute("error", "No child note found for this date.");
        } else {
            model.addAttribute("childNote", childNoteOpt.get());
        }
        model.addAttribute("selectedDate", selectedDate);
        return "pages/parent/child-note";
    }

}
