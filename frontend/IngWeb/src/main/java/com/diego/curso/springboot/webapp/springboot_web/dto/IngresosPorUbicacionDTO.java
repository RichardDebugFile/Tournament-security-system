package com.diego.curso.springboot.webapp.springboot_web.dto;

public class IngresosPorUbicacionDTO {

    private Long ubicacionId;
    private String nombreUbicacion;
    private double ingresoTotal;

    // ==== Constructores ====

    public IngresosPorUbicacionDTO() {}

    public IngresosPorUbicacionDTO(Long ubicacionId, String nombreUbicacion, double ingresoTotal) {
        this.ubicacionId = ubicacionId;
        this.nombreUbicacion = nombreUbicacion;
        this.ingresoTotal = ingresoTotal;
    }

    // ==== Getters y Setters ====

    public Long getUbicacionId() {
        return ubicacionId;
    }

    public void setUbicacionId(Long ubicacionId) {
        this.ubicacionId = ubicacionId;
    }

    public String getNombreUbicacion() {
        return nombreUbicacion;
    }

    public void setNombreUbicacion(String nombreUbicacion) {
        this.nombreUbicacion = nombreUbicacion;
    }

    public double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}
