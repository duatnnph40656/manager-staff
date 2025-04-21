package com.example.managementstaff.dto;

import com.example.managementstaff.entity.Department;
import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
    private UUID id;
    private String name;
    private String code;

    // Static factory method
    public static DepartmentDto fromEntity(Department department) {
        return new DepartmentDto(department.getId(), department.getName(), department.getCode());
    }
} 