package com.diego.curso.springboot.webapp.springboot_web.repositories;

import java.util.List;

import com.diego.curso.springboot.webapp.springboot_web.dto.ConsultaEquiposPorTorneo;
import com.diego.curso.springboot.webapp.springboot_web.models.Equipo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByTorneoId(Long torneoId);

    @Query("SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.ConsultaEquiposPorTorneo(e.id, e.nombre, e.numeroJugadores, e.colorUniforme, e.torneo.nombre) " +
           "FROM Equipo e WHERE e.torneo.id = :torneoId")
    List<ConsultaEquiposPorTorneo> obtenerEquiposPorTorneo(@Param("torneoId") Long torneoId);
}
