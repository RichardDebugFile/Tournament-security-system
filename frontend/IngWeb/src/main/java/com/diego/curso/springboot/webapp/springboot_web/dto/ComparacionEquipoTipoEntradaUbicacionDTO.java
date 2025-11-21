package com.diego.curso.springboot.webapp.springboot_web.dto;

import com.diego.curso.springboot.webapp.springboot_web.models.TipoEntrada;

public class ComparacionEquipoTipoEntradaUbicacionDTO {

    private String equipo;
    private TipoEntrada tipoEntrada;
    private String ubicacion;
    private int cantidadVendida;
    private double ingresoTotal;

    // ==== Constructor requerido por Hibernate para el JPQL ====
    public ComparacionEquipoTipoEntradaUbicacionDTO(String equipo, TipoEntrada tipoEntrada, String ubicacion, Long cantidadVendida, Double ingresoTotal) {
        this.equipo = equipo;
        this.tipoEntrada = tipoEntrada;
        this.ubicacion = ubicacion;
        this.cantidadVendida = (cantidadVendida != null) ? cantidadVendida.intValue() : 0;
        this.ingresoTotal = (ingresoTotal != null) ? ingresoTotal : 0.0;
    }

    // ==== Constructor vacío ====
    public ComparacionEquipoTipoEntradaUbicacionDTO() {}

    // ==== Getters y Setters ====

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public TipoEntrada getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(TipoEntrada tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}
