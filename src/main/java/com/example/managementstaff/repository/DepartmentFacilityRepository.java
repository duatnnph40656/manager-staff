package com.example.managementstaff.repository;

import com.example.managementstaff.entity.DepartmentFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentFacilityRepository extends JpaRepository<DepartmentFacility, UUID> {
    @Query("SELECT df FROM DepartmentFacility df " +
           "JOIN FETCH df.department d " +
           "JOIN FETCH df.facility f " +
           "WHERE d.id = :departmentId AND f.id = :facilityId")
    Optional<DepartmentFacility> findByDepartmentIdAndFacilityId(
            @Param("departmentId") UUID departmentId, 
            @Param("facilityId") UUID facilityId);
    
    @Query("SELECT df FROM DepartmentFacility df " +
           "JOIN FETCH df.department d " +
           "JOIN FETCH df.facility f " +
           "WHERE f.id = :facilityId")
    List<DepartmentFacility> findByFacilityId(@Param("facilityId") UUID facilityId);
    
    @Query("SELECT df FROM DepartmentFacility df " +
           "JOIN FETCH df.department d " +
           "JOIN FETCH df.facility f " +
           "WHERE d.id = :departmentId")
    List<DepartmentFacility> findByDepartmentId(@Param("departmentId") UUID departmentId);
} 