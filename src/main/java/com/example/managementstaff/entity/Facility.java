package com.example.managementstaff.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "facility")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    private String name;

    private String code;

    @Column(name = "status")
    private Byte status = 1; // 1: active, 0: inactive

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @OneToMany(mappedBy = "facility")
    private Set<DepartmentFacility> departmentFacilities;
} 