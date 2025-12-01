package isa.restaurante.service;

import isa.restaurante.modelo.Cliente; 
import isa.restaurante.modelo.EstatusReserva;
import isa.restaurante.modelo.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IReservaService {

    List<Reserva> buscarTodos();
    Reserva buscarPorId(Integer id);
    void eliminar(Integer id);

    
    Reserva guardar(Reserva r);

   
    List<Reserva> buscarPorFiltros(LocalDate desde,
                                   LocalDate hasta,
                                   Integer clienteId,
                                   Integer mesaId,
                                   Integer capacidadMin,
                                   EstatusReserva estatus);


    boolean existeChoqueMesaFechaHora(Integer mesaId, LocalDate fecha, LocalTime hora, Integer excluirId);
   
    void guardarAsignacionesDeReserva(Reserva reserva, List<Integer> empleadosIds);
    
   
    Reserva buscarReservaConfirmadaPorCliente(Integer clienteId);
}