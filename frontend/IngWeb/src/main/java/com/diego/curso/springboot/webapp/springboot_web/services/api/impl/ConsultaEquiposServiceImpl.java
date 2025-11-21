package com.diego.curso.springboot.webapp.springboot_web.services.api.impl;

import com.diego.curso.springboot.webapp.springboot_web.dto.ConsultaEquiposPorTorneo;
import com.diego.curso.springboot.webapp.springboot_web.models.Equipo;
import com.diego.curso.springboot.webapp.springboot_web.repositories.EquipoRepository;
import com.diego.curso.springboot.webapp.springboot_web.services.api.ConsultaEquiposService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaEquiposServiceImpl implements ConsultaEquiposService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    public List<ConsultaEquiposPorTorneo> obtenerEquiposPorTorneo(Long torneoId) {
        List<Equipo> equipos = equipoRepository.findByTorneoId(torneoId);
        return equipos.stream().map(equipo -> {
            ConsultaEquiposPorTorneo dto = new ConsultaEquiposPorTorneo();
            dto.setId(equipo.getId());
            dto.setNombre(equipo.getNombre());
            dto.setNumeroJugadores(equipo.getNumeroJugadores());
            dto.setColorUniforme(equipo.getColorUniforme());
            return dto;
        }).collect(Collectors.toList());
    }
}
