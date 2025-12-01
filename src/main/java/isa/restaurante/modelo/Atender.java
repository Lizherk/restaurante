package isa.restaurante.modelo;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "atender")
public class Atender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaAsignacion = LocalDate.now();

    private String observacion;

   
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idEmpleado", nullable = false)
    private Empleado empleado;


    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "idPedido", nullable = true)
    private Pedido pedido;


    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "idReserva", nullable = true)
    private Reserva reserva;

    // ===== Getters / Setters =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

 
    @PrePersist @PreUpdate
    private void validarDestino() {
        if (pedido == null && reserva == null) {
            throw new IllegalStateException("Atender: debe asociarse a un Pedido o a una Reserva.");
        }
    }

    @Override
    public String toString() {
        return "Atender{" +
                "id=" + id +
                ", fechaAsignacion=" + fechaAsignacion +
                ", observacion='" + observacion + '\'' +
                ", empleado=" + (empleado != null ? empleado.getNombreCompleto() : "null") +
                ", pedido=" + (pedido != null ? pedido.getId() : "null") +
                ", reserva=" + (reserva != null ? reserva.getId() : "null") +
                '}';
    }
}
