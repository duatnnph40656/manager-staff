package com.example.managementstaff.controller;

import com.example.managementstaff.dto.DepartmentDto;
import com.example.managementstaff.dto.DepartmentMajorDto;
import com.example.managementstaff.dto.FacilityDto;
import com.example.managementstaff.dto.MajorDto;
import com.example.managementstaff.entity.Department;
import com.example.managementstaff.entity.DepartmentFacility;
import com.example.managementstaff.entity.Facility;
import com.example.managementstaff.entity.Major;
import com.example.managementstaff.repository.DepartmentFacilityRepository;
import com.example.managementstaff.service.DepartmentService;
import com.example.managementstaff.service.FacilityService;
import com.example.managementstaff.service.MajorService;
import com.example.managementstaff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final DepartmentService departmentService;
    private final MajorService majorService;
    private final StaffService staffService;
    private final FacilityService facilityService;
    private final DepartmentFacilityRepository departmentFacilityRepository;

    @Transactional(readOnly = true)
    @GetMapping("/facilities/all")
    public List<FacilityDto> getAllFacilities() {
        return facilityService.findAll().stream()
                .map(FacilityDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/departments/all")
    public List<DepartmentDto> getAllDepartments() {
        return departmentService.findAll().stream()
                .map(DepartmentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/majors/all")
    public List<MajorDto> getAllMajors() {
        return majorService.findAll().stream()
                .map(MajorDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/departments/by-facility/{facilityId}")
    @Transactional(readOnly = true)
    public List<DepartmentDto> getDepartmentsByFacility(@PathVariable UUID facilityId) {
        return departmentService.findByFacilityId(facilityId).stream()
                .map(DepartmentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/majors/by-department/{departmentId}/facility/{facilityId}")
    @Transactional(readOnly = true)
    public List<MajorDto> getMajorsByDepartmentAndFacility(
            @PathVariable UUID departmentId,
            @PathVariable UUID facilityId) {
        return majorService.findByDepartmentIdAndFacilityId(departmentId, facilityId).stream()
                .map(MajorDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/majors/by-department-facility")
    @Transactional(readOnly = true)
    public List<MajorDto> getMajorsByDepartmentAndFacilityParams(
            @RequestParam UUID departmentId,
            @RequestParam UUID facilityId) {
        return majorService.findByDepartmentIdAndFacilityId(departmentId, facilityId).stream()
                .map(MajorDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Endpoints for form dropdowns
    @GetMapping("/facilities/{facilityId}/departments")
    @Transactional(readOnly = true)
    public List<DepartmentDto> getDepartmentsForFacility(@PathVariable UUID facilityId) {
        return departmentService.findByFacilityId(facilityId).stream()
                .map(DepartmentDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/departments/{departmentId}/majors")
    @Transactional(readOnly = true)
    public List<MajorDto> getMajorsForDepartment(@PathVariable UUID departmentId) {
        return majorService.findByDepartmentId(departmentId).stream()
                .map(MajorDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/staff/{staffId}/check-assignment")
    public ResponseEntity<?> checkStaffAssignment(
            @PathVariable UUID staffId,
            @RequestBody Map<String, UUID> request) {
        try {
            staffService.validateAssignment(
                staffId,
                request.get("facilityId"),
                request.get("majorId")
            );
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/staff/{staffId}/check-facility-assignment/{facilityId}")
    public boolean checkFacilityAssignment(
            @PathVariable UUID staffId,
            @PathVariable UUID facilityId) {
        // Returns true if staff already has an assignment at this facility
        return staffService.hasFacilityAssignment(staffId, facilityId);
    }

    /**
     * Get all department-major combinations for a specific facility
     * This is used for the staff import dropdown
     */
    @GetMapping("/facilities/{facilityId}/department-majors")
    @Transactional(readOnly = true)
    public List<DepartmentMajorDto> getDepartmentMajorsForFacility(@PathVariable UUID facilityId) {
        List<DepartmentMajorDto> result = new ArrayList<>();
        
        // Get facility information
        Facility facility = facilityService.findById(facilityId);
        if (facility == null) {
            return result;
        }
        
        // Get all departments for this facility
        List<DepartmentFacility> departmentFacilities = departmentFacilityRepository.findByFacilityId(facilityId);
        
        for (DepartmentFacility df : departmentFacilities) {
            Department department = df.getDepartment();
            
            // Get all majors for this department-facility combination
            List<Major> majors = majorService.findByDepartmentIdAndFacilityId(department.getId(), facilityId);
            
            // Create a DTO for each department with its majors
            DepartmentMajorDto dto = new DepartmentMajorDto();
            dto.setDepartmentId(department.getId());
            dto.setDepartmentName(department.getName());
            dto.setDepartmentCode(department.getCode());
            
            List<MajorDto> majorDtos = majors.stream()
                .map(major -> {
                    MajorDto majorDto = new MajorDto();
                    majorDto.setId(major.getId());
                    majorDto.setName(major.getName());
                    majorDto.setCode(major.getCode());
                    return majorDto;
                })
                .collect(Collectors.toList());
            
            dto.setMajors(majorDtos);
            dto.setFacilityCode(facility.getCode());
            result.add(dto);
        }
        
        return result;
    }
} 