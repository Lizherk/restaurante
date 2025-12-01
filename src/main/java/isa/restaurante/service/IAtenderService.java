package isa.restaurante.service;

import isa.restaurante.modelo.Atender;
import java.util.List;
import java.util.Optional; 

public interface IAtenderService {
    List<Atender> buscarTodos();

   
    List<Atender> buscarPorPedidoId(Integer pedidoId);
 

 
    List<Atender> buscarPorReservaId(Integer reservaId);


    Atender guardar(Atender a);
    void eliminar(Integer id);
}