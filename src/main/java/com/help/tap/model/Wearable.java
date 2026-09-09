package com.help.tap.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "wearable", schema = "\"hpTap\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wearable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private String wearableName;

    @Column(name = "access_url", nullable = false, columnDefinition = "uuid")
    private UUID accessUrl;

    @Column(name = "data_vinculacao")
    private LocalDate bindingDate;

    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;
}
