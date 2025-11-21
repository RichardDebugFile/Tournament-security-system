package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.Torneo;
import com.diego.curso.springboot.webapp.springboot_web.dto.ReporteTorneoDTO;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TorneoService {
    List<Torneo> findAll();
    Optional<Torneo> findById(Long id);
    Torneo save(Torneo torneo);
    void deleteById(Long id);
    List<ReporteTorneoDTO> generarReporte(LocalDate inicio, LocalDate fin);

}