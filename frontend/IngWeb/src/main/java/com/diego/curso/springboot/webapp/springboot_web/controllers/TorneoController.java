package com.diego.curso.springboot.webapp.springboot_web.controllers;

import com.diego.curso.springboot.webapp.springboot_web.models.Torneo;
import com.diego.curso.springboot.webapp.springboot_web.services.TorneoService;
import com.diego.curso.springboot.webapp.springboot_web.services.CategoriaTorneoService;
import com.diego.curso.springboot.webapp.springboot_web.dto.ReporteTorneoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/torneos")
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @Autowired
    private CategoriaTorneoService categoriaTorneoService;

    @GetMapping
    public String listarTorneos(Model model) {
        model.addAttribute("torneos", torneoService.findAll());
        return "torneos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("torneo", new Torneo());
        model.addAttribute("categorias", categoriaTorneoService.findAll());
        return "torneos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarTorneo(@ModelAttribute("torneo") Torneo torneo) {
        torneoService.save(torneo);
        return "redirect:/torneos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Torneo torneo = torneoService.findById(id).orElse(null);
        model.addAttribute("torneo", torneo);
        model.addAttribute("categorias", categoriaTorneoService.findAll());
        return "torneos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarTorneo(@PathVariable Long id) {
        torneoService.deleteById(id);
        return "redirect:/torneos";
    }

    @GetMapping("/reporte")
    public String mostrarFormularioReporte() {
        return "torneos/reporte-formulario";
    }

    @PostMapping("/reporte")
    public String generarReporte(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        List<ReporteTorneoDTO> reporte = torneoService.generarReporte(fechaInicio, fechaFin);
        model.addAttribute("reporte", reporte);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        return "torneos/reporte-resultado";
    }
}
