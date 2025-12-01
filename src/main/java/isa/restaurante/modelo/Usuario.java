package isa.restaurante.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50) private String nombre;
    @Column(length = 100, unique = true) private String email;
    @Column(length = 100) private String password; 
    @Column(length = 45, unique = true) private String username; 

    @Column(name = "estatus") private Integer estatus; 
    @Column(name = "fecha_registro") private LocalDate fechaRegistro; 

    // ==========================================================
    // CORRECCIÓN CRÍTICA: Inicializar la lista (Resuelve el NullPointerException)
    // Usamos @ManyToMany simple según el requerimiento del docente.
    // ==========================================================
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "usuarioperfil",
        joinColumns = @JoinColumn(name = "idusuario"),
        inverseJoinColumns = @JoinColumn(name = "idperfil")
    )
    private List<Perfil> perfiles = new LinkedList<>(); // <-- SOLUCIÓN: Inicialización
    // ==========================================================

    // MÉTODO AUXILIAR REQUERIDO
    public void agregarPerfilAUsuario(Perfil tmpPerfil) {
        // La verificación de null ahora es redundante pero segura
        if (perfiles == null) {
            perfiles = new LinkedList<>(); 
        }
        perfiles.add(tmpPerfil);
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getEstatus() { return estatus; }
    public void setEstatus(Integer estatus) { this.estatus = estatus; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public List<Perfil> getPerfiles() { return perfiles; } 
    public void setPerfiles(List<Perfil> perfiles) { this.perfiles = perfiles; }
}