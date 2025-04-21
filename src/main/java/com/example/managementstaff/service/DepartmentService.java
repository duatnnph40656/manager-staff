package com.example.managementstaff.service;

import com.example.managementstaff.entity.Department;
import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    List<Department> findAll();
    List<Department> findByFacilityId(UUID facilityId);
    Department save(Department department);
    Department findById(UUID id);
} 