package ar.edu.utn.frba.dds.Entities.Filtros;

import ar.edu.utn.frba.dds.Entities.Hecho;

import java.time.LocalDateTime;

public class FiltroFechasStrategy implements FiltroStrategy {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public FiltroFechasStrategy(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    @Override
    public Boolean cumpleFiltro(Hecho hecho) {
        LocalDateTime fecha = hecho.getFechaAcontecimiento();
        return (fecha.isAfter(fechaInicio) || fecha.isEqual(fechaInicio)) &&
                (fecha.isBefore(fechaFin) || fecha.isEqual(fechaFin));
    }
}
