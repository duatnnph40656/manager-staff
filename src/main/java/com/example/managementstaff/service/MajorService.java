package com.example.managementstaff.service;

import com.example.managementstaff.entity.Major;
import java.util.List;
import java.util.UUID;

public interface MajorService {
    List<Major> findAll();
    Major findById(UUID id);
    Major save(Major major);
    void deleteById(UUID id);
    List<Major> findByDepartmentIdAndFacilityId(UUID departmentId, UUID facilityId);
    List<Major> findByDepartmentId(UUID departmentId);
} 