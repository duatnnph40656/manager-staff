package com.example.managementstaff.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Department with its associated Majors
 * Used for displaying department-major combinations in the import staff form
 */
@Data
public class DepartmentMajorDto {
    private UUID departmentId;
    private String departmentName;
    private String departmentCode;
    private String facilityCode;
    private List<MajorDto> majors;
} 