package com.diego.curso.springboot.webapp.springboot_web.controllers;

import com.diego.curso.springboot.webapp.springboot_web.models.Ubicacion;
import com.diego.curso.springboot.webapp.springboot_web.services.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ubicaciones", ubicacionService.findAll());
        return "ubicaciones/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("ubicacion", new Ubicacion());
        return "ubicaciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Ubicacion ubicacion) {
        ubicacionService.save(ubicacion);
        return "redirect:/ubicaciones";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Ubicacion ubicacion = ubicacionService.findById(id).orElse(null);
        model.addAttribute("ubicacion", ubicacion);
        return "ubicaciones/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        ubicacionService.deleteById(id);
        return "redirect:/ubicaciones";
    }
}
