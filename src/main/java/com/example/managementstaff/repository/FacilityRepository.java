package com.example.managementstaff.repository;

import com.example.managementstaff.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {
} 