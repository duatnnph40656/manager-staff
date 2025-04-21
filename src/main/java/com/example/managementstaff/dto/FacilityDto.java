package com.example.managementstaff.dto;

import com.example.managementstaff.entity.Facility;
import lombok.Data;
import java.util.UUID;

@Data
public class FacilityDto {
    private UUID id;
    private String name;
    private String code;

    public static FacilityDto fromEntity(Facility facility) {
        FacilityDto dto = new FacilityDto();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setCode(facility.getCode());
        return dto;
    }
} 