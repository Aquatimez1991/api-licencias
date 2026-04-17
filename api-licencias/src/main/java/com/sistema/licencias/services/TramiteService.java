package com.sistema.licencias.services;

import com.sistema.licencias.entities.Licencia;
import com.sistema.licencias.entities.Tramite;
import com.sistema.licencias.repositories.LicenciaRepository;
import com.sistema.licencias.repositories.TramiteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TramiteService {

    private final TramiteRepository tramiteRepository;
    private final LicenciaRepository licenciaRepository;

    public TramiteService(TramiteRepository tramiteRepository, LicenciaRepository licenciaRepository) {
        this.tramiteRepository = tramiteRepository;
        this.licenciaRepository = licenciaRepository;
    }

    public Tramite registrarTramite(Tramite tramite) {
        tramite.setFechaRegistro(LocalDateTime.now());
        tramite.setEstadoActual("PENDIENTE");
        String codigoUnico = UUID.randomUUID().toString().substring(0, 7).toUpperCase();
        tramite.setNumeroExpediente("EXP-" + codigoUnico);
        return tramiteRepository.save(tramite);
    }

    public List<Tramite> listarTodos() {
        return tramiteRepository.findAll();
    }

    public Tramite evaluarTramite(Long id, String decision) {
        Tramite tramite = tramiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trámite no encontrado"));

        if (!tramite.getEstadoActual().equals("PENDIENTE")) {
            throw new RuntimeException("El trámite ya fue evaluado anteriormente. Estado actual: " + tramite.getEstadoActual());
        }

        if (decision.equalsIgnoreCase("APROBAR")) {
            tramite.setEstadoActual("APROBADO");
        } else if (decision.equalsIgnoreCase("RECHAZAR")) {
            tramite.setEstadoActual("RECHAZADO");
        }

        return tramiteRepository.save(tramite);
    }

    public Licencia emitirLicencia(Long tramiteId) {
        Tramite tramite = tramiteRepository.findById(tramiteId)
                .orElseThrow(() -> new RuntimeException("Trámite no encontrado"));

        if (!"APROBADO".equals(tramite.getEstadoActual())) {
            throw new RuntimeException("El trámite debe estar APROBADO para emitir la licencia");
        }

        Licencia licencia = new Licencia();

        licencia.setNumeroLicencia("LIC-" + LocalDate.now().getYear() + "-" + (int)(Math.random() * 100000));
        licencia.setFechaEmision(LocalDate.now());
        licencia.setFechaVencimiento(LocalDate.now().plusYears(1));

        licencia.setCodigoQrUrl("https://tusistema.com/validar/" + licencia.getNumeroLicencia());
        licencia.setTramite(tramite);


        tramite.setEstadoActual("EMITIDO");
        tramiteRepository.save(tramite);

        return licenciaRepository.save(licencia);
    }
}