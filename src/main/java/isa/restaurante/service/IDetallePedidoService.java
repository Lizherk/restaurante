package isa.restaurante.service;

import isa.restaurante.modelo.DetallePedido;
import java.util.List;

public interface IDetallePedidoService {

  
    List<DetallePedido> buscarPorPedido(Integer idPedido);


    DetallePedido guardar(DetallePedido detalle);

  
    void eliminar(Integer id);
}
