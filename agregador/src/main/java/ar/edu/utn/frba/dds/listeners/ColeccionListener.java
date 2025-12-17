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
        if (event.fuenteIds() == null || event.fuenteIds().isEmpty())
            return;

        log.info("Iniciando procesamiento paralelo para colección: {}", event.coleccionId());

        List<CompletableFuture<Void>> futuros = event.fuenteIds().stream()
                .map(fuenteId -> procesadorFuentesService.procesarFuenteAsync(fuenteId, event.coleccionId()))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futuros.toArray(new CompletableFuture[0]));

        allFutures.thenRun(() -> {
            try {
                if (event.coleccionId() != null) {
                    if (event.recalcularConsenso()) {
                        coleccionService.refrescarHechosCurados(event.coleccionId());
                    }
                    coleccionService.actualizarEstadoColeccion(event.coleccionId(), EstadoColeccion.DISPONIBLE);
                }
            } catch (Exception e) {
                log.error("Error al finalizar procesamiento", e);
            }
        });
    }
}