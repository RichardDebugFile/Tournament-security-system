package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.Asistencia;
import com.diego.curso.springboot.webapp.springboot_web.repositories.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Override
    public List<Asistencia> findAll() {
        return asistenciaRepository.findAll();
    }

    @Override
    public Optional<Asistencia> findById(Long id) {
        return asistenciaRepository.findById(id);
    }

    @Override
    public Asistencia save(Asistencia asistencia) {
        return asistenciaRepository.save(asistencia);
    }

    @Override
    public void deleteById(Long id) {
        asistenciaRepository.deleteById(id);
    }
}
