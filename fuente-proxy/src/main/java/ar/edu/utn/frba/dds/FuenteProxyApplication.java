package ar.edu.utn.frba.dds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FuenteProxyApplication {
  public static void main(String[] args) {
    SpringApplication.run(FuenteProxyApplication.class, args);
  }
}