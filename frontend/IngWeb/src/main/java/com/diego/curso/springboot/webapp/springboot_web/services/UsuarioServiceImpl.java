package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.Usuario;
import com.diego.curso.springboot.webapp.springboot_web.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario save(Usuario usuario) {
        // Validar y encriptar la contraseña antes de guardar
        if (!esPasswordSegura(usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y símbolos.");
        }

        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(LocalDate.now());
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Validación personalizada de contraseña segura
    private boolean esPasswordSegura(String password) {
        return password != null &&
               password.length() >= 8 &&
               password.matches(".*[A-Z].*") && // al menos una mayúscula
               password.matches(".*[a-z].*") && // al menos una minúscula
               password.matches(".*\\d.*") &&   // al menos un número
               password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:'\",.<>/?].*"); // al menos un símbolo
    }
}
