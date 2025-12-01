package isa.restaurante.service;

import isa.restaurante.modelo.Mesa;

import java.util.List;

public interface IMesaService {
    List<Mesa> listar();
    Mesa buscarPorId(Integer id);
    Mesa guardar(Mesa mesa);
    void eliminar(Integer id);

    
    List<Mesa> buscarPorCapacidadMin(Integer minCapacidad);
    List<Mesa> buscarPorUbicacionContiene(String q);
}