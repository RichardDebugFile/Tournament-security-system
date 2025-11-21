package com.diego.curso.springboot.webapp.springboot_web.repositories;

import com.diego.curso.springboot.webapp.springboot_web.models.Torneo;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    List<Torneo> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
}