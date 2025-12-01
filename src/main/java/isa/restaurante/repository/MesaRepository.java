package isa.restaurante.repository;

import isa.restaurante.modelo.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Integer> {
    
    // Este método busca mesas con capacidad >= minCapacidad.
    List<Mesa> findByCapacidadGreaterThanEqual(Integer minCapacidad);
    
    // Búsqueda por ubicación
    List<Mesa> findByUbicacionContainingIgnoreCase(String q);
    
    // [Se eliminó la línea List<Mesa> findByCapacidadGreaterThanEqual(Integer minCapacidad); duplicada]
}