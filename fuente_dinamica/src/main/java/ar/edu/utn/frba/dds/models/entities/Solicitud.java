package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.enums.Estado;
import java.time.LocalDateTime;

import ar.edu.utn.frba.dds.models.enums.Motivo;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "solicitudes")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "texto", columnDefinition = "TEXT")
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo")
    private Motivo motivo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hecho_id")
    private Hecho hecho;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    @Builder.Default
    private Estado estado = Estado.PENDIENTE;

    @Column(name = "fecha")
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "supervisor")
    private String supervisor;

    public Boolean estaFundado() {
    return texto.length() >= 500;
  }

    public void rechazar() {
        this.estado = Estado.RECHAZADA; // TODO: Chequear esto.
    }

    public void aceptar() {
        this.estado = Estado.ACEPTADA; // TODO: Chequear esto.
    }
}
