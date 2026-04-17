package com.sistema.licencias.config;

import com.sistema.licencias.entities.Rol;
import com.sistema.licencias.entities.TipoTramite;
import com.sistema.licencias.entities.Tramite;
import com.sistema.licencias.entities.Usuario;
import com.sistema.licencias.repositories.RolRepository;
import com.sistema.licencias.repositories.TipoTramiteRepository;
import com.sistema.licencias.repositories.TramiteRepository;
import com.sistema.licencias.repositories.UsuarioRepository;
import com.sistema.licencias.services.DocumentoService;
import com.sistema.licencias.services.TramiteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleMenuRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TipoTramiteRepository tipoTramiteRepository;
    private final TramiteRepository tramiteRepository;
    private final TramiteService tramiteService;
    private final DocumentoService documentoService;

    public ConsoleMenuRunner(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                             TipoTramiteRepository tipoTramiteRepository, TramiteRepository tramiteRepository,
                             TramiteService tramiteService, DocumentoService documentoService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.tipoTramiteRepository = tipoTramiteRepository;
        this.tramiteRepository = tramiteRepository;
        this.tramiteService = tramiteService;
        this.documentoService = documentoService;
    }

    @Override
    public void run(String... args) {

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            boolean salir = false;

            try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

            while (!salir) {
                System.out.println("\n========================================");
                System.out.println("    SIMULADOR DE SISTEMA DE LICENCIAS   ");
                System.out.println("========================================");
                System.out.println("1. Registrar nuevo Usuario");
                System.out.println("2. Registrar nuevo Trámite (Simular Ciudadano)");
                System.out.println("3. Evaluar Trámite (Simular Evaluador)");
                System.out.println("4. Ver todos los Trámites");
                System.out.println("5. Generar y Validar PDF con QR (CUS06/07)");
                System.out.println("6. Salir del Menú");
                System.out.print("Elige una opción: ");

                String opcion = scanner.nextLine();

                try {
                    switch (opcion) {
                        case "1": crearUsuario(scanner); break;
                        case "2": crearTramite(scanner); break;
                        case "3": evaluarTramite(scanner); break;
                        case "4": listarTramites(); break;
                        case "5": simularDescargaPDF(scanner); break;
                        case "6":
                            salir = true;
                            System.out.println("Saliendo del simulador y apagando el servidor...");
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Opción no válida. Intenta de nuevo.");
                    }
                } catch (Exception e) {
                    System.out.println("Ocurrió un error en la operación: " + e.getMessage());
                }
            }
        }).start();
    }

    private void crearUsuario(Scanner scanner) {
        System.out.println("\n--- REGISTRO DE USUARIO ---");
        System.out.print("Ingresa el Username (ej. juan.perez): ");
        String username = scanner.nextLine();

        System.out.print("Ingresa la Contraseña: ");
        String password = scanner.nextLine();

        System.out.print("Ingresa el Nombre Completo: ");
        String nombre = scanner.nextLine();

        System.out.println("Selecciona el Rol:");
        System.out.println("1. Administrado (Ciudadano)");
        System.out.println("2. Evaluador (Funcionario)");
        System.out.print("Opción: ");
        String rolOpt = scanner.nextLine();

        Rol rol = new Rol();
        rol.setId(rolOpt.equals("2") ? 2L : 1L);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setNombreCompleto(nombre);
        nuevoUsuario.setRol(rol);

        usuarioRepository.save(nuevoUsuario);
        System.out.println("¡Usuario registrado con éxito!");
    }

    private void crearTramite(Scanner scanner) {
        System.out.println("\n--- NUEVO TRÁMITE ---");


        System.out.println("Usuarios registrados actualmente:");
        usuarioRepository.findAll().forEach(u ->
                System.out.println("ID: " + u.getId() + " | Username: " + u.getUsername())
        );
        System.out.print("Ingresa el ID del Usuario que solicita el trámite: ");
        Long userId = Long.parseLong(scanner.nextLine());


        System.out.println("\nSelecciona el Tipo de Trámite (Ingresa el ID numérico):");
        List<TipoTramite> tipos = tipoTramiteRepository.findAll();
        for (TipoTramite tipo : tipos) {
            System.out.println("ID: " + tipo.getId() + " - " + tipo.getDenominacion());
        }
        System.out.print("Opción: ");
        Long tipoId = Long.parseLong(scanner.nextLine());

        Usuario usuario = new Usuario();
        usuario.setId(userId);

        TipoTramite tipo = new TipoTramite();
        tipo.setId(tipoId);

        Tramite nuevoTramite = new Tramite();
        nuevoTramite.setAdministrado(usuario);
        nuevoTramite.setTipoTramite(tipo);

        Tramite registrado = tramiteService.registrarTramite(nuevoTramite);
        System.out.println("¡Trámite registrado exitosamente con el Expediente: " + registrado.getNumeroExpediente() + "!");
    }

    private void evaluarTramite(Scanner scanner) {
        System.out.println("\n--- EVALUAR TRÁMITE ---");


        List<Tramite> pendientes = tramiteRepository.findAll().stream()
                .filter(t -> t.getEstadoActual().equals("PENDIENTE"))
                .toList();

        if (pendientes.isEmpty()) {
            System.out.println("¡Excelente! No hay trámites pendientes por evaluar en este momento.");
            return;
        }

        System.out.println("Expedientes en cola de revisión:");
        for (Tramite t : pendientes) {
            System.out.printf("ID: %d | Expediente: %s | Solicitante: %s%n",
                    t.getId(), t.getNumeroExpediente(), t.getAdministrado().getNombreCompleto());
        }

        System.out.print("\nIngresa el ID del Trámite que deseas evaluar: ");
        Long tramiteId = Long.parseLong(scanner.nextLine());

        System.out.println("¿Qué decisión tomarás?");
        System.out.println("1. APROBAR");
        System.out.println("2. RECHAZAR");
        System.out.print("Opción: ");
        String decision = scanner.nextLine().equals("1") ? "APROBAR" : "RECHAZAR";

        try {
            Tramite evaluado = tramiteService.evaluarTramite(tramiteId, decision);
            System.out.println("El trámite ahora tiene estado: " + evaluado.getEstadoActual());

            if (decision.equals("APROBAR")) {
                System.out.println("Generando licencia física automáticamente...");
                tramiteService.emitirLicencia(tramiteId);
                System.out.println("¡Licencia emitida con éxito! Estado final: EMITIDO");
            }
        } catch (Exception e) {
            System.out.println("Error en la evaluación: " + e.getMessage());
        }
    }

    private void listarTramites() {
        System.out.println("\n--- BANDEJA DE TRÁMITES ---");
        List<Tramite> tramites = tramiteRepository.findAll();
        if (tramites.isEmpty()) {
            System.out.println("No hay trámites registrados aún.");
            return;
        }
        for (Tramite t : tramites) {
            System.out.printf("ID: %d | Exp: %s | Ciudadano: %s | Estado: %s%n",
                    t.getId(), t.getNumeroExpediente(), t.getAdministrado().getNombreCompleto(), t.getEstadoActual());
        }
    }


    private void simularDescargaPDF(Scanner scanner) {
        System.out.println("\n--- GENERADOR DE LICENCIA FÍSICA ---");


        System.out.println("Licencias disponibles para descargar:");
        List<Tramite> tramitesEmitidos = tramiteRepository.findAll().stream()
                .filter(t -> t.getEstadoActual().equals("EMITIDO") && t.getLicencia() != null)
                .toList();

        if (tramitesEmitidos.isEmpty()) {
            System.out.println("No hay trámites en estado EMITIDO con licencia generada.");
            return;
        }

        for (Tramite t : tramitesEmitidos) {
            System.out.printf("ID Trámite: %d | N° Licencia: %s | Administrado: %s%n",
                    t.getId(), t.getLicencia().getNumeroLicencia(), t.getAdministrado().getNombreCompleto());
        }

        System.out.print("\nIngresa el ID del Trámite para generar su PDF: ");
        Long tramiteId = Long.parseLong(scanner.nextLine());


        Tramite tramite = tramiteRepository.findById(tramiteId)
                .orElseThrow(() -> new RuntimeException("Trámite no encontrado"));

        if (tramite.getLicencia() == null) {
            System.out.println("Este trámite no tiene una licencia asociada.");
            return;
        }

        System.out.println("Generando documento con Códigos QR usando OpenPDF y ZXing...");
        try {
            byte[] pdfBytes = documentoService.generarLicenciaPdf(tramite.getLicencia());


            String nombreArchivo = "Licencia_" + tramite.getLicencia().getNumeroLicencia() + ".pdf";
            java.io.File archivoFisico = new java.io.File(nombreArchivo);


            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(archivoFisico)) {
                fos.write(pdfBytes);
            }
            System.out.println("¡ÉXITO! Documento PDF generado y guardado (" + (pdfBytes.length / 1024) + " KB).");


            System.out.println("Abriendo el documento en tu visor de PDFs predeterminado...");
            try {
                if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
                    java.awt.Desktop.getDesktop().open(archivoFisico);
                } else {

                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "", archivoFisico.getAbsolutePath()});
                    } else {
                        System.out.println("Búscalo manualmente en la carpeta del proyecto.");
                    }
                }
            } catch (Exception ex) {
                System.out.println("No se pudo abrir automáticamente: " + ex.getMessage());
            }

            System.out.println("El QR interno apunta a: http://localhost:8080/api/validar/" + tramite.getLicencia().getNumeroLicencia());

        } catch (Exception e) {
            System.out.println("Error al generar o abrir el PDF: " + e.getMessage());
        }
    }
}