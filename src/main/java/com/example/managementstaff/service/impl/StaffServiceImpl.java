package com.example.managementstaff.service.impl;

import com.example.managementstaff.dto.StaffImportDTO;
import com.example.managementstaff.entity.*;
import com.example.managementstaff.repository.*;
import com.example.managementstaff.service.DepartmentService;
import com.example.managementstaff.service.StaffService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;
    private final StaffMajorFacilityRepository staffMajorFacilityRepository;
    private final MajorRepository majorRepository;
    private final MajorFacilityRepository majorFacilityRepository;
    private final FacilityRepository facilityRepository;
    private final DepartmentFacilityRepository departmentFacilityRepository;
    private final ImportHistoryRepository importHistoryRepository;
    private final DepartmentService departmentService;

    private static final Pattern EMAIL_FPT_PATTERN = Pattern.compile("^[a-zA-Z0-9]+@fpt\\.edu\\.vn$");
    private static final Pattern EMAIL_FE_PATTERN = Pattern.compile("^[a-zA-Z0-9]+@fe\\.edu\\.vn$");

    private List<Department> departmentCache;
    private List<Major> majorCache;
    private List<Facility> facilityCache;

    @Override
    @Transactional
    public Staff save(Staff staff) {
        validateStaff(staff, true);
        return staffRepository.save(staff);
    }

    @Override
    @Transactional
    public Staff update(Staff staff) {
        validateStaff(staff, false);
        return staffRepository.save(staff);
    }

    @Override
    public Staff findById(UUID id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên"));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        staffRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleStatus(UUID id) {
        Staff staff = findById(id);
        staff.setStatus(staff.getStatus() == 1 ? (byte)0 : (byte)1);
        staffRepository.save(staff);
    }

    @Override
    public Page<Staff> findByKeywordAndStatus(String keyword, Integer status, Pageable pageable) {
        return staffRepository.findByKeywordAndStatus(keyword, status, pageable);
    }

    @Override
    @Transactional
    public void addMajorFacility(UUID staffId, UUID majorId, UUID facilityId) {
        Staff staff = findById(staffId);
        

        // Check if staff already has a major in this facility
        if (staffMajorFacilityRepository.existsByStaffIdAndFacilityId(staffId, facilityId)) {
            throw new IllegalStateException("Nhân viên đã có bộ môn chuyên ngành tại cơ sở này");
        }

        // Get or create the appropriate MajorFacility
        // This may need to be adjusted based on your actual data model
        MajorFacility majorFacility = getMajorFacility(majorId, facilityId);
        
        StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
        staffMajorFacility.setStaff(staff);
        staffMajorFacility.setMajorFacility(majorFacility);
        staffMajorFacility.setCreatedDate(System.currentTimeMillis());
        staffMajorFacility.setLastModifiedDate(System.currentTimeMillis());
        staffMajorFacility.setStatus((byte)1);
        staffMajorFacilityRepository.save(staffMajorFacility);
    }

    // Helper method to find or create MajorFacility
    private MajorFacility getMajorFacility(UUID majorId, UUID facilityId) {
        // This implementation depends on your data model and repository methods
        // You'll need to implement this based on your actual requirements
        throw new UnsupportedOperationException("Implementation needed based on data model");
    }

    @Override
    @Transactional
    public void removeMajorFacility(UUID staffMajorFacilityId) {
        staffMajorFacilityRepository.deleteById(staffMajorFacilityId);
    }

    @Override
    public void validateStaff(Staff staff, boolean isNew) {
        if (staff.getStaffCode() == null || staff.getStaffCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống");
        }
        if (staff.getStaffCode().length() > 15) {
            throw new IllegalArgumentException("Mã nhân viên không được vượt quá 15 ký tự");
        }
        if (staff.getName() == null || staff.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }
        if (staff.getName().length() > 100) {
            throw new IllegalArgumentException("Tên nhân viên không được vượt quá 100 ký tự");
        }
        if (staff.getAccountFpt() == null || !EMAIL_FPT_PATTERN.matcher(staff.getAccountFpt()).matches()) {
            throw new IllegalArgumentException("Email FPT không hợp lệ");
        }
        if (staff.getAccountFe() == null || !EMAIL_FE_PATTERN.matcher(staff.getAccountFe()).matches()) {
            throw new IllegalArgumentException("Email FE không hợp lệ");
        }

        // Check for duplicates
        if (isNew) {
            if (staffRepository.existsByStaffCode(staff.getStaffCode())) {
                throw new IllegalArgumentException("Mã nhân viên đã tồn tại");
            }
            if (staffRepository.existsByAccountFpt(staff.getAccountFpt())) {
                throw new IllegalArgumentException("Email FPT đã tồn tại");
            }
            if (staffRepository.existsByAccountFe(staff.getAccountFe())) {
                throw new IllegalArgumentException("Email FE đã tồn tại");
            }
        } else {
            if (staffRepository.existsByStaffCodeAndIdNot(staff.getStaffCode(), staff.getId())) {
                throw new IllegalArgumentException("Mã nhân viên đã tồn tại");
            }
            if (staffRepository.existsByAccountFptAndIdNot(staff.getAccountFpt(), staff.getId())) {
                throw new IllegalArgumentException("Email FPT đã tồn tại");
            }
            if (staffRepository.existsByAccountFeAndIdNot(staff.getAccountFe(), staff.getId())) {
                throw new IllegalArgumentException("Email FE đã tồn tại");
            }
        }
    }

    @Override
    public byte[] downloadTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Staff Import Template");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Mã Nhân Viên", "Họ và Tên", "Email FPT", "Email FE", "Bộ Môn - Chuyên Ngành"};
            
            // Create header cell style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Add sample data
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue(1);
            sampleRow.createCell(1).setCellValue("NV001");
            sampleRow.createCell(2).setCellValue("Nguyễn Văn A");
            sampleRow.createCell(3).setCellValue("nguyenvana@fpt.edu.vn");
            sampleRow.createCell(4).setCellValue("nguyenvana@fe.edu.vn");
            sampleRow.createCell(5).setCellValue("Ứng dụng phần mềm - Java - Hà Nội");
            
            // Set column width for email columns a bit wider
            sheet.setColumnWidth(3, 30 * 256); // Email FPT
            sheet.setColumnWidth(4, 30 * 256); // Email FE
            sheet.setColumnWidth(5, 40 * 256); // Bộ Môn - Chuyên Ngành
            
            // Create dropdown list for Department - Major - Facility
            XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
            List<Object[]> combinations = getValidCombinationsWithNames();
            String[] dropdownValues = combinations.stream()
                .map(combo -> String.format("%s - %s - %s", combo[0], combo[1], combo[2]))
                .toArray(String[]::new);
            
            XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(dropdownValues);
            CellRangeAddressList addressList = new CellRangeAddressList(1, 100, 5, 5); // Apply to rows 2-101, column 6
            XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo template", e);
        }
    }
    
    /**
     * Helper method to get all valid department-major-facility combinations with names
     */
    @Transactional(readOnly = true)
    private List<Object[]> getValidCombinationsWithNames() {
        List<Object[]> result = new ArrayList<>();
        
        // Get all department facilities
        List<DepartmentFacility> departmentFacilities = departmentFacilityRepository.findAll();
        
        for (DepartmentFacility df : departmentFacilities) {
            Department department = df.getDepartment();
            Facility facility = df.getFacility();
            
            // Get all majors for this department-facility combination
            List<Major> majors = majorRepository.findByDepartmentIdAndFacilityId(department.getId(), facility.getId());
            
            for (Major major : majors) {
                result.add(new Object[] {
                    department.getName(),
                    major.getName(),
                    facility.getName()
                });
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public ImportHistory importStaff(MultipartFile file, String username) {
        ImportHistory importHistory = new ImportHistory();
        importHistory.setFileName(file.getOriginalFilename());
        importHistory.setImportedBy(username);
        List<String> errors = new ArrayList<>();
        int totalRecords = 0;
        int successRecords = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            totalRecords = sheet.getPhysicalNumberOfRows() - 1; // Exclude header row

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    StaffImportDTO dto = new StaffImportDTO();
                    
                    // Get basic staff info
                    dto.setStaffCode(getCellValueAsString(row.getCell(1))); // Column B - Mã Nhân Viên
                    dto.setName(getCellValueAsString(row.getCell(2))); // Column C - Họ và Tên
                    dto.setAccountFpt(getCellValueAsString(row.getCell(3))); // Column D - Email FPT
                    dto.setAccountFe(getCellValueAsString(row.getCell(4))); // Column E - Email FE
                    
                    // Parse department-major combination from column F, facility is optional
                    String combinationString = getCellValueAsString(row.getCell(5)); // Column F - Bộ Môn - Chuyên Ngành
                    if (combinationString != null && !combinationString.trim().isEmpty()) {
                        // Expected format: "DEPT_NAME - MAJOR_NAME" or "DEPT_NAME - MAJOR_NAME - FACILITY_NAME"
                        String[] parts = combinationString.split("-");
                        if (parts.length >= 2) {
                            String deptName = parts[0].trim();
                            String majorName = parts[1].trim();
                            
                            // Find corresponding IDs from names
                            Department department = findDepartmentByName(deptName);
                            Major major = findMajorByName(majorName);
                            
                            if (department != null && major != null) {
                                dto.setDepartmentId(department.getId());
                                dto.setMajorId(major.getId());
                                // Facility is optional, if provided, try to set it
                                if (parts.length == 3) {
                                    String facilityName = parts[2].trim();
                                    Facility facility = findFacilityByName(facilityName);
                                    if (facility != null) {
                                        dto.setFacilityId(facility.getId());
                                    } else {
                                        dto.addError("Không tìm thấy cơ sở với tên: " + facilityName);
                                    }
                                }
                            } else {
                                if (department == null) {
                                    dto.addError("Không tìm thấy bộ môn với tên: " + deptName);
                                }
                                if (major == null) {
                                    dto.addError("Không tìm thấy chuyên ngành với tên: " + majorName);
                                }
                            }
                        } else {
                            dto.addError("Định dạng cột Bộ Môn - Chuyên Ngành không đúng. Yêu cầu: 'TÊN_BỘ_MÔN - TÊN_CHUYÊN_NGÀNH' hoặc 'TÊN_BỘ_MÔN - TÊN_CHUYÊN_NGÀNH - TÊN_CƠ_SỞ'");
                        }
                    } else {
                        dto.addError("Cột Bộ Môn - Chuyên Ngành không được để trống");
                    }

                    validateImportDTO(dto);

                    // Check for duplicates before saving
                    if (dto.isValid()) {
                        if (staffRepository.existsByStaffCode(dto.getStaffCode()) || 
                            staffRepository.existsByAccountFpt(dto.getAccountFpt()) || 
                            staffRepository.existsByAccountFe(dto.getAccountFe())) {
                            errors.add("Dòng " + (i + 1) + ": Nhân viên đã tồn tại (mã, email FPT hoặc FE trùng). Bỏ qua.");
                            continue; // Skip this record
                        }

                        Staff staff = new Staff();
                        staff.setStaffCode(dto.getStaffCode());
                        staff.setName(dto.getName());
                        staff.setAccountFpt(dto.getAccountFpt());
                        staff.setAccountFe(dto.getAccountFe());
                        staff.setStatus((byte)1);

                        staff = save(staff);
                        
                        // Add assignment with department, facility is optional
                        if (dto.getFacilityId() != null) {
                            addAssignmentWithDepartment(
                                staff.getId(), 
                                dto.getMajorId(), 
                                dto.getDepartmentId(), 
                                dto.getFacilityId()
                            );
                        } else {
                            // If no facility, just add assignment without facility if possible
                            addAssignment(
                                staff.getId(), 
                                dto.getMajorId(), 
                                dto.getDepartmentId()
                            );
                        }
                        
                        successRecords++;
                    } else {
                        errors.add("Dòng " + (i + 1) + ": " + dto.getErrors());
                    }
                } catch (Exception e) {
                    errors.add("Dòng " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file", e);
        }

        importHistory.setTotalRecords(totalRecords);
        importHistory.setSuccessRecords(successRecords);
        importHistory.setFailedRecords(totalRecords - successRecords);
        importHistory.setErrors(String.join("\n", errors));
        return importHistoryRepository.save(importHistory);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }


    private void validateImportDTO(StaffImportDTO dto) {
        if (dto.getStaffCode() == null || dto.getStaffCode().trim().isEmpty()) {
            dto.addError("Mã nhân viên không được để trống");
        } else if (dto.getStaffCode().length() > 15) {
            dto.addError("Mã nhân viên không được vượt quá 15 ký tự");
        } else if (staffRepository.existsByStaffCode(dto.getStaffCode())) {
            dto.addError("Mã nhân viên đã tồn tại");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            dto.addError("Tên nhân viên không được để trống");
        } else if (dto.getName().length() > 100) {
            dto.addError("Tên nhân viên không được vượt quá 100 ký tự");
        }

        // Validate FPT email format only
        if (dto.getAccountFpt() == null || !EMAIL_FPT_PATTERN.matcher(dto.getAccountFpt()).matches()) {
            dto.addError("Email FPT không hợp lệ");
        } else if (staffRepository.existsByAccountFpt(dto.getAccountFpt())) {
            dto.addError("Email FPT đã tồn tại");
        }

        // Validate FE email format only
        if (dto.getAccountFe() == null || !EMAIL_FE_PATTERN.matcher(dto.getAccountFe()).matches()) {
            dto.addError("Email FE không hợp lệ");
        } else if (staffRepository.existsByAccountFe(dto.getAccountFe())) {
            dto.addError("Email FE đã tồn tại");
        }

        if (dto.getFacilityId() == null || !facilityRepository.existsById(dto.getFacilityId())) {
            dto.addError("Cơ sở không hợp lệ");
        }
        
        if (dto.getDepartmentId() == null) {
            dto.addError("Bộ môn không được để trống");
        } else {
            try {
                departmentService.findById(dto.getDepartmentId());
            } catch (Exception e) {
                dto.addError("Bộ môn không hợp lệ");
            }
        }

        if (dto.getMajorId() == null || !majorRepository.existsById(dto.getMajorId())) {
            dto.addError("Chuyên ngành không hợp lệ");
        }
        
        // Validate department-facility relationship
        if (dto.getFacilityId() != null && dto.getDepartmentId() != null) {
            Optional<DepartmentFacility> departmentFacility = departmentFacilityRepository.findByDepartmentIdAndFacilityId(
                dto.getDepartmentId(), dto.getFacilityId());
            if (departmentFacility.isEmpty()) {
                dto.addError("Bộ môn không thuộc cơ sở này");
            }
        }
        
        // Validate major-facility relationship
        if (dto.getFacilityId() != null && dto.getMajorId() != null) {
            boolean validMajorFacility = majorRepository.existsByMajorIdAndFacilityId(
                dto.getMajorId(), dto.getFacilityId());
            if (!validMajorFacility) {
                dto.addError("Chuyên ngành không thuộc cơ sở này");
            }
        }
    }

    @Override
    public void validateAssignment(UUID staffId, UUID facilityId, UUID majorId) throws IllegalStateException {
        // Kiểm tra xem nhân viên đã được phân công cho cơ sở này chưa
        if (staffMajorFacilityRepository.existsByStaffIdAndFacilityId(staffId, facilityId)) {
            throw new IllegalStateException("Nhân viên đã được phân công tại cơ sở này. Mỗi nhân viên chỉ được phân công một bộ môn chuyên ngành trong một cơ sở.");
        }

       
        
        // Kiểm tra xem major và facility có mối quan hệ không
        boolean validAssignment = majorRepository.existsByMajorIdAndFacilityId(majorId, facilityId);
        
        if (!validAssignment) {
            throw new IllegalStateException("Chuyên ngành không thuộc cơ sở này");
        }
    }

    @Override
    public List<StaffMajorFacility> findAssignmentsByStaffId(UUID staffId) {
        return staffMajorFacilityRepository.findByStaffId(staffId);
    }

    @Override
    @Transactional
    public void addAssignment(UUID staffId, UUID majorId, UUID facilityId) {
        // Kiểm tra các điều kiện phân công
        validateAssignment(staffId, facilityId, majorId);
        
        Staff staff = findById(staffId);
        if (staff == null) {
            throw new RuntimeException("Không tìm thấy nhân viên");
        }
        
        // Tìm MajorFacility phù hợp cho major và facility
        MajorFacility majorFacility = majorFacilityRepository.findByMajorIdAndFacilityId(majorId, facilityId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tổ hợp chuyên ngành và cơ sở phù hợp"));
        
        StaffMajorFacility assignment = new StaffMajorFacility();
        assignment.setStaff(staff);
        assignment.setMajorFacility(majorFacility);
        assignment.setStatus((byte)1);
        assignment.setCreatedDate(System.currentTimeMillis());
        assignment.setLastModifiedDate(System.currentTimeMillis());

        staffMajorFacilityRepository.save(assignment);
    }

    @Override
    public void removeAssignment(UUID assignmentId) {
        StaffMajorFacility assignment = staffMajorFacilityRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phân công"));
        staffMajorFacilityRepository.delete(assignment);
    }

    @Override
    public void checkAssignment(UUID staffId, UUID facilityId, UUID majorId) {
        validateAssignment(staffId, facilityId, majorId);
    }

    @Override
    @Transactional
    public void addAssignmentWithDepartment(UUID staffId, UUID majorId, UUID departmentId, UUID facilityId) {
        // Kiểm tra các điều kiện phân công
        validateAssignment(staffId, facilityId, majorId);
        
        Staff staff = findById(staffId);
        if (staff == null) {
            throw new RuntimeException("Không tìm thấy nhân viên");
        }
        
        // Tìm DepartmentFacility phù hợp cho department và facility
        DepartmentFacility departmentFacility = departmentFacilityRepository
            .findByDepartmentIdAndFacilityId(departmentId, facilityId)
            .orElseGet(() -> {
                // Nếu không tìm thấy, tạo một DepartmentFacility mới
                DepartmentFacility newDf = new DepartmentFacility();
                newDf.setDepartment(departmentService.findById(departmentId));
                newDf.setFacility(facilityRepository.findById(facilityId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy cơ sở")));
                newDf.setStatus((byte)1);
                newDf.setCreatedDate(System.currentTimeMillis());
                newDf.setLastModifiedDate(System.currentTimeMillis());
                return departmentFacilityRepository.save(newDf);
            });
        
        // Tìm hoặc tạo MajorFacility cho major và departmentFacility
        MajorFacility majorFacility = majorFacilityRepository
            .findByMajorIdAndFacilityId(majorId, facilityId)
            .orElseGet(() -> {
                // Nếu không tìm thấy, tạo một MajorFacility mới
                MajorFacility newMf = new MajorFacility();
                newMf.setMajor(majorRepository.findById(majorId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên ngành")));
                newMf.setDepartmentFacility(departmentFacility);
                newMf.setStatus((byte)1);
                newMf.setCreatedDate(System.currentTimeMillis());
                newMf.setLastModifiedDate(System.currentTimeMillis());
                return majorFacilityRepository.save(newMf);
            });
        
        // Tạo và lưu StaffMajorFacility
        StaffMajorFacility assignment = new StaffMajorFacility();
        assignment.setStaff(staff);
        assignment.setMajorFacility(majorFacility);
        assignment.setStatus((byte)1);
        assignment.setCreatedDate(System.currentTimeMillis());
        assignment.setLastModifiedDate(System.currentTimeMillis());

        staffMajorFacilityRepository.save(assignment);
    }

    @Override
    public boolean hasFacilityAssignment(UUID staffId, UUID facilityId) {
        return staffMajorFacilityRepository.existsByStaffIdAndFacilityId(staffId, facilityId);
    }

    /**
     * Helper method to find a department by its name
     */
    private Department findDepartmentByName(String name) {
        // Cache departments to avoid querying database multiple times
        if (departmentCache == null) {
            departmentCache = departmentService.findAll();
        }
        
        return departmentCache.stream()
            .filter(d -> d.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Helper method to find a major by its name
     */
    private Major findMajorByName(String name) {
        // Cache majors to avoid querying database multiple times
        if (majorCache == null) {
            majorCache = majorRepository.findAll();
        }
        
        return majorCache.stream()
            .filter(m -> m.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Helper method to find a facility by its name
     */
    private Facility findFacilityByName(String name) {
        // Cache facilities to avoid querying database multiple times
        if (facilityCache == null) {
            facilityCache = facilityRepository.findAll();
        }
        
        return facilityCache.stream()
            .filter(f -> f.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Page<ImportHistory> getImportHistory(Pageable pageable) {
        return importHistoryRepository.findAll(pageable);
    }
} 