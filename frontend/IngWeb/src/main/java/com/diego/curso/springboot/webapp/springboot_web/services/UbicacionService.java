package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.Ubicacion;

import java.util.List;
import java.util.Optional;

public interface UbicacionService {
    List<Ubicacion> findAll();
    Optional<Ubicacion> findById(Long id);
    Ubicacion save(Ubicacion ubicacion);
    void deleteById(Long id);
}
