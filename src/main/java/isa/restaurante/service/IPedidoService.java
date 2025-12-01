package isa.restaurante.service;

import isa.restaurante.modelo.Pedido;
import java.time.LocalDate;
import java.util.List;

public interface IPedidoService {
    List<Pedido> buscarTodos();
    Pedido buscarPorId(Integer id);
    Pedido guardar(Pedido pedido);
    void eliminar(Integer id);

    List<Pedido> buscarPorFiltros(LocalDate desde, LocalDate hasta, Integer clienteId, String q);
    
    // -------------------------------------------------
    // MÉTODO AÑADIDO (Paso 9)
    // -------------------------------------------------
    /**
     * Busca pedidos asignados a un empleado específico.
     * Implementa "Mesero -> Sus Ventas"
     */
    List<Pedido> buscarPorEmpleadoAsignado(Integer empleadoId);
}