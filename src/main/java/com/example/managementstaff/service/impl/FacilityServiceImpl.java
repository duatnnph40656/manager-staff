package com.example.managementstaff.service.impl;

import com.example.managementstaff.entity.Facility;
import com.example.managementstaff.repository.FacilityRepository;
import com.example.managementstaff.service.FacilityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;

    @Override
    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    @Override
    public Facility findById(UUID id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Facility not found with id: " + id));
    }

    @Override
    @Transactional
    public Facility save(Facility facility) {
        return facilityRepository.save(facility);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        facilityRepository.deleteById(id);
    }
} 