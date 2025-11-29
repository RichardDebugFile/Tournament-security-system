package com.diego.curso.springboot.webapp.springboot_web.config;

import com.diego.curso.springboot.webapp.springboot_web.models.Usuario;
import com.diego.curso.springboot.webapp.springboot_web.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioService.findByEmail("admin@tournament.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setEmail("admin@tournament.com");
            admin.setPassword("Password123!"); // Contraseña segura por defecto
            admin.setFechaRegistro(LocalDate.now());

            usuarioService.save(admin);
            System.out.println("Usuario admin creado: admin@tournament.com / Password123!");
        }
    }
}
