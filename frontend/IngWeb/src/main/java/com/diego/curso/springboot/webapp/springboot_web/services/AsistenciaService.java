package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.Asistencia;

import java.util.List;
import java.util.Optional;

public interface AsistenciaService {
    List<Asistencia> findAll();
    Optional<Asistencia> findById(Long id);
    Asistencia save(Asistencia asistencia);
    void deleteById(Long id);
}
