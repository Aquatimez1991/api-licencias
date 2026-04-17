package com.sistema.licencias.repositories;

import com.sistema.licencias.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
}