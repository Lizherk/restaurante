package isa.restaurante.repository;

import isa.restaurante.modelo.Atender;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AtenderRepository extends JpaRepository<Atender, Integer> {

    List<Atender> findByPedido_Id(Integer pedidoId);

   
    List<Atender> findByReserva_Id(Integer reservaId);
}