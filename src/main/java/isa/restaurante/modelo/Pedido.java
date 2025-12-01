package isa.restaurante.modelo;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha = LocalDate.now();

    private Double total = 0.0;

 
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

   
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "idReserva", nullable = true)
    private Reserva reserva;

    // Detalles
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

  
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Atender> atenciones = new ArrayList<>();

    // ===== Getters / Setters =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    public List<Atender> getAtenciones() { return atenciones; }
    public void setAtenciones(List<Atender> atenciones) { this.atenciones = atenciones; }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", total=" + total +
                ", cliente=" + (cliente != null ? cliente.getId() : null) +
                ", reserva=" + (reserva != null ? reserva.getId() : null) +
                ", detalles=" + (detalles != null ? detalles.size() : 0) +
                ", atenciones=" + (atenciones != null ? atenciones.size() : 0) +
                '}';
    }
}


