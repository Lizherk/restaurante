package isa.restaurante.service.jpa;

import isa.restaurante.modelo.Usuario;
import isa.restaurante.repository.UsuarioRepository;
import isa.restaurante.service.IUsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceJpa implements IUsuarioService {

    private final UsuarioRepository usuarioRepo;

    public UsuarioServiceJpa(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public List<Usuario> buscarTodos() {
        return usuarioRepo.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepo.findById(id);
    }

    @Override
    public void guardar(Usuario usuario) {
        // En un escenario real, aquí podrías aplicar la lógica de encriptación de contraseña.
        usuarioRepo.save(usuario);
    }

    @Override
    public void eliminar(Integer id) {
        // La configuración CascadeType.ALL y orphanRemoval=true en la entidad Usuario
        // se encarga de eliminar las entradas en usuarioperfil.
        usuarioRepo.deleteById(id);
    }
    
    @Override
    public Usuario buscarPorUsername(String username) {
        return usuarioRepo.findByUsername(username);
    }
}