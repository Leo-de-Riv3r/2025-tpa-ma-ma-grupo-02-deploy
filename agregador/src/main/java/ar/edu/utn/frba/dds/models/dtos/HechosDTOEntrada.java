package ar.edu.utn.frba.dds.models.dtos;


//{
//    "id": 10,
//    "titulo": "Evacuaciones por Fenómeno meteorológico con granizo en Salta, Salta",
//    "descripcion": "Severa fenómeno meteorológico con granizo impactó en la localidad de Salta, Salta. El incidente dejando a varios sectores sin comunicación. Las autoridades locales han desplegado equipos de emergencia para atender a los afectados.",
//    "categoria": "Fenómeno meteorológico con granizo",
//    "latitud": -24.77728,
//    "longitud": -65.402076,
//    "fecha_hecho": "2024-03-03T00:00:00.000000Z",
//    "created_at": "2025-05-06T22:14:14.000000Z",
//    "updated_at": "2025-05-06T22:14:14.000000Z"
//    }

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HechosDTOEntrada {
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fecha_hecho;
  private LocalDateTime created_at;
  private LocalDateTime updated_at;
}
