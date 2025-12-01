package isa.restaurante.modelo;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate fecha;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstatusReserva estatus = EstatusReserva.PENDIENTE;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "idMesa", nullable = false)
    private Mesa mesa;


    
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Atender> atenciones = new ArrayList<>();

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Pedido> pedidos = new ArrayList<>();

    // ===== Getters / Setters =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public EstatusReserva getEstatus() { return estatus; }
    public void setEstatus(EstatusReserva estatus) { this.estatus = estatus; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

   

    public List<Atender> getAtenciones() { return atenciones; }
    public void setAtenciones(List<Atender> atenciones) { this.atenciones = atenciones; }

    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }

   
    @Transient
    public String getAtendientesTexto() {
        if (atenciones != null && !atenciones.isEmpty()) {
            return atenciones.stream()
                    .filter(a -> a.getEmpleado() != null)
                    .map(a -> a.getEmpleado().getNombreCompleto())
                    .distinct()
                    .collect(Collectors.joining(", "));
        }
   
        return "—"; 
    }

    @Override
    public String toString() {
        return "Reserva{id=%d, fecha=%s, hora=%s, estatus=%s, cliente=%s, mesa=%s, atenciones=%d, pedidos=%d}"
                .formatted(
                        id, fecha, hora, estatus,
                        cliente != null ? cliente.getId() : null,
                        mesa != null ? mesa.getId() : null,
                        atenciones != null ? atenciones.size() : 0,
                        pedidos != null ? pedidos.size() : 0
                );
    }
}
