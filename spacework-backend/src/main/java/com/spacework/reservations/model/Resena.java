package com.spacework.reservations.model;

import jakarta.persistence.*;
import com.spacework.crm.model.Usuario;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Resenas", schema = "Reservas")
@Getter
@Setter
@NoArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResenaID")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ReservaID", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UsuarioID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EspacioID", nullable = false)
    private Espacio espacio;

    @Column(name = "Calificacion", nullable = false)
    private Integer calificacion; // 1 to 5

    @Column(name = "Comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "FechaResena", nullable = false)
    private LocalDateTime fechaResena = LocalDateTime.now();

    @Column(name = "Estado", nullable = false)
    private boolean estado = true;

}
