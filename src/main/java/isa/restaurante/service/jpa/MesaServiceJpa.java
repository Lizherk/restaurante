package isa.restaurante.service.jpa;

import isa.restaurante.modelo.Mesa;
import isa.restaurante.repository.MesaRepository;
import isa.restaurante.service.IMesaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesaServiceJpa implements IMesaService {

    private final MesaRepository repo;

    public MesaServiceJpa(MesaRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Mesa> listar() {
        return repo.findAll();
    }

    @Override
    public Mesa buscarPorId(Integer id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Mesa guardar(Mesa mesa) {
        return repo.save(mesa);
    }

    @Override
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public List<Mesa> buscarPorCapacidadMin(Integer minCapacidad) {
        return repo.findByCapacidadGreaterThanEqual(minCapacidad);
    }

    @Override
    public List<Mesa> buscarPorUbicacionContiene(String q) {
        return repo.findByUbicacionContainingIgnoreCase(q == null ? "" : q);
    }
}
