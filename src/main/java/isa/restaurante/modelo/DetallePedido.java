package isa.restaurante.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario = 0.0;

    @Column(nullable = false)
    private Double subtotal = 0.0;

   
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idPedido", nullable = false)
    private Pedido pedido;

   
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idProducto", nullable = false)
    private Producto producto;

    // --- Getters y Setters ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", pedido=" + (pedido != null ? pedido.getId() : null) +
                ", producto=" + (producto != null ? producto.getId() : null) +
                '}';
    }
}

