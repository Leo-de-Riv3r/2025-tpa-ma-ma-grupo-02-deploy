package ar.edu.utn.frba.dds.estadisticas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EstadisticasApplication {
    public static void main(String[] args) {
        SpringApplication.run(EstadisticasApplication.class, args);
    }
}
