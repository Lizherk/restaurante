package isa.restaurante.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "perfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String perfil; 

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    @Override
    public String toString() {
        return "Perfil [id=" + id + ", perfil=" + perfil + "]";
    }
}