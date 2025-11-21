package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.Equipo;
import java.util.List;
import java.util.Optional;

public interface EquipoService {
    List<Equipo> findAll();
    Optional<Equipo> findById(Long id);
    Equipo save(Equipo equipo);
    void deleteById(Long id);
    List<Equipo> findByTorneoId(Long torneoId);

}
