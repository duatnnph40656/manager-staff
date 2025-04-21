package com.example.managementstaff.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "import_history")
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "success_records")
    private Integer successRecords;

    @Column(name = "failed_records")
    private Integer failedRecords;

    @Column(columnDefinition = "TEXT")
    private String errors;

    @Column(name = "import_date", nullable = false)
    private LocalDateTime importDate;

    @Column(name = "imported_by", nullable = false)
    private String importedBy;

    @PrePersist
    protected void onCreate() {
        importDate = LocalDateTime.now();
    }
} 