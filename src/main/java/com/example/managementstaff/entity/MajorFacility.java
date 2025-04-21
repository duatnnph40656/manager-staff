package com.example.managementstaff.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "major_facility")
public class MajorFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "status")
    private Byte status = 1; // 1: active, 0: inactive
    
    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @ManyToOne
    @JoinColumn(name = "id_major")
    private Major major;

    @ManyToOne
    @JoinColumn(name = "id_department_facility")
    private DepartmentFacility departmentFacility;

    @OneToMany(mappedBy = "majorFacility")
    private Set<StaffMajorFacility> staffMajorFacilities;
} 