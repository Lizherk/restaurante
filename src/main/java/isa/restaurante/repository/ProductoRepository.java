package isa.restaurante.repository;

import isa.restaurante.modelo.Producto;
import isa.restaurante.modelo.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

   
    List<Producto> findByTipo(TipoProducto tipo);
    List<Producto> findByPrecioBetween(Double min, Double max);
    List<Producto> findByTipoAndPrecioBetween(TipoProducto tipo, Double min, Double max);
    List<Producto> findByNombreContainingIgnoreCase(String q);
    List<Producto> findByTipoOrderByPrecioAsc(TipoProducto tipo);
    List<Producto> findByTipoOrderByPrecioDesc(TipoProducto tipo);

    @Query("""
           SELECT p
           FROM Producto p
           WHERE (:q IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')))
             AND (:tipo IS NULL OR p.tipo = :tipo)
             AND (:min IS NULL OR p.precio >= :min)
             AND (:max IS NULL OR p.precio <= :max)
           ORDER BY p.id DESC
           """)
    List<Producto> searchAdmin(
            @Param("q") String q,
            @Param("tipo") TipoProducto tipo,
            @Param("min") Double min,
            @Param("max") Double max
    );

    
    @Query("""
           SELECT p
           FROM Producto p
           WHERE (:tipo IS NULL OR p.tipo = :tipo)
             AND (:min IS NULL OR p.precio >= :min)
             AND (:max IS NULL OR p.precio <= :max)
             AND (:q IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')))
           ORDER BY p.id DESC
           """)
    List<Producto> searchPublico(
            @Param("tipo") TipoProducto tipo,
            @Param("min") Double min,
            @Param("max") Double max,
            @Param("q") String q
    );
}

