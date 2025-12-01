package isa.restaurante.modelo;

import jakarta.persistence.*;
import java.util.ArrayList; 
import java.util.List;      

@Entity
@Table(name = "empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String clave;

    @Column(nullable = false, length = 120)
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Puesto puesto; // COCINERO, MESERO, CAJERO

   
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Atender> atenciones = new ArrayList<>();


    // GET/SET
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public Puesto getPuesto() { return puesto; }
    public void setPuesto(Puesto puesto) { this.puesto = puesto; }

  
    public List<Atender> getAtenciones() {
        return atenciones;
    }

    public void setAtenciones(List<Atender> atenciones) {
        this.atenciones = atenciones;
    }

    @Override public String toString() {
        return "Empleado{id=%d, clave='%s', nombreCompleto='%s', puesto=%s}"
                .formatted(id, clave, nombreCompleto, puesto);
    }
}