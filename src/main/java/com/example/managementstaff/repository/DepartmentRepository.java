package com.example.managementstaff.repository;

import com.example.managementstaff.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    @Query("SELECT DISTINCT d FROM Department d JOIN d.departmentFacilities df JOIN df.facility f WHERE f.id = :facilityId")
    List<Department> findByFacilityId(@Param("facilityId") UUID facilityId);
} 