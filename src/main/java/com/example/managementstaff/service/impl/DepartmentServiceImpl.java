package com.example.managementstaff.service.impl;

import com.example.managementstaff.entity.Department;
import com.example.managementstaff.repository.DepartmentRepository;
import com.example.managementstaff.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public List<Department> findByFacilityId(UUID facilityId) {
        return departmentRepository.findByFacilityId(facilityId);
    }
    
    @Override
    @Transactional
    public Department save(Department department) {
        return departmentRepository.save(department);
    }
    
    @Override
    public Department findById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
    }
} 