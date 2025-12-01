package isa.restaurante.service.jpa;

import isa.restaurante.modelo.Atender; 
import isa.restaurante.modelo.Empleado; 
import isa.restaurante.modelo.EstatusReserva;
import isa.restaurante.modelo.Reserva;
import isa.restaurante.repository.ReservaRepository;
import isa.restaurante.service.IAtenderService; 
import isa.restaurante.service.IEmpleadoService; 
import isa.restaurante.service.IReservaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Importante: Necesitas esta importación para inicializar la colección de pedidos
import org.hibernate.Hibernate; 

@Service
@Transactional
public class ReservaServiceJpa implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final IAtenderService atenderService; 
    private final IEmpleadoService empleadoService; 

   
    public ReservaServiceJpa(ReservaRepository reservaRepository,
                             IAtenderService atenderService,
                             IEmpleadoService empleadoService) {
        this.reservaRepository = reservaRepository;
        this.atenderService = atenderService;
        this.empleadoService = empleadoService;
    }
    
    // ... (buscarTodos, buscarPorId sin cambios) ...
    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarTodos() {
        return reservaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva buscarPorId(Integer id) {
        return reservaRepository.findById(id).orElse(null);
    }

    // CORRECCIÓN: Bloquea la eliminación si hay pedidos asociados
    @Override
    public void eliminar(Integer id) {
        if (id != null) {
            Reserva reservaDB = reservaRepository.findById(id).orElse(null);
            
            // Forzar la inicialización de la colección 'pedidos' antes de verificar
            if (reservaDB != null) {
                 Hibernate.initialize(reservaDB.getPedidos()); 
            }
            
            if (reservaDB != null && !reservaDB.getPedidos().isEmpty()) {
                 throw new IllegalStateException("La reserva #" + id + " no puede ser ELIMINADA porque ya tiene pedidos asociados. Debe eliminar los pedidos primero.");
            }
            
            reservaRepository.deleteById(id);
        }
    }
    
 
    @Override
    @Transactional(readOnly = true)
    public Reserva buscarReservaConfirmadaPorCliente(Integer clienteId) {
        return reservaRepository.findFirstByCliente_IdAndEstatusOrderByFechaDesc(
            clienteId, EstatusReserva.CONFIRMADA);
    }
    
    // CORRECCIÓN: Bloquea el cambio a CANCELADA si hay pedidos
    @Override
    public Reserva guardar(Reserva r) {
        if (r == null) return null;

        if (EstatusReserva.CANCELADA.equals(r.getEstatus())) {
            if (r.getId() != null) {
                // 1. Recargar la entidad para inicializar la colección de Pedidos (Lazy Loading)
                Reserva reservaDB = reservaRepository.findById(r.getId()).orElse(null);
                
                if (reservaDB != null) {
                    Hibernate.initialize(reservaDB.getPedidos());
                }
                
                if (reservaDB != null && !reservaDB.getPedidos().isEmpty()) {
                     // 2. Si hay pedidos, BLOQUEAR la cancelación/eliminación.
                     throw new IllegalStateException("La reserva #" + r.getId() + " no puede ser CANCELADA porque ya tiene pedidos asociados. Debe eliminar los pedidos primero.");
                }
                
                // 3. Si no hay pedidos, proceder con la lógica original de eliminar/cancelar
                reservaRepository.deleteById(r.getId());
            }
            return null; 
        }

        
        if (r.getMesa() == null || r.getMesa().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una mesa.");
        }
        if (r.getFecha() == null) {
            throw new IllegalArgumentException("Debe indicar la fecha de la reserva.");
        }
        if (r.getHora() == null) {
            throw new IllegalArgumentException("Debe indicar la hora de la reserva.");
        }

        
        Integer excluirId = r.getId();
        boolean existeChoque = reservaRepository.existsChoque(
                r.getMesa().getId(),
                r.getFecha(),
                r.getHora(),
                excluirId
        );

        if (existeChoque) {
            throw new IllegalStateException("Ya existe una reserva para esa mesa en esa fecha y hora.");
        }

        return reservaRepository.save(r);
    }
    
   
    @Override
    public void guardarAsignacionesDeReserva(Reserva rForm, List<Integer> empleadosIds) {
        if (rForm.getId() == null) return; 

        Reserva reservaDB = reservaRepository.findById(rForm.getId()).orElse(null);
        if (reservaDB == null) return;

       
        reservaDB.getAtenciones().clear(); 

        
        if (empleadosIds != null && !empleadosIds.isEmpty()) {
            for (Integer empId : empleadosIds) {
                Empleado empleado = empleadoService.buscarPorId(empId); 
                if (empleado != null) {
                    Atender nuevaAsignacion = new Atender();
                    nuevaAsignacion.setReserva(reservaDB); 
                    nuevaAsignacion.setEmpleado(empleado);
                    
                   
                    reservaDB.getAtenciones().add(nuevaAsignacion); 
                }
            }
        }
        

        reservaRepository.save(reservaDB);
    }
    

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarPorFiltros(LocalDate desde,
                                          LocalDate hasta,
                                          Integer clienteId,
                                          Integer mesaId,
                                          Integer capacidadMin,
                                          EstatusReserva estatus) {
        return reservaRepository.search(desde, hasta, clienteId, mesaId, capacidadMin, estatus);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeChoqueMesaFechaHora(Integer mesaId, LocalDate fecha, LocalTime hora, Integer excluirId) {
        return reservaRepository.existsChoque(mesaId, fecha, hora, excluirId);
    }
}