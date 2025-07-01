package ar.edu.utn.frba.dds.models.entities.utils;

import ar.edu.utn.frba.dds.models.entities.adapters.CsvReaderAdapter;
import ar.edu.utn.frba.dds.models.entities.FuenteDeDatos;
import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class FuenteEstaticaCsv implements FuenteDeDatos {
    private CsvReaderAdapter csvReaderAdapter;
    private String rutaArchivo;
    private String separadorColumna; // TODO: Esto no es redundante? Tipo, los csv no se separan siempre con comas??

    @Override
    public Set<Hecho> obtenerHechos(Set<FiltroStrategy> criterios) {
        ImportadorHechos importador = new ImportadorHechos(csvReaderAdapter, rutaArchivo, separadorColumna);
        Set<Hecho> hechos = importador.importarHechos();
        return hechos.stream()
                .filter(h -> criterios.stream().allMatch(f -> f.cumpleFiltro(h)))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean eliminarHecho(Hecho hecho) {
        hecho.setEliminado(true);
        return true;
    }
}
