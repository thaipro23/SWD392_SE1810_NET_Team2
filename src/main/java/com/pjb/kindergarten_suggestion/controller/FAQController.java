package com.pjb.kindergarten_suggestion.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;

import com.pjb.kindergarten_suggestion.common.utils.AppRoutes;
import com.pjb.kindergarten_suggestion.dto.FaqDTO;
import com.pjb.kindergarten_suggestion.entities.FAQ;
import com.pjb.kindergarten_suggestion.services.FaqService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class FAQController {
    private final FaqService faqService;

    @GetMapping(AppRoutes.FAQ_PAGE)
    public String faq(Model model) {
        model.addAttribute("pageTitle", "FAQ");

        return "/pages/faq";
    }

    @GetMapping(AppRoutes.ADMIN_FAQ)
    public String showListFAQ(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model) {
        model.addAttribute("pageTitle", "FAQ Management");
        if (page < 0) {
            page = 0;
        }
        Page<FAQ> faqsPage = (keyword != null && !keyword.isEmpty())
                ? faqService.getFaqByKeyWithPagination(keyword, page, size)
                : faqService.getFaqWithPagination(page, size);

        int totalPages = faqsPage.getTotalPages();

        if (totalPages > 0 && page >= totalPages) {
            page = totalPages - 1;
            faqsPage = (keyword != null && !keyword.isEmpty())
                    ? faqService.getFaqByKeyWithPagination(keyword, page, size)
                    : faqService.getFaqWithPagination(page, size);
        }
        model.addAttribute("faqsPage", faqsPage);
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

        if (faqsPage == null || faqsPage.getTotalElements() == 0) {
            model.addAttribute("noUsersMessage", "No results found.");
            model.addAttribute("totalPages", 1);
        } else {
            model.addAttribute("totalPages", faqsPage.getTotalPages());
        }
        return "/pages/admin/faq/faq-management";
    }

    @GetMapping(AppRoutes.ADMIN_FAQ_DETAIL)
    public String getFaqDetail(Model model, @PathVariable long id) {
        model.addAttribute("pageTitle", "FAQ Detail");
        FAQ faq = this.faqService.getFaqById(id);
        model.addAttribute("faq", faq);
        return "pages/admin/faq/faq-detail";
    }

    @GetMapping(AppRoutes.ADMIN_FAQ_UPDATE)
    public String getFaqUpdatePage(Model model, @PathVariable long id, RedirectAttributes redirectAttributes) {
        FAQ faq = this.faqService.getFaqById(id);

        if (faq == null) {
            redirectAttributes.addFlashAttribute("error", "FAQ is not exist.");
            return "redirect:/admin/faq";
        }

        model.addAttribute("pageTitle", "FAQ Update");
        FaqDTO faqDTO = faqService.convertFAQToDto(faq);
        model.addAttribute("faq", faqDTO);

        return "pages/admin/faq/faq-update";
    }

    @PostMapping(AppRoutes.ADMIN_FAQ_UPDATE)
    public String handleUpdateFaq(
            Model model,
            @PathVariable long id,
            @ModelAttribute("faq") @Valid FaqDTO faqDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        FAQ existingFaq = faqService.getFaqById(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Wrong something!");
            return "pages/admin/faq/faq-update";
        }

        if (existingFaq == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "FAQ is not exist.");
            return "redirect:/admin/faq/detail/" + id;
        }

        existingFaq.setQuestion(faqDTO.getQuestion());
        existingFaq.setAnswer(faqDTO.getAnswer());
        existingFaq.setStatus(faqDTO.isStatus());

        faqService.handleSaveFaq(existingFaq);
        redirectAttributes.addFlashAttribute("successMessage", "FAQ updated successfully.");
        return "redirect:/admin/faq/detail/" + id;
    }

    @PostMapping(AppRoutes.ADMIN_FAQ_DELETE)
    public String handleDeleteFaq(
            Model model,
            @PathVariable long id,
            RedirectAttributes redirectAttributes) {

        FAQ existingFaq = faqService.getFaqById(id);
        if (existingFaq == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "FAQ is not exist.");
            return "redirect:/admin/faq/detail/" + id;
        }
        faqService.handleDeleteFaq(existingFaq);
        redirectAttributes.addFlashAttribute("successMessage", "FAQ is deleted successfully.");
        return "redirect:/admin/faq";
    }

    @GetMapping(AppRoutes.ADMIN_FAQ_CREATE)
    public String getFaqCreatePage(Model model) {
        model.addAttribute("pageTitle", "FAQ Create");
        model.addAttribute("faq", new FaqDTO());
        return "pages/admin/faq/faq-create";
    }

    @PostMapping(AppRoutes.ADMIN_FAQ_CREATE)
    public String registerPost(@Valid @ModelAttribute("faq") FaqDTO faqDTO,
            BindingResult result, Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Wrong something!");
            return "pages/admin/faq/faq-create";
        }
        try {
            FAQ newFaq = faqService.convertFaqDtoToFAQ(faqDTO);
            this.faqService.handleSaveFaq(newFaq);
            model.addAttribute("successMessage", "Add new FAQ successfully!");
            return "redirect:" + AppRoutes.ADMIN_FAQ;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e);
            return "pages/admin/faq/faq-create";
        }
    }

}
