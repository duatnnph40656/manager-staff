package com.example.managementstaff.service;

import com.example.managementstaff.entity.Facility;
import java.util.List;
import java.util.UUID;

public interface FacilityService {
    List<Facility> findAll();
    Facility findById(UUID id);
    Facility save(Facility facility);
    void deleteById(UUID id);
} 