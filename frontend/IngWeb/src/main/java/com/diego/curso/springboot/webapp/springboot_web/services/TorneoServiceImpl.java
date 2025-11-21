package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.EstadoTorneo;
import com.diego.curso.springboot.webapp.springboot_web.models.Torneo;
import com.diego.curso.springboot.webapp.springboot_web.repositories.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.diego.curso.springboot.webapp.springboot_web.dto.ReporteTorneoDTO;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TorneoServiceImpl implements TorneoService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Override
    public List<Torneo> findAll() {
        return torneoRepository.findAll();
    }

    @Override
    public Optional<Torneo> findById(Long id) {
        return torneoRepository.findById(id);
    }

    @Override
    public Torneo save(Torneo torneo) {
        return torneoRepository.save(torneo);
    }

    @Override
    public void deleteById(Long id) {
        torneoRepository.deleteById(id);
    }
    @Override
public List<ReporteTorneoDTO> generarReporte(LocalDate inicio, LocalDate fin) {
    List<Torneo> torneos = torneoRepository.findByFechaInicioBetween(inicio, fin);

    return torneos.stream().map(t -> {
        long diasRetrasados = 0;
        if (t.getEstado() == EstadoTorneo.EN_PROGRESO && t.getFechaFinal().isBefore(LocalDate.now())) {
            diasRetrasados = java.time.temporal.ChronoUnit.DAYS.between(t.getFechaFinal(), LocalDate.now());
        }

        return new ReporteTorneoDTO(
            t.getNombre(),
            t.getFechaInicio(),
            t.getFechaFinal(),
            t.getEstado().toString(),
            diasRetrasados
        );
    }).toList();
}
    
}
