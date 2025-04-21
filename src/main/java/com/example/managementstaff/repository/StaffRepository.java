package com.example.managementstaff.repository;

import com.example.managementstaff.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface StaffRepository extends JpaRepository<Staff, UUID> {
    boolean existsByStaffCode(String staffCode);
    boolean existsByAccountFpt(String accountFpt);
    boolean existsByAccountFe(String accountFe);
    
    boolean existsByStaffCodeAndIdNot(String staffCode, UUID id);
    boolean existsByAccountFptAndIdNot(String accountFpt, UUID id);
    boolean existsByAccountFeAndIdNot(String accountFe, UUID id);

    @Query("SELECT s FROM Staff s WHERE " +
           "(:keyword IS NULL OR " +
           "LOWER(s.staffCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.accountFpt) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.accountFe) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR s.status = :status)")
    Page<Staff> findByKeywordAndStatus(@Param("keyword") String keyword, 
                                     @Param("status") Integer status, 
                                     Pageable pageable);
} 