package isa.restaurante.modelo;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "usuarioperfil")
public class UsuarioPerfil implements Serializable {
    
    @EmbeddedId
    private UsuarioPerfilId id = new UsuarioPerfilId(); 

    // Relación al Usuario
    @MapsId("idusuario")
    @ManyToOne
    @JoinColumn(name = "idusuario")
    private Usuario usuario;

    // Relación al Perfil
    @MapsId("idperfil")
    @ManyToOne
    @JoinColumn(name = "idperfil")
    private Perfil perfil;
    
    // Getters and Setters
    public UsuarioPerfilId getId() { return id; }
    public void setId(UsuarioPerfilId id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}