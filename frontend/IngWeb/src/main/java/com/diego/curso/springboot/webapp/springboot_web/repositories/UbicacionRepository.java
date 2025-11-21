package com.diego.curso.springboot.webapp.springboot_web.repositories;

import com.diego.curso.springboot.webapp.springboot_web.models.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {
}
