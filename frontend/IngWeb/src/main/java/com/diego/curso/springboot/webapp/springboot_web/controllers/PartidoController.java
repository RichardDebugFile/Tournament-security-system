package com.diego.curso.springboot.webapp.springboot_web.controllers;

import com.diego.curso.springboot.webapp.springboot_web.models.Equipo;
import com.diego.curso.springboot.webapp.springboot_web.models.EstadoTorneo;
import com.diego.curso.springboot.webapp.springboot_web.models.Partido;
import com.diego.curso.springboot.webapp.springboot_web.models.Torneo;
import com.diego.curso.springboot.webapp.springboot_web.repositories.PartidoRepository;
import com.diego.curso.springboot.webapp.springboot_web.services.PartidoService;
import com.diego.curso.springboot.webapp.springboot_web.services.TorneoService;
import com.diego.curso.springboot.webapp.springboot_web.services.EquipoService;
import com.diego.curso.springboot.webapp.springboot_web.services.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/partidos")
public class PartidoController {

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private TorneoService torneoService;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private UbicacionService ubicacionService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("partidos", partidoService.findAll());
        return "partidos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("partido", new Partido());
        model.addAttribute("torneos", torneoService.findAll());
        model.addAttribute("equipos", equipoService.findAll());
        model.addAttribute("ubicaciones", ubicacionService.findAll());
        return "partidos/formulario";
    }

   @Autowired
private PartidoRepository partidoRepository;

@PostMapping("/guardar")
public String guardar(@ModelAttribute("partido") Partido partido, Model model) {
    Torneo torneo = partido.getTorneo();

    if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
        model.addAttribute("error", "No se pueden crear partidos en torneos finalizados.");
        model.addAttribute("partido", partido);
        model.addAttribute("torneos", torneoService.findAll());
        model.addAttribute("equipos", equipoService.findAll());
        model.addAttribute("ubicaciones", ubicacionService.findAll());
        return "partidos/formulario";
    }

    // Verificar si ya existe un partido entre esos equipos en este torneo
    Long torneoId = torneo.getId();
    Long equipo1Id = partido.getEquipo1().getId();
    Long equipo2Id = partido.getEquipo2().getId();

    boolean yaExiste = partidoRepository.existePartidoEntreEquipos(torneoId, equipo1Id, equipo2Id);
    if (yaExiste) {
        model.addAttribute("error", "Ya existe un partido entre esos dos equipos en este torneo.");
        model.addAttribute("partido", partido);
        model.addAttribute("torneos", torneoService.findAll());
        model.addAttribute("equipos", equipoService.findAll());
        model.addAttribute("ubicaciones", ubicacionService.findAll());
        return "partidos/formulario";
    }

    partidoService.save(partido);
    return "redirect:/partidos";
}



    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Partido partido = partidoService.findById(id).orElse(null);
        model.addAttribute("partido", partido);
        model.addAttribute("torneos", torneoService.findAll());
        model.addAttribute("equipos", equipoService.findAll());
        model.addAttribute("ubicaciones", ubicacionService.findAll());
        return "partidos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        partidoService.deleteById(id);
        return "redirect:/partidos";
    }

    @GetMapping("/por-torneo/{torneoId}")
@ResponseBody
public List<Equipo> obtenerEquiposPorTorneo(@PathVariable Long torneoId) {
    return equipoService.findByTorneoId(torneoId);
}

}
