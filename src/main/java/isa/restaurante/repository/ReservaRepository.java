package isa.restaurante.repository;

import isa.restaurante.modelo.EstatusReserva;
import isa.restaurante.modelo.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    Reserva findFirstByCliente_IdAndEstatusOrderByFechaDesc(Integer clienteId, EstatusReserva estatus);

    @Query("""
           SELECT r
           FROM Reserva r
           WHERE (:desde IS NULL OR r.fecha >= :desde)
             AND (:hasta IS NULL OR r.fecha <= :hasta)
             AND (:clienteId IS NULL OR r.cliente.id = :clienteId)
             AND (:mesaId IS NULL OR r.mesa.id = :mesaId)
             AND (:estatus IS NULL OR r.estatus = :estatus)
             AND (:capacidadMin IS NULL OR r.mesa.capacidad >= :capacidadMin)
           ORDER BY r.fecha DESC, r.hora DESC, r.id DESC
           """)
    List<Reserva> search(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("clienteId") Integer clienteId,
            @Param("mesaId") Integer mesaId,
            @Param("capacidadMin") Integer capacidadMin,
            @Param("estatus") EstatusReserva estatus
    );

 
    @Query("""
           SELECT (COUNT(r) > 0)
           FROM Reserva r
           WHERE r.mesa.id = :mesaId
             AND r.fecha = :fecha
             AND r.hora  = :hora
             AND (:excluirId IS NULL OR r.id <> :excluirId)
           """)
    boolean existsChoque(
            @Param("mesaId") Integer mesaId,
            @Param("fecha") LocalDate fecha,
            @Param("hora") LocalTime hora,
            @Param("excluirId") Integer excluirId
    );
}


