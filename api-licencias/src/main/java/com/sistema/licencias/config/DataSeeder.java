package com.sistema.licencias.config;

import com.sistema.licencias.entities.Rol;
import com.sistema.licencias.entities.TipoTramite;
import com.sistema.licencias.repositories.RolRepository;
import com.sistema.licencias.repositories.TipoTramiteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(RolRepository rolRepo, TipoTramiteRepository tipoRepo) {
        return args -> {

            if (rolRepo.count() == 0) {
                Rol rolAdmin = new Rol();
                rolAdmin.setNombre("ROLE_ADMINISTRADO");
                rolAdmin.setDescripcion("Usuario que solicita trámites");

                Rol rolEval = new Rol();
                rolEval.setNombre("ROLE_EVALUADOR");
                rolEval.setDescripcion("Funcionario que aprueba/rechaza");

                rolRepo.saveAll(List.of(rolAdmin, rolEval));
            }


            if (tipoRepo.count() == 0) {
                TipoTramite tipo1 = new TipoTramite();
                tipo1.setDenominacion("Licencia de Funcionamiento Definitiva");
                tipo1.setDiasResolucion(15);

                TipoTramite tipo2 = new TipoTramite();
                tipo2.setDenominacion("Licencia de Funcionamiento Temporal");
                tipo2.setDiasResolucion(7);

                TipoTramite tipo3 = new TipoTramite();
                tipo3.setDenominacion("Autorización de Anuncios Publicitarios");
                tipo3.setDiasResolucion(5);

                tipoRepo.saveAll(List.of(tipo1, tipo2, tipo3));
            }
        };
    }
}