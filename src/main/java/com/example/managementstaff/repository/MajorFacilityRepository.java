package com.example.managementstaff.repository;

import com.example.managementstaff.entity.MajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MajorFacilityRepository extends JpaRepository<MajorFacility, UUID> {
    @Query("SELECT mf FROM MajorFacility mf " +
           "JOIN FETCH mf.major m " +
           "JOIN FETCH mf.departmentFacility df " +
           "JOIN FETCH df.department d " +
           "JOIN FETCH df.facility f " +
           "WHERE m.id = :majorId AND f.id = :facilityId")
    Optional<MajorFacility> findByMajorIdAndFacilityId(
            @Param("majorId") UUID majorId, 
            @Param("facilityId") UUID facilityId);
    
    @Query("SELECT mf FROM MajorFacility mf " +
           "JOIN FETCH mf.major m " +
           "JOIN FETCH mf.departmentFacility df " +
           "JOIN FETCH df.department d " +
           "WHERE d.id = :departmentId")
    List<MajorFacility> findByDepartmentId(@Param("departmentId") UUID departmentId);
    
    @Query("SELECT mf FROM MajorFacility mf " +
           "JOIN FETCH mf.major m " +
           "JOIN FETCH mf.departmentFacility df " +
           "JOIN FETCH df.department d " +
           "JOIN FETCH df.facility f " +
           "WHERE d.id = :departmentId AND f.id = :facilityId")
    List<MajorFacility> findByDepartmentIdAndFacilityId(
            @Param("departmentId") UUID departmentId, 
            @Param("facilityId") UUID facilityId);
} 