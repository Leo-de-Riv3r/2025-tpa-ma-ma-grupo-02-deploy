package ar.edu.utn.frba.dds.models.entities.strategies;

import ar.edu.utn.frba.dds.entities.Hecho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FiltroTituloStrategy implements FiltroStrategy {
    private String titulo;

    @Override
    public boolean cumpleFiltro(Hecho hecho) {
        return hecho.getTitulo().equalsIgnoreCase(titulo);
    }
}
