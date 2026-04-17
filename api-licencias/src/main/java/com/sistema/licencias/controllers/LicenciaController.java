package com.sistema.licencias.controllers;

import com.sistema.licencias.entities.Licencia;
import com.sistema.licencias.repositories.LicenciaRepository;
import com.sistema.licencias.services.DocumentoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class LicenciaController {

    private final LicenciaRepository licenciaRepository;
    private final DocumentoService documentoService;

    public LicenciaController(LicenciaRepository licenciaRepository, DocumentoService documentoService) {
        this.licenciaRepository = licenciaRepository;
        this.documentoService = documentoService;
    }


    @GetMapping("/licencias/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Licencia licencia = licenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Licencia no encontrada"));

        byte[] pdfBytes = documentoService.generarLicenciaPdf(licencia);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Licencia_" + licencia.getNumeroLicencia() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


    @GetMapping("/validar/{numeroLicencia}")
    public ResponseEntity<?> validarLicencia(@PathVariable String numeroLicencia) {

        Optional<Licencia> licenciaOpt = licenciaRepository.findAll().stream()
                .filter(l -> l.getNumeroLicencia().equals(numeroLicencia))
                .findFirst();

        if (licenciaOpt.isPresent()) {
            Licencia lic = licenciaOpt.get();
            return ResponseEntity.ok("VÁLIDO - Licencia activa perteneciente a: " +
                    lic.getTramite().getAdministrado().getNombreCompleto() +
                    ". Vence el: " + lic.getFechaVencimiento());
        } else {
            return ResponseEntity.status(404).body("INVÁLIDO - Documento no existe o fue revocado.");
        }
    }
}