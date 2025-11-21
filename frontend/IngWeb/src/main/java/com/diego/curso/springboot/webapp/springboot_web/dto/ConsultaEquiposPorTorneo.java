package com.diego.curso.springboot.webapp.springboot_web.dto;

public class ConsultaEquiposPorTorneo {
    private Long id;
    private String nombre;
    private int numeroJugadores;
    private String colorUniforme;
    private String torneoNombre;

   public ConsultaEquiposPorTorneo() {
    // Necesario para frameworks y creación por setters
}

public ConsultaEquiposPorTorneo(Long id, String nombre, int numeroJugadores, String colorUniforme, String torneoNombre) {
    this.id = id;
    this.nombre = nombre;
    this.numeroJugadores = numeroJugadores;
    this.colorUniforme = colorUniforme;
    this.torneoNombre = torneoNombre;
}

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumeroJugadores() {
        return numeroJugadores;
    }

    public void setNumeroJugadores(int numeroJugadores) {
        this.numeroJugadores = numeroJugadores;
    }

    public String getColorUniforme() {
        return colorUniforme;
    }

    public void setColorUniforme(String colorUniforme) {
        this.colorUniforme = colorUniforme;
    }

        public String getTorneoNombre() {
        return torneoNombre;
    }

    public void setTorneoNombre(String torneoNombre) {
        this.torneoNombre = torneoNombre;
    }
}
