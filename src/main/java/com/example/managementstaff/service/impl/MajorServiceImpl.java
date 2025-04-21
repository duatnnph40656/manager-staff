package com.example.managementstaff.service.impl;

import com.example.managementstaff.entity.Major;
import com.example.managementstaff.repository.MajorRepository;
import com.example.managementstaff.service.MajorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MajorServiceImpl implements MajorService {
    private final MajorRepository majorRepository;

    @Override
    public List<Major> findAll() {
        return majorRepository.findAll();
    }

    @Override
    public Major findById(UUID id) {
        return majorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Major not found with id: " + id));
    }

    @Override
    @Transactional
    public Major save(Major major) {
        return majorRepository.save(major);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        majorRepository.deleteById(id);
    }

    @Override
    public List<Major> findByDepartmentIdAndFacilityId(UUID departmentId, UUID facilityId) {
        return majorRepository.findByDepartmentIdAndFacilityId(departmentId, facilityId);
    }

    @Override
    public List<Major> findByDepartmentId(UUID departmentId) {
        return majorRepository.findByDepartmentId(departmentId);
    }
} 