package com.diego.curso.springboot.webapp.springboot_web.dto;

public class IngresosPorCategoriaDTO {
    private String categoria;
    private Double totalIngresos;

    public IngresosPorCategoriaDTO(String categoria, Double totalIngresos) {
        this.categoria = categoria;
        this.totalIngresos = totalIngresos;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(Double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }
}
