package com.sistema.licencias.controllers;

import com.sistema.licencias.entities.Licencia;
import com.sistema.licencias.entities.Tramite;
import com.sistema.licencias.services.TramiteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/tramites")
public class TramiteController {

    private final TramiteService tramiteService;

    public TramiteController(TramiteService tramiteService) {
        this.tramiteService = tramiteService;
    }

    @PostMapping
    public Tramite registrar(@RequestBody Tramite tramite) {
        return tramiteService.registrarTramite(tramite);
    }

    @GetMapping
    public List<Tramite> listar() {
        return tramiteService.listarTodos();
    }


    @PutMapping("/{id}/evaluar")
    public Tramite evaluar(@PathVariable Long id, @RequestParam String decision) {
        return tramiteService.evaluarTramite(id, decision);
    }


    @PostMapping("/{id}/emitir")
    public Licencia emitir(@PathVariable Long id) {
        return tramiteService.emitirLicencia(id);
    }
}