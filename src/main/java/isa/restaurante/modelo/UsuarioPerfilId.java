package isa.restaurante.modelo;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;

@Embeddable
public class UsuarioPerfilId implements Serializable {

    @Column(name = "idusuario")
    private Integer idusuario;

    @Column(name = "idperfil")
    private Integer idperfil;

    // Constructores, Getters, Setters y métodos hashCode/equals
    public UsuarioPerfilId() {}

    public UsuarioPerfilId(Integer idusuario, Integer idperfil) {
        this.idusuario = idusuario;
        this.idperfil = idperfil;
    }

    public Integer getIdusuario() { return idusuario; }
    public void setIdusuario(Integer idusuario) { this.idusuario = idusuario; }
    public Integer getIdperfil() { return idperfil; }
    public void setIdperfil(Integer idperfil) { this.idperfil = idperfil; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioPerfilId that = (UsuarioPerfilId) o;
        return Objects.equals(idusuario, that.idusuario) &&
               Objects.equals(idperfil, that.idperfil);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idusuario, idperfil);
    }
}