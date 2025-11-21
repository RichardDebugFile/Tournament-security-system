package com.diego.curso.springboot.webapp.springboot_web.controllers;

import com.diego.curso.springboot.webapp.springboot_web.models.Equipo;
import com.diego.curso.springboot.webapp.springboot_web.services.EquipoService;
import com.diego.curso.springboot.webapp.springboot_web.services.TorneoService;
import com.diego.curso.springboot.webapp.springboot_web.models.Torneo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/equipos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private TorneoService torneoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("equipos", equipoService.findAll());
        return "equipos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("equipo", new Equipo());
        model.addAttribute("torneos", torneoService.findAll()); // Dropdown de torneos
        return "equipos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Equipo equipo) {
        equipoService.save(equipo);
        return "redirect:/equipos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Equipo equipo = equipoService.findById(id).orElse(null);
        model.addAttribute("equipo", equipo);
        model.addAttribute("torneos", torneoService.findAll());
        return "equipos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        equipoService.deleteById(id);
        return "redirect:/equipos";
    }
}
