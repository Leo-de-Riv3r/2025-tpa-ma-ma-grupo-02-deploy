package ar.edu.utn.frba.dds.listeners;

import ar.edu.utn.frba.dds.models.entities.enums.EstadoColeccion;
import ar.edu.utn.frba.dds.models.events.FuentesAProcesarEvent;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.ProcesadorFuentesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ColeccionListener {

    private final ProcesadorFuentesService procesadorFuentesService;
    private final ColeccionService coleccionService;

    public ColeccionListener(ProcesadorFuentesService procesadorFuentesService,
            ColeccionService coleccionService) {
        this.procesadorFuentesService = procesadorFuentesService;
        this.coleccionService = coleccionService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void procesarFuentes(FuentesAProcesarEvent event) {
        if (event.fuenteIds() != null && !event.fuenteIds().isEmpty()) {
            log.info("Iniciando procesamiento paralelo para {} fuentes...", event.fuenteIds().size());

            List<CompletableFuture<Void>> futuros = event.fuenteIds().stream()
                    .map(fuenteId -> procesadorFuentesService.procesarFuenteAsync(fuenteId, event.coleccionId())
                            .exceptionally(ex -> {
                                log.error("Error al procesar fuente {}: {}", fuenteId, ex.getMessage());
                                return null;
                            }))
                    .collect(Collectors.toList());

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futuros.toArray(new CompletableFuture[0]));

            allFutures.thenRun(() -> {
                ejecutarRefresco(event);
            });

        } else if (event.recalcularConsenso()) {
            ejecutarRefresco(event);
        }
    }

    private void ejecutarRefresco(FuentesAProcesarEvent event) {
        log.info("Finalizando flujo de eventos. Iniciando actualización de consensos...");
        try {
            if (event.coleccionId() != null) {
                if (event.recalcularConsenso()) {
                    coleccionService.refrescarHechosCurados(event.coleccionId());
                }
                coleccionService.actualizarEstadoColeccion(event.coleccionId(), EstadoColeccion.DISPONIBLE);
            } else {
                if (event.recalcularConsenso() && event.fuenteIds() != null && !event.fuenteIds().isEmpty()) {
                    log.info("Refresco global: Recalculando consensos para colecciones afectadas.");
                    coleccionService.refrescarColeccionesAfectadas(event.fuenteIds());
                }
            }
        } catch (Exception e) {
            log.error("Error al finalizar procesamiento y recalcular consensos", e);
        }
    }
}