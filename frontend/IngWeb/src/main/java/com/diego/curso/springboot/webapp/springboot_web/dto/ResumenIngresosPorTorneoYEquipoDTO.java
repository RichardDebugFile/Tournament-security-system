package com.diego.curso.springboot.webapp.springboot_web.dto;

public class ResumenIngresosPorTorneoYEquipoDTO {

    private Long idTorneo;
    private String nombreTorneo;
    private String nombreEquipo;
    private Double ingresoTotal;
    private String tipo; // "mayor" o "menor" o vacío

    public ResumenIngresosPorTorneoYEquipoDTO(Long idTorneo, String nombreTorneo, String nombreEquipo, Double ingresoTotal) {
        this.idTorneo = idTorneo;
        this.nombreTorneo = nombreTorneo;
        this.nombreEquipo = nombreEquipo;
        this.ingresoTotal = ingresoTotal;
        this.tipo = ""; // se asignará en el servicio
    }

    // Getters
    public Long getIdTorneo() {
        return idTorneo;
    }

    public String getNombreTorneo() {
        return nombreTorneo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public Double getIngresoTotal() {
        return ingresoTotal;
    }

    public String getTipo() {
        return tipo;
    }

    // Setter del tipo
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
