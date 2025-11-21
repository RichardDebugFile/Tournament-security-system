package com.diego.curso.springboot.webapp.springboot_web.services;

import com.diego.curso.springboot.webapp.springboot_web.dto.ResumenIngresosPorTorneoYEquipoDTO;
import com.diego.curso.springboot.webapp.springboot_web.models.Partido;
import com.diego.curso.springboot.webapp.springboot_web.repositories.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartidoServiceImpl implements PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;

    @Override
    public List<Partido> findAll() {
        return partidoRepository.findAll();
    }

    @Override
    public Optional<Partido> findById(Long id) {
        return partidoRepository.findById(id);
    }

    @Override
    public Partido save(Partido partido) {
        return partidoRepository.save(partido);
    }

    @Override
    public void deleteById(Long id) {
        partidoRepository.deleteById(id);
    }
 

}


