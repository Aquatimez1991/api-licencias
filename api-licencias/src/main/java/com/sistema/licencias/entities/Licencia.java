package com.sistema.licencias.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "licencias")
public class Licencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroLicencia;

    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;

    @Column(columnDefinition = "TEXT")
    private String codigoQrUrl;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "tramite_id", nullable = false)
    private Tramite tramite;
}