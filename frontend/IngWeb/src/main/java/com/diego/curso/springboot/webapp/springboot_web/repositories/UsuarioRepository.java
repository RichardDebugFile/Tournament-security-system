package com.diego.curso.springboot.webapp.springboot_web.repositories;

import com.diego.curso.springboot.webapp.springboot_web.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Para buscar usuarios por email (usado en login)
    Optional<Usuario> findByEmail(String email);

    // Puedes agregar más métodos personalizados si los necesitas
}