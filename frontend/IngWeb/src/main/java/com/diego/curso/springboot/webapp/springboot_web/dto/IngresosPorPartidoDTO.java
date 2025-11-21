package com.diego.curso.springboot.webapp.springboot_web.dto;

public class IngresosPorPartidoDTO {

    private Long partidoId;
    private String nombrePartido;
    private double ingresoTotal;

    public IngresosPorPartidoDTO() {}

    public IngresosPorPartidoDTO(Long partidoId, String nombrePartido, double ingresoTotal) {
        this.partidoId = partidoId;
        this.nombrePartido = nombrePartido;
        this.ingresoTotal = ingresoTotal;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public String getNombrePartido() {
        return nombrePartido;
    }

    public void setNombrePartido(String nombrePartido) {
        this.nombrePartido = nombrePartido;
    }

    public double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}
