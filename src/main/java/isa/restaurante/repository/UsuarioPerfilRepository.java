package isa.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import isa.restaurante.modelo.UsuarioPerfil;
import isa.restaurante.modelo.UsuarioPerfilId;

@Repository
public interface UsuarioPerfilRepository extends JpaRepository<UsuarioPerfil, UsuarioPerfilId> {
    // La clave es el tipo de clave compuesta (UsuarioPerfilId)
}