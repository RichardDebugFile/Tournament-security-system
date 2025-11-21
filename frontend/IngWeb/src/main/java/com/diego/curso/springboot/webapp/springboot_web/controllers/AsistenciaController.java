package com.diego.curso.springboot.webapp.springboot_web.controllers;

import com.diego.curso.springboot.webapp.springboot_web.dto.AsistenciaFormularioDTO;
import com.diego.curso.springboot.webapp.springboot_web.models.Asistencia;
import com.diego.curso.springboot.webapp.springboot_web.models.TipoEntrada;
import com.diego.curso.springboot.webapp.springboot_web.models.Partido;
import com.diego.curso.springboot.webapp.springboot_web.services.AsistenciaService;
import com.diego.curso.springboot.webapp.springboot_web.services.PartidoService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private PartidoService partidoService;

    // 🔹 NUEVO MÉTODO PARA MOSTRAR LA LISTA DE ASISTENCIAS
    @GetMapping
    public String listar(Model model) {
        List<Asistencia> asistencias = asistenciaService.findAll();
        model.addAttribute("asistencias", asistencias);
        return "asistencias/lista";
    }

    @GetMapping("/nueva")
    public String nuevo(Model model) {
        model.addAttribute("formulario", new AsistenciaFormularioDTO());
        model.addAttribute("partidos", partidoService.findAll());
        return "asistencias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("formulario") AsistenciaFormularioDTO formulario,
                          BindingResult result,
                          Model model) {
    
        model.addAttribute("partidos", partidoService.findAll());
    
        if (result.hasErrors()) {
            return "asistencias/formulario";
        }
    
        Partido partido = partidoService.findById(formulario.getPartidoId()).orElse(null);
        if (partido == null) {
            model.addAttribute("error", "El partido seleccionado no es válido.");
            return "asistencias/formulario";
        }
    
        boolean tieneDatos = (formulario.getCantidadGeneral() != null && formulario.getCantidadGeneral() > 0) ||
                             (formulario.getCantidadPalco() != null && formulario.getCantidadPalco() > 0) ||
                             (formulario.getCantidadVip() != null && formulario.getCantidadVip() > 0);
    
        if (!tieneDatos) {
            model.addAttribute("error", "Debe ingresar al menos una cantidad de asistentes");
            return "asistencias/formulario";
        }
    
        if (formulario.getCantidadGeneral() != null && formulario.getCantidadGeneral() > 0) {
            Asistencia a = new Asistencia();
            a.setPartido(partido);
            a.setTipoEntrada(TipoEntrada.GENERAL);
            a.setCantidadVendida(formulario.getCantidadGeneral());
            a.setPrecio(formulario.getPrecioGeneral());
            asistenciaService.save(a);
        }
    
        if (formulario.getCantidadPalco() != null && formulario.getCantidadPalco() > 0) {
            Asistencia a = new Asistencia();
            a.setPartido(partido);
            a.setTipoEntrada(TipoEntrada.PALCO);
            a.setCantidadVendida(formulario.getCantidadPalco());
            a.setPrecio(formulario.getPrecioPalco());
            asistenciaService.save(a);
        }
    
        if (formulario.getCantidadVip() != null && formulario.getCantidadVip() > 0) {
            Asistencia a = new Asistencia();
            a.setPartido(partido);
            a.setTipoEntrada(TipoEntrada.VIP);
            a.setCantidadVendida(formulario.getCantidadVip());
            a.setPrecio(formulario.getPrecioVip());
            asistenciaService.save(a);
        }
    
        return "redirect:/asistencias";
    }

    @GetMapping("/editar/{id}")
public String editar(@PathVariable Long id, Model model) {
    Asistencia asistencia = asistenciaService.findById(id).orElse(null);
    if (asistencia == null) {
        return "redirect:/asistencias";
    }
    model.addAttribute("asistencia", asistencia);
    model.addAttribute("partidos", partidoService.findAll());
    return "asistencias/editar";
}

@PostMapping("/actualizar")
public String actualizar(@ModelAttribute("asistencia") Asistencia asistencia, BindingResult result, Model model) {
    if (result.hasErrors()) {
        model.addAttribute("partidos", partidoService.findAll());
        return "asistencias/editar";
    }
    asistenciaService.save(asistencia);
    return "redirect:/asistencias";
}

    
}
