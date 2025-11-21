package com.diego.curso.springboot.webapp.springboot_web.repositories;

import com.diego.curso.springboot.webapp.springboot_web.models.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartidoRepository extends JpaRepository<Partido, Long> {

    @Query("""
        SELECT COUNT(p) > 0 FROM Partido p
        WHERE p.torneo.id = :torneoId AND (
            (p.equipo1.id = :equipo1Id AND p.equipo2.id = :equipo2Id) OR
            (p.equipo1.id = :equipo2Id AND p.equipo2.id = :equipo1Id)
        )
    """)
    boolean existePartidoEntreEquipos(
        @Param("torneoId") Long torneoId,
        @Param("equipo1Id") Long equipo1Id,
        @Param("equipo2Id") Long equipo2Id
    );
}
