package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.Solicitud;
import ar.edu.utn.frba.dds.models.enums.Motivo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISolicitudesRepository {
    public Solicitud save(Solicitud hecho);
    public Solicitud findById(Long id);
    public void delete(Solicitud hecho);
    public void deleteById(Long id);
    public long count();
    public List<Solicitud> findAll();
    public List<Solicitud> findByMotivo(Motivo motivo);
}
