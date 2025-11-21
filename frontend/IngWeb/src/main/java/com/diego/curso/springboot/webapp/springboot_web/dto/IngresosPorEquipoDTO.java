package com.diego.curso.springboot.webapp.springboot_web.dto;

public class IngresosPorEquipoDTO {

    private Long equipoId;
    private String nombreEquipo;
    private double ingresoTotal;

    // ==== Constructores ====

    public IngresosPorEquipoDTO() {}

    public IngresosPorEquipoDTO(Long equipoId, String nombreEquipo, double ingresoTotal) {
        this.equipoId = equipoId;
        this.nombreEquipo = nombreEquipo;
        this.ingresoTotal = ingresoTotal;
    }

    // ==== Getters y Setters ====

    public Long getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Long equipoId) {
        this.equipoId = equipoId;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}
