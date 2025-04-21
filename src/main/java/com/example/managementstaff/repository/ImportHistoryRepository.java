package com.example.managementstaff.repository;

import com.example.managementstaff.entity.ImportHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {
    Page<ImportHistory> findAllByOrderByImportDateDesc(Pageable pageable);
} 