package com.diego.curso.springboot.webapp.springboot_web.repositories;

import com.diego.curso.springboot.webapp.springboot_web.models.OAuth2AuthenticationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OAuth2AuthenticationLogRepository extends JpaRepository<OAuth2AuthenticationLog, Long> {

    // Buscar logs por proveedor
    List<OAuth2AuthenticationLog> findByProvider(String provider);

    // Buscar logs por proveedor y rango de fechas
    List<OAuth2AuthenticationLog> findByProviderAndTimestampBetween(
            String provider,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Buscar logs exitosos o fallidos
    List<OAuth2AuthenticationLog> findBySuccess(Boolean success);

    // Buscar logs por usuario
    List<OAuth2AuthenticationLog> findByUsername(String username);

    // Buscar logs por email
    List<OAuth2AuthenticationLog> findByUserEmail(String userEmail);

    // Buscar intentos fallidos recientes por IP
    @Query("SELECT l FROM OAuth2AuthenticationLog l WHERE l.ipAddress = :ipAddress " +
           "AND l.success = false AND l.timestamp >= :since ORDER BY l.timestamp DESC")
    List<OAuth2AuthenticationLog> findFailedAttemptsByIpSince(
            @Param("ipAddress") String ipAddress,
            @Param("since") LocalDateTime since
    );

    // Contar autenticaciones exitosas por proveedor
    @Query("SELECT l.provider, COUNT(l) FROM OAuth2AuthenticationLog l " +
           "WHERE l.success = true GROUP BY l.provider")
    List<Object[]> countSuccessfulAuthenticationsByProvider();

    // Contar autenticaciones por proveedor en un rango de fechas
    @Query("SELECT l.provider, COUNT(l) FROM OAuth2AuthenticationLog l " +
           "WHERE l.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY l.provider")
    List<Object[]> countAuthenticationsByProviderBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Estadísticas de autenticaciones exitosas vs fallidas por proveedor
    @Query("SELECT l.provider, l.success, COUNT(l) FROM OAuth2AuthenticationLog l " +
           "GROUP BY l.provider, l.success ORDER BY l.provider")
    List<Object[]> getAuthenticationStatsByProvider();

    // Obtener últimas autenticaciones
    List<OAuth2AuthenticationLog> findTop10ByOrderByTimestampDesc();

    // Contar autenticaciones exitosas en las últimas 24 horas
    @Query("SELECT COUNT(l) FROM OAuth2AuthenticationLog l " +
           "WHERE l.success = true AND l.timestamp >= :since")
    Long countSuccessfulAuthenticationsSince(@Param("since") LocalDateTime since);

    // Contar autenticaciones fallidas en las últimas 24 horas
    @Query("SELECT COUNT(l) FROM OAuth2AuthenticationLog l " +
           "WHERE l.success = false AND l.timestamp >= :since")
    Long countFailedAuthenticationsSince(@Param("since") LocalDateTime since);

    // Usuarios únicos por proveedor
    @Query("SELECT l.provider, COUNT(DISTINCT l.username) FROM OAuth2AuthenticationLog l " +
           "WHERE l.success = true GROUP BY l.provider")
    List<Object[]> countUniqueUsersByProvider();

    // Limpiar logs antiguos (para mantenimiento)
    void deleteByTimestampBefore(LocalDateTime timestamp);
}
