package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.Partido;

import java.util.List;
import java.util.Optional;

public interface PartidoService {
    List<Partido> findAll();
    Optional<Partido> findById(Long id);
    Partido save(Partido partido);
    void deleteById(Long id);
}
