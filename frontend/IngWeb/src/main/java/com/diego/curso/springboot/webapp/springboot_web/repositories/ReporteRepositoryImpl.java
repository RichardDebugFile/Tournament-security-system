package com.diego.curso.springboot.webapp.springboot_web.repositories;

import com.diego.curso.springboot.webapp.springboot_web.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class ReporteRepositoryImpl implements ReporteRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
public List<IngresosPorPartidoDTO> obtenerIngresosPorPartido() {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.IngresosPorPartidoDTO(
            a.partido.id,
            CONCAT(p.equipo1.nombre, ' vs ', p.equipo2.nombre),
            SUM(a.precio * a.cantidadVendida)
        )
        FROM Asistencia a
        JOIN a.partido p
        GROUP BY a.partido.id, p.equipo1.nombre, p.equipo2.nombre
        """;

    return em.createQuery(jpql, IngresosPorPartidoDTO.class).getResultList();
}


@Override
public List<IngresosPorEquipoDTO> obtenerIngresosPorEquipo() {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.IngresosPorEquipoDTO(
            e.id,
            e.nombre,
            SUM(a.precio * a.cantidadVendida)
        )
        FROM Asistencia a
        JOIN a.partido p
        JOIN Equipo e ON e.id = p.equipo1.id OR e.id = p.equipo2.id
        GROUP BY e.id, e.nombre
        """;

    return em.createQuery(jpql, IngresosPorEquipoDTO.class).getResultList();
}


@Override
public List<IngresosPorUbicacionDTO> obtenerIngresosPorUbicacion() {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.IngresosPorUbicacionDTO(
            p.ubicacion.id,
            CONCAT(p.ubicacion.estadio, ' - ', p.ubicacion.sector),
            SUM(a.precio * a.cantidadVendida)
        )
        FROM Asistencia a
        JOIN a.partido p
        GROUP BY p.ubicacion.id, p.ubicacion.estadio, p.ubicacion.sector
        """;

    return em.createQuery(jpql, IngresosPorUbicacionDTO.class).getResultList();
}



@Override
public List<IngresosPorFechaDTO> obtenerIngresosPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.IngresosPorFechaDTO(
            p.fecha,
            SUM(a.precio * a.cantidadVendida)
        )
        FROM Asistencia a
        JOIN a.partido p
        WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin
        GROUP BY p.fecha
        ORDER BY p.fecha
        """;

    return em.createQuery(jpql, IngresosPorFechaDTO.class)
             .setParameter("fechaInicio", fechaInicio)
             .setParameter("fechaFin", fechaFin)
             .getResultList();
}


@Override
public List<EntradasVendidasPorTipoDTO> obtenerEntradasPorTipo() {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.EntradasVendidasPorTipoDTO(
            a.tipoEntrada,
            SUM(a.cantidadVendida),
            SUM(a.cantidadVendida * a.precio)
        )
        FROM Asistencia a
        GROUP BY a.tipoEntrada
        ORDER BY a.tipoEntrada
        """;

    return em.createQuery(jpql, EntradasVendidasPorTipoDTO.class)
             .getResultList();
}


@Override
public List<ComparacionEquipoTipoEntradaUbicacionDTO> obtenerComparacionEquipoTipoEntradaUbicacion() {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.ComparacionEquipoTipoEntradaUbicacionDTO(
            e.nombre,
            a.tipoEntrada,
            CONCAT(u.estadio, ' - ', u.sector),
            SUM(a.cantidadVendida),
            SUM(a.cantidadVendida * a.precio)
        )
        FROM Asistencia a
        JOIN a.partido p
        JOIN p.ubicacion u
        JOIN Equipo e ON e = p.equipo1 OR e = p.equipo2
        GROUP BY e.nombre, a.tipoEntrada, u.estadio, u.sector
        ORDER BY e.nombre, a.tipoEntrada, u.estadio
        """;

    return em.createQuery(jpql, ComparacionEquipoTipoEntradaUbicacionDTO.class)
             .getResultList();
}

