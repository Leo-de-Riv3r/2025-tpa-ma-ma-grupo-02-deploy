package ar.edu.utn.frba.dds.utils;

import ar.edu.utn.frba.dds.adapters.CsvReaderAdapter;
import ar.edu.utn.frba.dds.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.entities.Hecho;
import ar.edu.utn.frba.dds.strategies.FiltroStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class FuenteEstaticaCsv implements FuenteDeDatos {
    private CsvReaderAdapter csvReaderAdapter;
    private String rutaArchivo;
    private String separadorColumna; // TODO: Esto no es redundante? Tipo, los csv no se separan siempre con comas??

    @Override
    public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios) {
        return Set.of();
    }

    @Override
    public boolean eliminarHecho(Hecho hecho) {
        return false;
    }
}
