package com.example.managementstaff.controller;

import com.example.managementstaff.entity.Staff;
import com.example.managementstaff.entity.StaffMajorFacility;
import com.example.managementstaff.entity.ImportHistory;
import com.example.managementstaff.service.StaffService;
import com.example.managementstaff.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;
    private final FacilityService facilityService;

    // Exception handler cho controller
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
        return "redirect:/staff/list";
    }

    @GetMapping(value = {"/", "/staff"})
    public String redirectToStaffList() {
        return "redirect:/staff/list";
    }

    @GetMapping("/staff/list")
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        
        Page<Staff> staffPage = staffService.findByKeywordAndStatus(
            keyword, status, PageRequest.of(page, size));

        model.addAttribute("staffList", staffPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("totalItems", staffPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("hasNext", staffPage.hasNext());
        model.addAttribute("hasPrevious", staffPage.hasPrevious());
        model.addAttribute("firstPage", page == 0);
        model.addAttribute("lastPage", page == staffPage.getTotalPages() - 1);

        return "staff/list";
    }

    @GetMapping("/staff/create")
    public String createForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "staff/create";
    }

    @PostMapping("/staff/create")
    public String create(@Valid @ModelAttribute Staff staff,
                        BindingResult result, 
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "staff/create";
        }

        try {
            staffService.save(staff);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên thành công");
            return "redirect:/staff/list";
        } catch (Exception e) {
            result.rejectValue("staffCode", "error", e.getMessage());
            return "staff/create";
        }
    }

    @GetMapping("/staff/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        Staff staff = staffService.findById(id);
        if (staff == null) {
            throw new RuntimeException("Không tìm thấy nhân viên với ID: " + id);
        }
        model.addAttribute("staff", staff);
        return "staff/create";
    }

    @GetMapping("/staff/view/{id}")
    public String viewStaff(@PathVariable UUID id, Model model) {
        Staff staff = staffService.findById(id);
        if (staff == null) {
            throw new RuntimeException("Không tìm thấy nhân viên với ID: " + id);
        }
        
        List<StaffMajorFacility> assignments = staffService.findAssignmentsByStaffId(id);
        
        model.addAttribute("staff", staff);
        model.addAttribute("assignments", assignments);
        model.addAttribute("facilities", facilityService.findAll());
        return "staff/form";
    }

    @PostMapping("/staff/edit/{id}")
    public String edit(@PathVariable UUID id, 
                      @Valid @ModelAttribute Staff staff, 
                      BindingResult result, 
                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "staff/create";
        }

        try {
            staff.setId(id);
            staffService.update(staff);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên thành công");
            return "redirect:/staff/list";
        } catch (Exception e) {
            result.rejectValue("staffCode", "error", e.getMessage());
            return "staff/create";
        }
    }

    @PostMapping("/staff/toggle-status/{id}")
    @ResponseBody
    public ResponseEntity<String> toggleStatus(@PathVariable UUID id) {
        try {
            Staff staff = staffService.findById(id);
            if (staff == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy nhân viên");
            }
            
            boolean wasActive = staff.getStatus() == 1;
            staffService.toggleStatus(id);
            
            String message = "Đã " + (wasActive ? "vô hiệu hóa" : "kích hoạt") + " nhân viên " + staff.getName() + " thành công";
            System.out.println("Toggle status: " + message);
            
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            System.err.println("Error toggling status: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/staff/{staffId}/add-assignment")
    public String addAssignment(@PathVariable UUID staffId,
                              @RequestParam UUID majorId,
                              @RequestParam UUID departmentId,
                              @RequestParam UUID facilityId,
                              RedirectAttributes redirectAttributes) {
        try {
            staffService.addAssignmentWithDepartment(staffId, majorId, departmentId, facilityId);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm phân công thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/staff/view/" + staffId;
    }

    @PostMapping("/staff/remove-assignment")
    public String removeAssignment(@RequestParam UUID id,
                                 @RequestParam UUID staffId,
                                 RedirectAttributes redirectAttributes) {
        try {
            staffService.removeAssignment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa phân công thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/staff/view/" + staffId;
    }

    @GetMapping("/staff/download-template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] template = staffService.downloadTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=staff_import_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(template);
    }

    @PostMapping("/staff/import")
    public String importStaff(@RequestParam("file") MultipartFile file,
                            RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn file để import");
            return "redirect:/staff/list";
        }
        
        try {
            ImportHistory history = staffService.importStaff(file, "admin"); 
            String successMessage = "Import nhân viên thành công. Tổng cộng: " + history.getTotalRecords() + ", Thành công: " + history.getSuccessRecords() + ", Thất bại: " + history.getFailedRecords();
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            if (history.getFailedRecords() > 0) {
                System.out.println("Nguyên nhân thất bại:");
                System.out.println(history.getErrors());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi import: " + e.getMessage());
        }
        return "redirect:/staff/list";
    }

    @GetMapping("/staff/import-history")
    public String getImportHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<ImportHistory> historyPage = staffService.getImportHistory(PageRequest.of(page, size));
        model.addAttribute("historyList", historyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", historyPage.getTotalPages());
        model.addAttribute("totalItems", historyPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("hasNext", historyPage.hasNext());
        model.addAttribute("hasPrevious", historyPage.hasPrevious());
        return "staff/import-history";
    }

    @GetMapping("/api/staff/import-history")
    @ResponseBody
    public Page<ImportHistory> getImportHistoryApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return staffService.getImportHistory(PageRequest.of(page, size));
    }
} 