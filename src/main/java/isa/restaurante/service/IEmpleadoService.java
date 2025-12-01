package isa.restaurante.service;

import isa.restaurante.modelo.Empleado;
import isa.restaurante.modelo.Puesto;

import java.util.List;

public interface IEmpleadoService {
	  public List<Empleado> buscarTodos();
    List<Empleado> listar();
    Empleado buscarPorId(Integer id);
    Empleado guardar(Empleado empleado);
    void eliminar(Integer id);


    List<Empleado> buscarPorPuesto(Puesto puesto);
    List<Empleado> buscarPorNombre(String q);
    Empleado buscarPorClave(String clave);
  
}