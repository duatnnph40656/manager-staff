package com.example.managementstaff.repository;

import com.example.managementstaff.entity.StaffMajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffMajorFacilityRepository extends JpaRepository<StaffMajorFacility, UUID> {
    @Query("SELECT smf FROM StaffMajorFacility smf " +
           "LEFT JOIN FETCH smf.staff " +
           "LEFT JOIN FETCH smf.majorFacility mf " + 
           "LEFT JOIN FETCH mf.major " +
           "LEFT JOIN FETCH mf.departmentFacility df " +
           "LEFT JOIN FETCH df.facility " + 
           "LEFT JOIN FETCH df.department " +
           "WHERE smf.staff.id = :staffId")
    List<StaffMajorFacility> findByStaffId(@Param("staffId") UUID staffId);

    @Query("SELECT CASE WHEN COUNT(smf) > 0 THEN true ELSE false END FROM StaffMajorFacility smf " +
           "JOIN smf.majorFacility mf JOIN mf.departmentFacility df JOIN df.facility f " +
           "WHERE smf.staff.id = :staffId AND f.id = :facilityId")
    boolean existsByStaffIdAndFacilityId(@Param("staffId") UUID staffId, @Param("facilityId") UUID facilityId);

    @Query("SELECT smf FROM StaffMajorFacility smf " +
           "JOIN smf.majorFacility mf JOIN mf.departmentFacility df JOIN df.facility f " +
           "WHERE smf.staff.id = :staffId AND f.id = :facilityId")
    List<StaffMajorFacility> findByStaffIdAndFacilityId(@Param("staffId") UUID staffId, @Param("facilityId") UUID facilityId);
} 