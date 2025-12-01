package isa.restaurante.repository;

import isa.restaurante.modelo.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    // Para listar detalles por pedido 
    List<DetallePedido> findByPedido_Id(Integer pedidoId);

 
    void deleteByPedido_Id(Integer pedidoId);
}