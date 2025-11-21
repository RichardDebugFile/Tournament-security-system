package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.Ubicacion;
import com.diego.curso.springboot.webapp.springboot_web.repositories.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Override
    public List<Ubicacion> findAll() {
        return ubicacionRepository.findAll();
    }

    @Override
    public Optional<Ubicacion> findById(Long id) {
        return ubicacionRepository.findById(id);
    }

    @Override
    public Ubicacion save(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    @Override
    public void deleteById(Long id) {
        ubicacionRepository.deleteById(id);
    }
}
