package com.diego.curso.springboot.webapp.springboot_web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AsistenciaFormularioDTO {

    @NotNull(message = "Debe seleccionar un partido")
    private Long partidoId;

    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Double precioGeneral;

    @Min(value = 0, message = "La cantidad debe ser mayor o igual a 0")
    private Integer cantidadGeneral;

    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Double precioPalco;

    @Min(value = 0, message = "La cantidad debe ser mayor o igual a 0")
    private Integer cantidadPalco;

    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Double precioVip;

    @Min(value = 0, message = "La cantidad debe ser mayor o igual a 0")
    private Integer cantidadVip;

    // ==== Getters y Setters ====

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public Double getPrecioGeneral() {
        return precioGeneral;
    }

    public void setPrecioGeneral(Double precioGeneral) {
        this.precioGeneral = precioGeneral;
    }

    public Integer getCantidadGeneral() {
        return cantidadGeneral;
    }

    public void setCantidadGeneral(Integer cantidadGeneral) {
        this.cantidadGeneral = cantidadGeneral;
    }

    public Double getPrecioPalco() {
        return precioPalco;
    }

    public void setPrecioPalco(Double precioPalco) {
        this.precioPalco = precioPalco;
    }

    public Integer getCantidadPalco() {
        return cantidadPalco;
    }

    public void setCantidadPalco(Integer cantidadPalco) {
        this.cantidadPalco = cantidadPalco;
    }

    public Double getPrecioVip() {
        return precioVip;
    }

    public void setPrecioVip(Double precioVip) {
        this.precioVip = precioVip;
    }

    public Integer getCantidadVip() {
        return cantidadVip;
    }

    public void setCantidadVip(Integer cantidadVip) {
        this.cantidadVip = cantidadVip;
    }
}
