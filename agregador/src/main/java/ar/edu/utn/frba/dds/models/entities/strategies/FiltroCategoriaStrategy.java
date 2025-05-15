package ar.edu.utn.frba.dds.models.entities.strategies;

import ar.edu.utn.frba.dds.entities.Categoria;
import ar.edu.utn.frba.dds.entities.Hecho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FiltroCategoriaStrategy implements FiltroStrategy {
    private Categoria categoria;

    @Override
    public boolean cumpleFiltro(Hecho hecho) {
        return hecho.getCategoria().equals(categoria);
    }
}
