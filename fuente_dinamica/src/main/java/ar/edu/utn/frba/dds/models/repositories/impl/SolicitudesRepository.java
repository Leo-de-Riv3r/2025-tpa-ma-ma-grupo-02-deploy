package ar.edu.utn.frba.dds.models.repositories.impl;

import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.enums.Motivo;
import ar.edu.utn.frba.dds.models.repositories.ISolicitudesRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class SolicitudesRepository implements ISolicitudesRepository {
    private List<Solicitud> solicitudes;
    private Long nextId;

    public SolicitudesRepository() {
        this.solicitudes = new ArrayList<>();
        this.nextId = 0L;
    }

    @Override
    public Solicitud save(Solicitud solicitud) {
        if (solicitud.getId() == null) {
            solicitud.setId(this.nextId);
            solicitud.setFecha(LocalDateTime.now());
            this.solicitudes.add(solicitud);
            this.nextId++;
        } else {
            deleteById(solicitud.getId());
            this.solicitudes.add(solicitud);
        }
        return solicitud;
    }

    @Override
    public Solicitud findById(Long id) {
        for (Solicitud s: solicitudes) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public void delete(Solicitud solicitud) {
        this.solicitudes.remove(solicitud);
    }

    @Override
    public void deleteById(Long id) {
        this.solicitudes = this.solicitudes.stream().filter(s -> !s.getId().equals(id)).toList();
    }

    @Override
    public long count() {
        return this.solicitudes.size();
    }

    @Override
    public List<Solicitud> findAll() {
        return this.solicitudes;
    }

    @Override
    public List<Solicitud> findByMotivo(Motivo motivo) {
        return this.solicitudes.stream().filter(s -> s.getMotivo().equals(motivo)).toList();
    }

}
