package com.sistema.licencias.repositories;

import com.sistema.licencias.entities.Licencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenciaRepository extends JpaRepository<Licencia, Long> {
}