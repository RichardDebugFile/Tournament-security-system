package com.diego.curso.springboot.webapp.springboot_web.controllers;

import com.diego.curso.springboot.webapp.springboot_web.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/estadisticas")
public class EstadisticasController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public String dashboard(Model model) {
        // Obtener datos para los gráficos
        model.addAttribute("ingresosPartido", reporteService.obtenerIngresosPorPartido());
        model.addAttribute("entradasTipo", reporteService.obtenerEntradasPorTipo());
        model.addAttribute("ingresosCategoria", reporteService.obtenerIngresosPorCategoria());

        return "estadisticas/dashboard";
    }
}
