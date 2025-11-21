package com.diego.curso.springboot.webapp.springboot_web.dto;

public class RentabilidadTorneoDTO {
    private String nombreTorneo;
    private String estado;
    private Double costo;
    private Double ingresosTotales;
    private Double diferencia;
    private Double faltante;

    public RentabilidadTorneoDTO(String nombreTorneo, String estado, Double costo, Double ingresosTotales) {
        this.nombreTorneo = nombreTorneo;
        this.estado = estado;
        this.costo = costo;
        this.ingresosTotales = ingresosTotales;

        this.diferencia = ingresosTotales - costo;
        this.faltante = (ingresosTotales < costo) ? (costo - ingresosTotales) : 0.0;
    }

    // Getters
    public String getNombreTorneo() { return nombreTorneo; }
    public String getEstado() { return estado; }
    public Double getCosto() { return costo; }
    public Double getIngresosTotales() { return ingresosTotales; }
    public Double getDiferencia() { return diferencia; }
    public Double getFaltante() { return faltante; }
}
