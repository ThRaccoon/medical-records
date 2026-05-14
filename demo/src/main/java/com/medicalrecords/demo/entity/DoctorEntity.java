package com.medicalrecords.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"patients", "visits", "appUser"})
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uin;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String speciality;

    @Column(nullable = false)
    private boolean gp;

    @OneToMany(mappedBy = "gpDoctor", fetch = FetchType.LAZY)
    private Set<PatientEntity> patients = new HashSet<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private Set<VisitEntity> visits = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    private AppUserEntity appUser;
}
