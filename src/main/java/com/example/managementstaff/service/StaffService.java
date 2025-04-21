package com.example.managementstaff.service;

import com.example.managementstaff.entity.Staff;
import com.example.managementstaff.entity.StaffMajorFacility;
import com.example.managementstaff.entity.ImportHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface StaffService {
    Staff save(Staff staff);
    Staff update(Staff staff);
    Staff findById(UUID id);
    void deleteById(UUID id);
    void toggleStatus(UUID id);
    Page<Staff> findByKeywordAndStatus(String keyword, Integer status, Pageable pageable);
    ImportHistory importStaff(MultipartFile file, String username);
    byte[] downloadTemplate();
    void validateStaff(Staff staff, boolean isNew);
    void addMajorFacility(UUID staffId, UUID majorId, UUID facilityId);
    void removeMajorFacility(UUID staffMajorFacilityId);
    void validateAssignment(UUID staffId, UUID facilityId, UUID majorId) throws IllegalStateException;
    List<StaffMajorFacility> findAssignmentsByStaffId(UUID staffId);
    void addAssignment(UUID staffId, UUID majorId, UUID facilityId);
    void addAssignmentWithDepartment(UUID staffId, UUID majorId, UUID departmentId, UUID facilityId);
    void removeAssignment(UUID assignmentId);
    void checkAssignment(UUID staffId, UUID facilityId, UUID majorId);
    boolean hasFacilityAssignment(UUID staffId, UUID facilityId);
    Page<ImportHistory> getImportHistory(Pageable pageable);
} 