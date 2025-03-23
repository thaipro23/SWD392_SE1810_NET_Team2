package com.pjb.kindergarten_suggestion.controller;

import com.pjb.kindergarten_suggestion.entities.Child;
import com.pjb.kindergarten_suggestion.entities.ChildNote;
import com.pjb.kindergarten_suggestion.entities.User;
import com.pjb.kindergarten_suggestion.services.ChildNoteService;
import com.pjb.kindergarten_suggestion.services.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/teacher/child-notes")
@RequiredArgsConstructor
public class ChildNoteTeacherController {
    private final ChildNoteService childNoteService;
    private final ChildService childService;

    @GetMapping
    public String getChildNotes(@AuthenticationPrincipal User teacher,
                                @RequestParam(value = "date", required = false) String dateStr,
                                Model model) {
        List<ChildNote> childNotes;
        LocalDate selectedDate = null;
        String errorMessage = null;

        try {
            if (dateStr != null && !dateStr.isEmpty()) {
                selectedDate = LocalDate.parse(dateStr);
                childNotes = childNoteService.findByTeacherAndDate(teacher.getId(), selectedDate);
            } else {
                childNotes = childNoteService.findByTeacher(teacher.getId());
            }
        } catch (Exception e) {
            errorMessage = "Invalid date format. Please select a valid date.";
            childNotes = childNoteService.findByTeacher(teacher.getId());
        }

        model.addAttribute("childNotes", childNotes);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("errorMessage", errorMessage);
        return "pages/school/child-notes";
    }



    @GetMapping("/create")
    public String createChildNoteForm(@AuthenticationPrincipal User teacher, Model model) {
        List<Child> children = childService.findByUserAndNotEvaluatedToday(teacher.getId());
        model.addAttribute("childNote", new ChildNote());
        model.addAttribute("children", children);
        return "pages/school/child-note-form";
    }

    @PostMapping("/save")
    public String saveChildNote(@AuthenticationPrincipal User teacher, @ModelAttribute ChildNote childNote) {
        childNote.setUser(teacher);
        childNote.setDateCreate(LocalDateTime.now());
        childNoteService.save(childNote);
        return "redirect:/school/child-notes";
    }

    @GetMapping("/{id}/edit")
    public String editChildNoteForm(@PathVariable Long id, @AuthenticationPrincipal User teacher, Model model) {
        ChildNote childNote = childNoteService.findById(id);
        if (childNote == null || !childNote.getUser().getId().equals(teacher.getId())) {
            return "redirect:/school/child-notes?error=not_authorized";
        }
        List<Child> children = childService.findByUser(teacher.getId());
        model.addAttribute("childNote", childNote);
        model.addAttribute("children", children);
        return "pages/school/child-note-form";
    }

    @PostMapping("/{id}/delete")
    public String deleteChildNote(@PathVariable Long id, @AuthenticationPrincipal User teacher) {
        ChildNote childNote = childNoteService.findById(id);
        if (childNote != null && childNote.getUser().getId().equals(teacher.getId())) {
            childNoteService.delete(id);
        }
        return "redirect:/school/child-notes";
    }
}
