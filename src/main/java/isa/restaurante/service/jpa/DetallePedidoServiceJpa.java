package isa.restaurante.service.jpa;

import isa.restaurante.modelo.DetallePedido;
import isa.restaurante.repository.DetallePedidoRepository;
import isa.restaurante.service.IDetallePedidoService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Service
@Primary
public class DetallePedidoServiceJpa implements IDetallePedidoService {

    @Autowired
    private DetallePedidoRepository repo;

    /**
     */
    @Override
    public List<DetallePedido> buscarPorPedido(Integer idPedido) {
        return repo.findByPedido_Id(idPedido);
    }

    /**
    
     */
    @Override
    public DetallePedido guardar(DetallePedido detalle) {
        return repo.save(detalle);
    }

    /**
   
     */
    @Override
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}



