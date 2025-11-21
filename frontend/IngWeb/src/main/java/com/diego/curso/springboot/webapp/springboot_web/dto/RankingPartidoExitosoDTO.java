package com.diego.curso.springboot.webapp.springboot_web.dto;

import java.time.LocalDate;

public class RankingPartidoExitosoDTO {

    private Long partidoId;
    private String descripcionPartido;
    private LocalDate fecha;
    private String ubicacion;
    private Long totalEntradasVendidas;
    private Double ingresoTotal;


    public RankingPartidoExitosoDTO(Long partidoId, String descripcionPartido, LocalDate fecha, String ubicacion,
                                    Long totalEntradasVendidas, Double ingresoTotal) {
        this.partidoId = partidoId;
        this.descripcionPartido = descripcionPartido;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.totalEntradasVendidas = totalEntradasVendidas;
        this.ingresoTotal = ingresoTotal;
    }

    // === Getters y Setters ===

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public String getDescripcionPartido() {
        return descripcionPartido;
    }

    public void setDescripcionPartido(String descripcionPartido) {
        this.descripcionPartido = descripcionPartido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Long getTotalEntradasVendidas() {
        return totalEntradasVendidas;
    }

    public void setTotalEntradasVendidas(Long totalEntradasVendidas) {
        this.totalEntradasVendidas = totalEntradasVendidas;
    }

    public Double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(Double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}
