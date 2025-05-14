package ar.edu.utn.frba.dds.strategies;

import ar.edu.utn.frba.dds.entities.Etiqueta;
import ar.edu.utn.frba.dds.entities.Hecho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class FiltroEtiquetaStrategy implements FiltroStrategy {
    private Set<Etiqueta> etiquetas;

    @Override
    public boolean cumpleFiltro(Hecho hecho) {
        return hecho.getEtiquetas().stream().anyMatch(etiqueta -> etiquetas.contains(etiqueta));
    }
}
