package com.help.tap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "allergies", schema = "\"hpTap\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allergies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ilnessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(nullable = false)
    private String allergenic;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RiskRating riskRating;
}
