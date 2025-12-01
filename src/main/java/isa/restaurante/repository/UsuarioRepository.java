package isa.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import isa.restaurante.modelo.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Método para buscar por username, útil para el login
    Usuario findByUsername(String username);
}