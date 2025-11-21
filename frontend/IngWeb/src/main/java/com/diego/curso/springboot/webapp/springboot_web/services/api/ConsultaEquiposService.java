package com.diego.curso.springboot.webapp.springboot_web.services.api;

import com.diego.curso.springboot.webapp.springboot_web.dto.ConsultaEquiposPorTorneo;
import java.util.List;

public interface ConsultaEquiposService {
    List<ConsultaEquiposPorTorneo> obtenerEquiposPorTorneo(Long torneoId);
}
