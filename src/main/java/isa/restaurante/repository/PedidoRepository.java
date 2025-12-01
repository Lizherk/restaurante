package isa.restaurante.repository;

import isa.restaurante.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // <-- Asegúrate de importar Param

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    String FETCH_CLAUSE = "SELECT p FROM Pedido p " +
                          "LEFT JOIN FETCH p.cliente c " +
                          "LEFT JOIN FETCH p.reserva r " + 
                          "LEFT JOIN FETCH r.mesa m " +    
                          "LEFT JOIN FETCH p.atenciones a " +
                          "LEFT JOIN FETCH a.empleado e ";  
    
    @Query(FETCH_CLAUSE + "ORDER BY p.id DESC")
    List<Pedido> findAllWithFetch();

    @Query(FETCH_CLAUSE + "WHERE p.fecha BETWEEN :desde AND :hasta")
    List<Pedido> findByFechaBetweenWithFetch(LocalDate desde, LocalDate hasta); 

    @Query(FETCH_CLAUSE + "WHERE c.id = :clienteId")
    List<Pedido> findByClienteIdWithFetch(Integer clienteId);

    @Query(FETCH_CLAUSE + "WHERE p.fecha BETWEEN :desde AND :hasta AND c.id = :clienteId")
    List<Pedido> findByFechaBetweenAndClienteId(LocalDate desde, LocalDate hasta, Integer clienteId);
    
 
    @Query(FETCH_CLAUSE +
           "WHERE lower(concat(coalesce(c.nombre,''),' ',coalesce(c.apellido,''))) " +
           "like lower(concat('%', :q, '%'))")
    List<Pedido> findByClienteNombreLike(String q);
    
    @Query(FETCH_CLAUSE +
           "WHERE p.fecha BETWEEN :desde AND :hasta " +
           "AND lower(concat(coalesce(c.nombre,''),' ',coalesce(c.apellido,''))) " +
           "like lower(concat('%', :q, '%'))")
    List<Pedido> findByFechaBetweenAndClienteNombreLike(LocalDate desde, LocalDate hasta, String q);
    
    @Query(FETCH_CLAUSE +
           "WHERE c.id = :clienteId " +
           "AND lower(concat(coalesce(c.nombre,''),' ',coalesce(c.apellido,''))) " +
           "like lower(concat('%', :q, '%'))")
    List<Pedido> findByClienteIdAndClienteNombreLike(Integer clienteId, String q);
    
    @Query(FETCH_CLAUSE +
           "WHERE p.fecha BETWEEN :desde AND :hasta " +
           "AND c.id = :clienteId " +
           "AND lower(concat(coalesce(c.nombre,''),' ',coalesce(c.apellido,''))) " +
           "like lower(concat('%', :q, '%'))")
    List<Pedido> findByFechaBetweenAndClienteIdAndClienteNombreLike(LocalDate desde, LocalDate hasta, Integer clienteId, String q);
    
    List<Pedido> findByFechaBetween(LocalDate desde, LocalDate hasta);
    List<Pedido> findByCliente_Id(Integer clienteId);
    List<Pedido> findByFechaBetweenAndCliente_Id(LocalDate desde, LocalDate hasta, Integer clienteId);

    // -------------------------------------------------
    // MÉTODO AÑADIDO (Paso 9)
    // -------------------------------------------------
    /**
     * Busca todos los pedidos que están asignados (en la tabla 'atender')
     * a un ID de empleado específico.
     * Esto implementa "Mesero -> Sus Ventas"
     */
    @Query(FETCH_CLAUSE + "WHERE e.id = :empleadoId ORDER BY p.id DESC")
    List<Pedido> findByEmpleadoAsignado(@Param("empleadoId") Integer empleadoId);
}
