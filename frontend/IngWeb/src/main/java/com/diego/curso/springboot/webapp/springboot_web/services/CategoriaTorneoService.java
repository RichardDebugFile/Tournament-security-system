package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.CategoriaTorneo;

import java.util.List;
import java.util.Optional;

public interface CategoriaTorneoService {
    List<CategoriaTorneo> findAll();
    Optional<CategoriaTorneo> findById(Long id);
}
