package com.help.tap.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Data
@Entity
@Table(name = "users", schema = "\"hpTap\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "national_registration", nullable = false, length = 11)
    private String nationalRegistration;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 10)
    private String sex;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "identifier", unique = true)
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "user_picture", columnDefinition = "TEXT")
    private String userPicture;

    @Column(name = "professional_id")
    private String professionalId;

    // 1. Correção em tal problema (Adição de campos para telefone, LGPD e exclusão lógica)
    @Column(name = "phone")
    private String phone;

    @Column(name = "privacy_policy_accepted")
    @Builder.Default
    private Boolean privacyPolicyAccepted = false;

    @Column(name = "terms_of_use_accepted")
    @Builder.Default
    private Boolean termsOfUseAccepted = false;

    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "legal_guardian_name")
    private String legalGuardianName;

    @Column(name = "legal_guardian_cpf", length = 11)
    private String legalGuardianCpf;

    @Column(name = "legal_guardian_consent")
    @Builder.Default
    private Boolean legalGuardianConsent = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EmergencyContact> emergencyContacts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Wearable> wearables = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Illness> illnesses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Deficiency> deficiencies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AccessLog> accessLogs = new ArrayList<>();

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    //Metodos User Datails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        switch (this.role) {
            case ADMIN:
                return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_PATIENT"));
            case PATIENT:
                return List.of(new SimpleGrantedAuthority("ROLE_PATIENT"));
            case DOCTOR:
                return List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"));
            case POLICE:
                return List.of(new SimpleGrantedAuthority("ROLE_POLICE"));
            case FIREFIGHTER:
                return List.of(new SimpleGrantedAuthority("ROLE_FIREFIGHTER"));
            case RESCUER:
                return List.of(new SimpleGrantedAuthority("ROLE_RESCUER"));
            default:
                return List.of(null);
        }
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;//UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;//UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;//UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;//UserDetails.super.isEnabled();
    }
}
