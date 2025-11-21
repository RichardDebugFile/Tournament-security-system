package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.Equipo;
import com.diego.curso.springboot.webapp.springboot_web.repositories.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipoServiceImpl implements EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    public List<Equipo> findAll() {
        return equipoRepository.findAll();
    }

    @Override
    public Optional<Equipo> findById(Long id) {
        return equipoRepository.findById(id);
    }

    @Override
    public Equipo save(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    @Override
    public void deleteById(Long id) {
        equipoRepository.deleteById(id);
    }

    @Override
public List<Equipo> findByTorneoId(Long torneoId) {
    return equipoRepository.findByTorneoId(torneoId);
}
}
