package com.example.managementstaff.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "department_facility")
public class DepartmentFacility {
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
    @JoinColumn(name = "id_department")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "id_facility")
    private Facility facility;
    
    @ManyToOne
    @JoinColumn(name = "id_staff")
    private Staff staff;
    
    @OneToMany(mappedBy = "departmentFacility")
    private Set<MajorFacility> majorFacilities;
} 