package isa.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import isa.restaurante.modelo.Perfil;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
    // No se requieren métodos especiales por ahora
	 Perfil findByPerfil(String perfil);
}