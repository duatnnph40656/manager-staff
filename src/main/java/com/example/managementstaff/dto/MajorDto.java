package com.example.managementstaff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorDto {
    private UUID id;
    private String name;
    private String code;
    
    // Static factory method
    public static MajorDto fromEntity(com.example.managementstaff.entity.Major major) {
        return new MajorDto(
            major.getId(),
            major.getName(),
            major.getCode()
        );
    }
} 