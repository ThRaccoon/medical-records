package com.medicalrecords.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"gpDoctor", "visits", "appUser"})
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String egn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gp_doctor_id", nullable = false)
    private DoctorEntity gpDoctor;

    @Column(nullable = false)
    private boolean insured;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private Set<VisitEntity> visits = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    private AppUserEntity appUser;
}
