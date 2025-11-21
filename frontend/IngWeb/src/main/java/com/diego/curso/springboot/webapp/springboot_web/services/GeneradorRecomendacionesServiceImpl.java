package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class GeneradorRecomendacionesServiceImpl implements GeneradorRecomendacionesService {

    @Autowired
    private ReporteService reporteService;

    @Override
    public String generarRecomendacionesFinales() {
        StringBuilder sb = new StringBuilder();

        // Fecha actual
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        sb.append("=== RECOMENDACIONES FINALES ===\n");
        sb.append("📅 Fecha del análisis: ").append(fechaActual).append("\n\n");

        // 1. Equipo con más ingresos
        List<IngresosPorEquipoDTO> ingresosEquipo = reporteService.obtenerIngresosPorEquipo();
        ingresosEquipo.stream()
                .max(Comparator.comparingDouble(IngresosPorEquipoDTO::getIngresoTotal))
                .ifPresent(e -> sb.append("✓ El equipo que más ingresos generó fue: ")
                        .append(e.getNombreEquipo())
                        .append(" con $").append(String.format("%.2f", e.getIngresoTotal())).append("\n\n"));

        // 2. Ubicación con más ingresos
        List<IngresosPorUbicacionDTO> ingresosUbicacion = reporteService.obtenerIngresosPorUbicacion();
        ingresosUbicacion.stream()
                .max(Comparator.comparingDouble(IngresosPorUbicacionDTO::getIngresoTotal))
                .ifPresent(u -> sb.append("✓ La ubicación más rentable fue: ")
                        .append(u.getNombreUbicacion())
                        .append(" con $").append(String.format("%.2f", u.getIngresoTotal())).append("\n\n"));

        // 3. Tipo de entrada más popular
        List<EntradasVendidasPorTipoDTO> entradas = reporteService.obtenerEntradasPorTipo();
        entradas.stream()
                .max(Comparator.comparingLong(EntradasVendidasPorTipoDTO::getCantidadVendida))
                .ifPresent(t -> sb.append("✓ El tipo de entrada más popular fue: ")
                        .append(t.getTipoEntrada())
                        .append(" con ").append(t.getCantidadVendida()).append(" entradas vendidas\n\n"));

        // 4. Partido más exitoso
        List<RankingPartidoExitosoDTO> ranking = reporteService.obtenerRankingPartidos();
        if (!ranking.isEmpty()) {
            RankingPartidoExitosoDTO top = ranking.get(0);
            sb.append("✓ El partido más exitoso fue: ")
              .append(top.getDescripcionPartido())
              .append(" el ").append(top.getFecha())
              .append(" en ").append(top.getUbicacion())
              .append(" con $").append(String.format("%.2f", top.getIngresoTotal()))
              .append(" de ingresos\n\n");
        }

        // 5. Recomendación final
        sb.append("🎯 Recomendación final: Enfocar futuros eventos en el equipo, ubicación y tipo de entrada que generaron más ingresos.\n");

        return sb.toString();
    }
}
