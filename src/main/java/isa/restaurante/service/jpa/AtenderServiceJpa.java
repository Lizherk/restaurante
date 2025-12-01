package isa.restaurante.service.jpa;

import isa.restaurante.modelo.Atender;
import isa.restaurante.repository.AtenderRepository;
import isa.restaurante.service.IAtenderService;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AtenderServiceJpa implements IAtenderService {

    private final AtenderRepository repo;

    public AtenderServiceJpa(AtenderRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Atender> buscarTodos() { return repo.findAll(); }

    @Override
    public List<Atender> buscarPorPedidoId(Integer pedidoId) {
        return repo.findByPedido_Id(pedidoId);
    }


    @Override
    public List<Atender> buscarPorReservaId(Integer reservaId) {
        return repo.findByReserva_Id(reservaId);
    }

  

    @Override
    public Atender guardar(Atender a) { return repo.save(a); }

    @Override
    public void eliminar(Integer id) { repo.deleteById(id); }
}