@Override
public List<RankingPartidoExitosoDTO> obtenerRankingPartidos() {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.RankingPartidoExitosoDTO(
            a.partido.id,
            CONCAT(a.partido.equipo1.nombre, ' vs ', a.partido.equipo2.nombre),
            a.partido.fecha,
            CONCAT(a.partido.ubicacion.estadio, ' - ', a.partido.ubicacion.sector),
            SUM(a.cantidadVendida),
            SUM(a.precio * a.cantidadVendida)
        )
        FROM Asistencia a
        GROUP BY a.partido.id, a.partido.equipo1.nombre, a.partido.equipo2.nombre,
                 a.partido.fecha, a.partido.ubicacion.estadio, a.partido.ubicacion.sector
        ORDER BY SUM(a.precio * a.cantidadVendida) DESC
    """;

    return em.createQuery(jpql, RankingPartidoExitosoDTO.class).getResultList();
}


@PersistenceContext
    private EntityManager entityManager;
@Override
    public List<RecomendacionCombinacionExitosaDTO> obtenerRecomendaciones() {
        String jpql = """
            SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.RecomendacionCombinacionExitosaDTO(
                CASE
                    WHEN a.partido.equipo1 IS NOT NULL THEN a.partido.equipo1.nombre
                    ELSE a.partido.equipo2.nombre
                END,
                a.tipoEntrada,
                CONCAT(a.partido.ubicacion.estadio, ' - ', a.partido.ubicacion.sector),
                SUM(a.cantidadVendida),
                SUM(a.precio * a.cantidadVendida),
                CONCAT(
                    'Promover más partidos de ',
                    CASE
                        WHEN a.partido.equipo1 IS NOT NULL THEN a.partido.equipo1.nombre
                        ELSE a.partido.equipo2.nombre
                    END,
                    ' en ',
                    CONCAT(a.partido.ubicacion.estadio, ' - ', a.partido.ubicacion.sector),
                    ' con entrada ',
                    a.tipoEntrada
                )
            )
            FROM Asistencia a
            GROUP BY
                CASE
                    WHEN a.partido.equipo1 IS NOT NULL THEN a.partido.equipo1.nombre
                    ELSE a.partido.equipo2.nombre
                END,
                a.tipoEntrada,
                a.partido.ubicacion.estadio,
                a.partido.ubicacion.sector
            ORDER BY SUM(a.precio * a.cantidadVendida) DESC
        """;
        

        return entityManager.createQuery(jpql, RecomendacionCombinacionExitosaDTO.class).getResultList();
    }




@Override
public List<IngresosPorCategoriaDTO> obtenerIngresosPorCategoria() {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.IngresosPorCategoriaDTO(
            t.categoria.nombre,
            SUM(a.precio * a.cantidadVendida)
        )
        FROM Asistencia a
        JOIN a.partido p
        JOIN p.torneo t
        GROUP BY t.categoria.nombre
        ORDER BY SUM(a.precio * a.cantidadVendida) DESC
        """;

    return em.createQuery(jpql, IngresosPorCategoriaDTO.class)
             .getResultList();
}

@Override
public List<RentabilidadTorneoDTO> calcularRentabilidadPorTorneo() {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.RentabilidadTorneoDTO(
            t.nombre,
            CAST(t.estado AS string),
            t.costo,
            SUM(a.precio * a.cantidadVendida)
        )
        FROM Asistencia a
        JOIN a.partido p
        JOIN p.torneo t
        GROUP BY t.id, t.nombre, t.estado, t.costo
    """;
    

    return em.createQuery(jpql, RentabilidadTorneoDTO.class).getResultList();
}


@Override
public List<ResumenIngresosPorTorneoYEquipoDTO> obtenerIngresosPorTorneoYEquipo() {
    String jpql = """
        SELECT new com.diego.curso.springboot.webapp.springboot_web.dto.ResumenIngresosPorTorneoYEquipoDTO(
            t.id,
            t.nombre,
            e.nombre,
            SUM(a.precio * a.cantidadVendida)
        )
        FROM Asistencia a
        JOIN a.partido p
        JOIN p.torneo t
        JOIN Equipo e ON e.id = p.equipo1.id OR e.id = p.equipo2.id
        GROUP BY t.id, t.nombre, e.nombre
        ORDER BY t.id, SUM(a.precio * a.cantidadVendida) DESC
        """;

    return em.createQuery(jpql, ResumenIngresosPorTorneoYEquipoDTO.class).getResultList();
}




}
