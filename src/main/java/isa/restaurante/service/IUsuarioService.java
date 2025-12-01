package isa.restaurante.service;

import isa.restaurante.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    List<Usuario> buscarTodos();
    Optional<Usuario> buscarPorId(Integer id);
    void guardar(Usuario usuario);
    void eliminar(Integer id);
    Usuario buscarPorUsername(String username);
}