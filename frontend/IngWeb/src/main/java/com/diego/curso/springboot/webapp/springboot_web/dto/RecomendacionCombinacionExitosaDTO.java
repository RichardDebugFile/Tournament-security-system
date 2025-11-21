package com.diego.curso.springboot.webapp.springboot_web.dto;

import com.diego.curso.springboot.webapp.springboot_web.models.TipoEntrada;

public class RecomendacionCombinacionExitosaDTO {

    private String equipo;
    private TipoEntrada tipoEntrada;
    private String ubicacion;
    private Long totalAsistentes;
    private Double totalIngresos;
    private String recomendacion;

    public RecomendacionCombinacionExitosaDTO(String equipo, TipoEntrada tipoEntrada, String ubicacion,
                                               Long totalAsistentes, Double totalIngresos, String recomendacion) {
        this.equipo = equipo;
        this.tipoEntrada = tipoEntrada;
        this.ubicacion = ubicacion;
        this.totalAsistentes = totalAsistentes;
        this.totalIngresos = totalIngresos;
        this.recomendacion = recomendacion;
    }

    public String getEquipo() {
        return equipo;
    }

    public TipoEntrada getTipoEntrada() {
        return tipoEntrada;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public Long getTotalAsistentes() {
        return totalAsistentes;
    }

    public Double getTotalIngresos() {
        return totalIngresos;
    }

    public String getRecomendacion() {
        return recomendacion;
    }
}
