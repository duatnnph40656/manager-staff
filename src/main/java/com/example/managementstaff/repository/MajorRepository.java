package com.example.managementstaff.repository;

import com.example.managementstaff.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, UUID> {
    // Phương thức này cần được xem xét lại vì Major không có trường department
    // List<Major> findByDepartmentId(UUID departmentId);

    @Query("SELECT m FROM Major m JOIN m.majorFacilities mf JOIN mf.departmentFacility df JOIN df.facility f WHERE f.id = :facilityId")
    List<Major> findByFacilityId(@Param("facilityId") UUID facilityId);

    @Query("SELECT DISTINCT m FROM Major m " +
           "JOIN m.majorFacilities mf " +
           "JOIN mf.departmentFacility df " +
           "JOIN df.department d " +
           "JOIN df.facility f " +
           "WHERE d.id = :departmentId " +
           "AND (:facilityId IS NULL OR f.id = :facilityId)")
    List<Major> findByDepartmentIdAndFacilityId(@Param("departmentId") UUID departmentId, 
                                               @Param("facilityId") UUID facilityId);

    @Query("SELECT DISTINCT m FROM Major m " +
           "JOIN m.majorFacilities mf " +
           "JOIN mf.departmentFacility df " +
           "JOIN df.department d " +
           "WHERE d.id = :departmentId")
    List<Major> findByDepartmentId(@Param("departmentId") UUID departmentId);

    @Query("SELECT m FROM Major m " +
           "JOIN m.majorFacilities mf " +
           "JOIN mf.departmentFacility df " +
           "JOIN df.department d " +
           "JOIN df.facility f " +
           "WHERE d.id = :departmentId " +
           "AND f.id = :facilityId")
    List<Major> findByDepartmentAndFacility(@Param("departmentId") UUID departmentId, 
                                          @Param("facilityId") UUID facilityId);
    
    @Query("SELECT COUNT(m) > 0 FROM Major m " +
           "JOIN m.majorFacilities mf " +
           "JOIN mf.departmentFacility df " +
           "JOIN df.facility f " +
           "WHERE m.id = :majorId AND f.id = :facilityId")
    boolean existsByMajorIdAndFacilityId(@Param("majorId") UUID majorId, 
                                        @Param("facilityId") UUID facilityId);
} 