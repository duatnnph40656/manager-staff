package com.example.managementstaff.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "staff_code")
    private String staffCode;

    private String name;

    @Column(name = "account_fpt")
    private String accountFpt;

    @Column(name = "account_fe")
    private String accountFe;

    @Column(name = "status")
    private Byte status = 1; // 1: active, 0: inactive
    
    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @OneToMany(mappedBy = "staff")
    private Set<DepartmentFacility> departmentFacilities;

    @OneToMany(mappedBy = "staff")
    private Set<StaffMajorFacility> staffMajorFacilities;
} 