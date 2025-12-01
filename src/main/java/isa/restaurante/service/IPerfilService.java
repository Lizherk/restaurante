package isa.restaurante.service;

import isa.restaurante.modelo.Perfil;
import java.util.List;
import java.util.Optional;

public interface IPerfilService {
    List<Perfil> buscarTodos();
    Optional<Perfil> buscarPorId(Integer id);
    void guardar(Perfil perfil);
    void eliminar(Integer id);
}