package com.pjb.kindergarten_suggestion.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pjb.kindergarten_suggestion.dto.FaqDTO;
import com.pjb.kindergarten_suggestion.entities.FAQ;
import com.pjb.kindergarten_suggestion.repositories.FaqRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaqServiceImpl implements FaqService {
    private final FaqRepository faqRepository;

    @Override
    public List<FAQ> getAll() {
        return faqRepository.findAll();
    }

    @Override
    public List<FAQ> getAllIsTrue() {
        return faqRepository.findFaqByStatusTrue();
    }

    @Override
    public Page<FAQ> getFaqByKeyWithPagination(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return faqRepository.searchFaqByKeywordNative(keyword, pageable);
    }

    @Override
    public Page<FAQ> getFaqWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return faqRepository.findFaq(pageable);
    }

    @Override
    public FAQ getFaqById(Long id) {
        return faqRepository.findById(id).get();
    }

    @Override
    public void handleSaveFaq(FAQ faq) {
        faqRepository.save(faq);
    }

    @Override
    public FaqDTO convertFAQToDto(FAQ faq) {
        FaqDTO faqDTO = new FaqDTO();
        faqDTO.setId(faq.getId());
        faqDTO.setQuestion(faq.getQuestion());
        faqDTO.setAnswer(faq.getAnswer());
        faqDTO.setStatus(faq.isStatus());
        return faqDTO;
    }

    @Override
    public FAQ convertFaqDtoToFAQ(FaqDTO faqDTO) {
        FAQ faq = new FAQ();
        faq.setQuestion(faqDTO.getQuestion());
        faq.setAnswer(faqDTO.getAnswer());
        faq.setStatus(faqDTO.isStatus());
        return faq;
    }

    @Override
    public void handleDeleteFaq(FAQ faq) {
        faqRepository.deleteById(faq.getId());
    }
}
