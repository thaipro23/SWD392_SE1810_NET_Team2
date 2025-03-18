package com.pjb.kindergarten_suggestion.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.pjb.kindergarten_suggestion.dto.FaqDTO;
import com.pjb.kindergarten_suggestion.entities.FAQ;

public interface FaqService {
    List<FAQ> getAll();
    List<FAQ> getAllIsTrue() ;
    Page<FAQ> getFaqByKeyWithPagination(String keyword, int page, int size);

    Page<FAQ> getFaqWithPagination(int page, int size);

    FAQ getFaqById(Long id);

    void handleSaveFaq(FAQ faq);

    void handleDeleteFaq(FAQ faq);

    FaqDTO convertFAQToDto(FAQ faq);

    
    FAQ convertFaqDtoToFAQ(FaqDTO faqDTO);
}

    