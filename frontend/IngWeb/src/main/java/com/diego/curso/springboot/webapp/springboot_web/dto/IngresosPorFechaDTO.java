package com.diego.curso.springboot.webapp.springboot_web.dto;

import java.time.LocalDate;

public class IngresosPorFechaDTO {

    private LocalDate fecha;
    private double ingresoTotal;

    // ==== Constructores ====

    public IngresosPorFechaDTO() {}

    public IngresosPorFechaDTO(LocalDate fecha, double ingresoTotal) {
        this.fecha = fecha;
        this.ingresoTotal = ingresoTotal;
    }

    // ==== Getters y Setters ====

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}
