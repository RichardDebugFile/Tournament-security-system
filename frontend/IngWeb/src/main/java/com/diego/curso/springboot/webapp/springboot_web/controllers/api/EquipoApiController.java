package com.diego.curso.springboot.webapp.springboot_web.controllers.api;

import com.diego.curso.springboot.webapp.springboot_web.dto.ConsultaEquiposPorTorneo;
import com.diego.curso.springboot.webapp.springboot_web.repositories.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoApiController {

    @Autowired
    private EquipoRepository equipoRepository;

    @GetMapping("/por-torneo/{id}")
    public List<ConsultaEquiposPorTorneo> obtenerEquiposPorTorneo(@PathVariable("id") Long torneoId) {
        return equipoRepository.obtenerEquiposPorTorneo(torneoId);
    }
}
