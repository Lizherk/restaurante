package isa.restaurante.service.jpa;

import isa.restaurante.modelo.Pedido;
import isa.restaurante.repository.PedidoRepository;
import isa.restaurante.service.IPedidoService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


import org.hibernate.Hibernate; 

@Service
@Primary
public class PedidoServiceJpa implements IPedidoService {

    private final PedidoRepository repo;

    public PedidoServiceJpa(PedidoRepository repo) {
        this.repo = repo;
    }


    @Override 
    public List<Pedido> buscarTodos() { 
        List<Pedido> pedidos = repo.findAllWithFetch(); 

        for (Pedido pedido : pedidos) {
            Hibernate.initialize(pedido.getAtenciones());
            for (isa.restaurante.modelo.Atender asignacion : pedido.getAtenciones()) {
                Hibernate.initialize(asignacion.getEmpleado());
            }
        }
        return pedidos;
    } 
    
    @Override public Pedido buscarPorId(Integer id) { return repo.findById(id).orElse(null); } 
    @Override public Pedido guardar(Pedido pedido) { return repo.save(pedido); }
    @Override public void eliminar(Integer id) { repo.deleteById(id); }
    
    @Override
    public List<Pedido> buscarPorFiltros(LocalDate desde, LocalDate hasta, Integer clienteId, String q) {
        boolean hasRango   = (desde != null || hasta != null);
        boolean hasCliente = (clienteId != null);
        boolean hasQ       = (q != null && !q.isBlank());
        String needle      = hasQ ? q.trim() : null;

        if (hasRango) {
            if (desde == null) desde = hasta;
            if (hasta == null) hasta = desde;
        }

        List<Pedido> pedidos; 

        if (hasRango && hasCliente && hasQ) {
            pedidos = repo.findByFechaBetweenAndClienteIdAndClienteNombreLike(desde, hasta, clienteId, needle);
        } else if (hasRango && hasCliente) {
            pedidos = repo.findByFechaBetweenAndClienteId(desde, hasta, clienteId); 
        } else if (hasRango && hasQ) {
            pedidos = repo.findByFechaBetweenAndClienteNombreLike(desde, hasta, needle);
        } else if (hasCliente && hasQ) {
            pedidos = repo.findByClienteIdAndClienteNombreLike(clienteId, needle);
        } else if (hasRango) {
            pedidos = repo.findByFechaBetweenWithFetch(desde, hasta); 
        } else if (hasCliente) {
            pedidos = repo.findByClienteIdWithFetch(clienteId); 
        } else if (hasQ) {
            pedidos = repo.findByClienteNombreLike(needle);
        } else {
            pedidos = repo.findAllWithFetch(); 
        }
        
        // (La inicialización de Hibernate aquí es redundante si findAllWithFetch usa FETCH_CLAUSE,
        // pero la mantenemos por consistencia con tu método buscarTodos())
        for (Pedido pedido : pedidos) {
            Hibernate.initialize(pedido.getAtenciones());
            for (isa.restaurante.modelo.Atender asignacion : pedido.getAtenciones()) {
                Hibernate.initialize(asignacion.getEmpleado());
            }
        }
        
        return pedidos; 
    }
    
    // -------------------------------------------------
    // MÉTODO AÑADIDO (Paso 9) - SOLUCIONA EL ERROR
    // -------------------------------------------------
    /**
     * Implementa el método de la interfaz IPedidoService.
     * Llama al repositorio para buscar los pedidos asignados a un empleado.
     */
    @Override
    public List<Pedido> buscarPorEmpleadoAsignado(Integer empleadoId) {
        // La consulta @Query "findByEmpleadoAsignado" en el Repo ya incluye los FETCH
        // así que no necesitamos inicializar Hibernate aquí.
        return repo.findByEmpleadoAsignado(empleadoId);
    }
}

