package com.example.managementstaff.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
@Table(name = "staff_major_facility")
public class StaffMajorFacility {
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
    @JoinColumn(name = "id_staff")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "id_major_facility")
    private MajorFacility majorFacility;
} 