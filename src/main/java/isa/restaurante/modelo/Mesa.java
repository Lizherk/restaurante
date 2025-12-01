package isa.restaurante.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "mesa")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false, length = 80)
    private String ubicacion;

    // GET/SET
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    @Override public String toString() {
        return "Mesa{id=%d, capacidad=%d, ubicacion='%s'}".formatted(id, capacidad, ubicacion);
    }
}