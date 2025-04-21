package com.example.managementstaff.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class StaffImportDTO {
    private String staffCode;
    private String name;
    private String accountFpt;
    private String accountFe;
    private UUID facilityId;
    private UUID departmentId;
    private UUID majorId;
    
    private List<String> errors = new ArrayList<>();
    
    public void addError(String error) {
        errors.add(error);
    }
    
    public String getErrors() {
        return String.join(", ", errors);
    }
    
    public boolean isValid() {
        return errors.isEmpty();
    }
} 