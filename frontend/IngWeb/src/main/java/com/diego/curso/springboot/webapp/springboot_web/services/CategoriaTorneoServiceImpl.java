package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.models.CategoriaTorneo;
import com.diego.curso.springboot.webapp.springboot_web.repositories.CategoriaTorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaTorneoServiceImpl implements CategoriaTorneoService {

    @Autowired
    private CategoriaTorneoRepository categoriaTorneoRepository;

    @Override
    public List<CategoriaTorneo> findAll() {
        return categoriaTorneoRepository.findAll();
    }

    @Override
    public Optional<CategoriaTorneo> findById(Long id) {
        return categoriaTorneoRepository.findById(id);
    }
}
