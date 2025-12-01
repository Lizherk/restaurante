package isa.restaurante.repository;

import isa.restaurante.modelo.Empleado;
import isa.restaurante.modelo.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

    List<Empleado> findByPuesto(Puesto puesto);

    List<Empleado> findByNombreCompletoContainingIgnoreCase(String q);

    Optional<Empleado> findByClave(String clave);
    
}