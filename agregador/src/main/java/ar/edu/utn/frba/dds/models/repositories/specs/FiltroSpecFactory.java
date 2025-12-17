package ar.edu.utn.frba.dds.models.repositories.specs;

import ar.edu.utn.frba.dds.models.entities.Hecho;
import ar.edu.utn.frba.dds.models.entities.strategies.FiltroStrategy.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class FiltroSpecFactory {

    public Specification<Hecho> getSpec(IFiltroStrategy filtro) {
        if (filtro instanceof FiltroCategoria f) {
            return HechoSpecs.conCategoria(f.getNombreCategoria());
        } else if (filtro instanceof FiltroProvincia f) {
            return HechoSpecs.enProvincia(f.getProvincia());
        } else if (filtro instanceof FiltroMunicipio f) {
            return HechoSpecs.enMunicipio(f.getMunicipio());
        } else if (filtro instanceof FiltroDepartamento f) {
            return HechoSpecs.enDepartamento(f.getDepartamento());
        } else if (filtro instanceof FiltroFuente f) {
            if (f.getTipoFuente() != null) {
                return HechoSpecs.conTipoFuente(f.getTipoFuente().toString());
            }
        } else if (filtro instanceof FiltroFechaAcontecimiento f) {
            LocalDate inicio = f.getFechaInicio() != null ? f.getFechaInicio().toLocalDate() : null;
            LocalDate fin = f.getFechaFinal() != null ? f.getFechaFinal().toLocalDate() : null;
            return HechoSpecs.fechaAcontecimientoEntre(inicio, fin);
        } else if (filtro instanceof FiltroFechaReporte f) {
            LocalDate inicio = f.getFechaInicio() != null ? f.getFechaInicio().toLocalDate() : null;
            LocalDate fin = f.getFechaFinal() != null ? f.getFechaFinal().toLocalDate() : null;
            return HechoSpecs.fechaReporteEntre(inicio, fin);
        }
        return Specification.where(null);
    }
}