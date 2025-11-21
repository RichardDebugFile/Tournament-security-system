package com.diego.curso.springboot.webapp.springboot_web.dto;

import com.diego.curso.springboot.webapp.springboot_web.models.TipoEntrada;

public class EntradasVendidasPorTipoDTO {

    private TipoEntrada tipoEntrada;
    private Long cantidadVendida;
    private Double ingresoTotal;

    public EntradasVendidasPorTipoDTO() {}

    public EntradasVendidasPorTipoDTO(TipoEntrada tipoEntrada, Long cantidadVendida, Double ingresoTotal) {
        this.tipoEntrada = tipoEntrada;
        this.cantidadVendida = cantidadVendida;
        this.ingresoTotal = ingresoTotal;
    }

    public TipoEntrada getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(TipoEntrada tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public Double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(Double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}
