package com.diego.curso.springboot.webapp.springboot_web.controllers;

import com.diego.curso.springboot.webapp.springboot_web.dto.*;
import com.diego.curso.springboot.webapp.springboot_web.services.GeneradorRecomendacionesService;
import com.diego.curso.springboot.webapp.springboot_web.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping({"", "/"})
    public String vistaGeneral() {
        return "reportes/index"; // Vista principal del módulo de reportes
    }

    @GetMapping("/ingresos-partido")
    public String ingresosPorPartido(Model model) {
        List<IngresosPorPartidoDTO> lista = reporteService.obtenerIngresosPorPartido();
        model.addAttribute("datos", lista);
        return "reportes/ingresos_partido";
    }

    @GetMapping("/ingresos-equipo")
    public String ingresosPorEquipo(Model model) {
        List<IngresosPorEquipoDTO> lista = reporteService.obtenerIngresosPorEquipo();
        model.addAttribute("datos", lista);
        return "reportes/ingresos_equipo";
    }

    @GetMapping("/ingresos-ubicacion")
    public String ingresosPorUbicacion(Model model) {
        List<IngresosPorUbicacionDTO> lista = reporteService.obtenerIngresosPorUbicacion();
        model.addAttribute("datos", lista);
        return "reportes/ingresos_ubicacion";
    }

    @GetMapping("/ingresos-fecha")
    public String ingresosPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model
    ) {
        List<IngresosPorFechaDTO> lista = reporteService.obtenerIngresosPorFecha(desde, hasta);
        model.addAttribute("datos", lista);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "reportes/ingresos_fecha";
    }

    @GetMapping("/entradas-tipo")
    public String entradasPorTipo(Model model) {
        List<EntradasVendidasPorTipoDTO> lista = reporteService.obtenerEntradasPorTipo();
        model.addAttribute("datos", lista);
        return "reportes/entradas_tipo";
    }

    @GetMapping("/comparacion")
    public String comparacion(Model model) {
        List<ComparacionEquipoTipoEntradaUbicacionDTO> lista = reporteService.obtenerComparacionEquipoTipoEntradaUbicacion();
        model.addAttribute("datos", lista);
        return "reportes/comparacion";
    }

    @GetMapping("/ranking")
    public String ranking(Model model) {
        List<RankingPartidoExitosoDTO> lista = reporteService.obtenerRankingPartidos();
        model.addAttribute("datos", lista);
        return "reportes/ranking";
    }

    @GetMapping("/recomendaciones")
    public String recomendaciones(Model model) {
        List<RecomendacionCombinacionExitosaDTO> lista = reporteService.obtenerRecomendaciones();
        model.addAttribute("datos", lista);
        return "reportes/recomendaciones";
    }

    @GetMapping("/ingresos-categoria")
    public String ingresosPorCategoria(Model model) {
        List<IngresosPorCategoriaDTO> lista = reporteService.obtenerIngresosPorCategoria();
        model.addAttribute("datos", lista);
        return "reportes/ingresos_categoria";
    }

    @Autowired
private GeneradorRecomendacionesService generadorRecomendacionesService;

@GetMapping("/reporte-recomendaciones")
public String vistaRecomendacionesFinales(Model model) {
    String reporte = generadorRecomendacionesService.generarRecomendacionesFinales();
    model.addAttribute("reporte", reporte);
    return "reportes/reporte_recomendaciones";
}

@GetMapping("/rentabilidad")
public String rentabilidadTorneos(Model model) {
    List<RentabilidadTorneoDTO> lista = reporteService.calcularRentabilidadPorTorneo();
    model.addAttribute("datos", lista);
    return "reportes/rentabilidad";
}

@GetMapping("/ingresos-torneo-equipo")
public String verIngresosPorTorneoYEquipo(Model model) {
    List<ResumenIngresosPorTorneoYEquipoDTO> datos = reporteService.obtenerResumenMayorYMenorPorTorneo();
    model.addAttribute("resumenes", datos);
    return "reportes/ingresos_torneo_equipo";
}


@Autowired
private com.diego.curso.springboot.webapp.springboot_web.services.TorneoService torneoService;

@GetMapping("/equipos-por-torneo")
public String vistaEquiposPorTorneo(Model model) {
    model.addAttribute("torneos", torneoService.findAll());
    return "reportes/apiVista/equipos_por_torneo";
}








}
