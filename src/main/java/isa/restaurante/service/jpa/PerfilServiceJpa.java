package isa.restaurante.service.jpa;

import isa.restaurante.modelo.Perfil;
import isa.restaurante.repository.PerfilRepository;
import isa.restaurante.service.IPerfilService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilServiceJpa implements IPerfilService {

    private final PerfilRepository perfilRepo;

    public PerfilServiceJpa(PerfilRepository perfilRepo) {
        this.perfilRepo = perfilRepo;
    }

    @Override
    public List<Perfil> buscarTodos() {
        return perfilRepo.findAll();
    }

    @Override
    public Optional<Perfil> buscarPorId(Integer id) {
        return perfilRepo.findById(id);
    }

    @Override
    public void guardar(Perfil perfil) {
        perfilRepo.save(perfil);
    }

    @Override
    public void eliminar(Integer id) {
        perfilRepo.deleteById(id);
    }
}