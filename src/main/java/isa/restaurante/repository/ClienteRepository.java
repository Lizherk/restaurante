package isa.restaurante.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import isa.restaurante.modelo.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends CrudRepository<Cliente, Integer>, JpaRepository<Cliente, Integer> {
	  // 1
    Optional<Cliente> findByNombre(String nombre);

    // 2
    List<Cliente> findByNombreContainingIgnoreCase(String cadena);

    // 3
    Optional<Cliente> findByEmail(String email);

    // 4
    List<Cliente> findByEmailEndingWith(String sufijo); 

    // 5
    List<Cliente> findByCreditoBetween(Double min, Double max);

    // 6
    List<Cliente> findByCreditoGreaterThan(Double monto);

    // 7
    List<Cliente> findByDestacado(Integer destacado);

    // 8
    List<Cliente> findByNombreAndCreditoGreaterThan(String nombre, Double monto);

    // 9
    List<Cliente> findByFotoCliente(String nombreArchivo);

    // 10
    List<Cliente> findByDestacadoAndCreditoGreaterThan(Integer destacado, Double monto);

    // 11
    List<Cliente> findTop5ByOrderByCreditoDesc();
    

}
