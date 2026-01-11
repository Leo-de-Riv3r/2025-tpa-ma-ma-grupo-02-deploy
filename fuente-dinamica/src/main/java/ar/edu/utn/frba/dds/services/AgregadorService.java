package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.client.AgregadorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class AgregadorService {
    private final AgregadorClient agregadorClient;

    public AgregadorService(AgregadorClient agregadorClient) {
      this.agregadorClient = agregadorClient;
    }

    @Async
    public void notificarCambios() {
        log.info("NOTIFICADOR: Enviando señal a agregador.");

        try {
            agregadorClient.refrescarDinamica();
            log.info("NOTIFICADOR: Señal enviada con éxito.");
        } catch (Exception e) {
            log.error("NOTIFICADOR: Falló el intento.", e);
        }
    }
}