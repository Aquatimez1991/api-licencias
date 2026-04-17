package com.sistema.licencias.repositories;

import com.sistema.licencias.entities.TipoTramite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoTramiteRepository extends JpaRepository<TipoTramite, Long> {
}