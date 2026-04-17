package com.sistema.licencias.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "tramites")
public class Tramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroExpediente;

    private LocalDateTime fechaRegistro;

    private String estadoActual;

    @ManyToOne
    @JoinColumn(name = "administrado_id", nullable = false)
    private Usuario administrado;

    @ManyToOne
    @JoinColumn(name = "tipo_tramite_id", nullable = false)
    private TipoTramite tipoTramite;


    @OneToOne(mappedBy = "tramite", cascade = CascadeType.ALL)
    private Licencia licencia;
}