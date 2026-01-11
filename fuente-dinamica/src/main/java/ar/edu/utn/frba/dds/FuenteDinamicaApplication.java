package ar.edu.utn.frba.dds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
@EnableDiscoveryClient
public class FuenteDinamicaApplication {
  public static void main(String[] args) {
    SpringApplication.run(FuenteDinamicaApplication.class, args);
  }
}