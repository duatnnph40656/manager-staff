package com.example.managementstaff.controller;

import com.example.managementstaff.entity.ImportHistory;
import com.example.managementstaff.repository.ImportHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/import-history")
@RequiredArgsConstructor
@Slf4j
public class ImportHistoryController {
    private final ImportHistoryRepository importHistoryRepository;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "10") int size,
                      Model model) {
        try {
            log.info("Fetching import history page: {}, size: {}", page, size);
            
            Page<ImportHistory> historyPage = importHistoryRepository.findAllByOrderByImportDateDesc(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "importDate")));
    
            model.addAttribute("historyList", historyPage.getContent());
            model.addAttribute("currentPage", page + 1);
            model.addAttribute("totalPages", historyPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("hasNext", historyPage.hasNext());
            model.addAttribute("hasPrevious", historyPage.hasPrevious());
            model.addAttribute("firstPage", page == 0);
            model.addAttribute("lastPage", page == historyPage.getTotalPages() - 1);
            
            return "staff/import-history";
        } catch (Exception e) {
            log.error("Error fetching import history: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Không thể tải lịch sử import: " + e.getMessage());
            model.addAttribute("historyList", java.util.Collections.emptyList());
            return "staff/import-history";
        }
    }
} 