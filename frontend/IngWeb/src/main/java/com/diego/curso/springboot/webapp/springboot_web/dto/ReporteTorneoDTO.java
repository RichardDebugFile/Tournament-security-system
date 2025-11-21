package com.diego.curso.springboot.webapp.springboot_web.dto;

import java.time.LocalDate;

public class ReporteTorneoDTO {
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private String estado;
    private long diasRetrasados;

    // Constructor
    public ReporteTorneoDTO(String nombre, LocalDate fechaInicio, LocalDate fechaFinal, String estado, long diasRetrasados) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.estado = estado;
        this.diasRetrasados = diasRetrasados;
    }

    // Getters
    public String getNombre() { return nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFinal() { return fechaFinal; }
    public String getEstado() { return estado; }
    public long getDiasRetrasados() { return diasRetrasados; }
}