package isa.restaurante.controller;

import isa.restaurante.modelo.Atender;
import isa.restaurante.service.IAtenderService;
import isa.restaurante.service.IEmpleadoService;
import isa.restaurante.service.IPedidoService;
import isa.restaurante.service.IReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/atender")
public class AtenderController {

    // CORRECCIÓN 1: Inyección por Constructor (Mejor Práctica)
    private final IAtenderService atenderService;
    private final IEmpleadoService empleadoService;
    private final IPedidoService pedidoService;
    private final IReservaService reservaService;

    public AtenderController(IAtenderService atenderService, 
                             IEmpleadoService empleadoService, 
                             IPedidoService pedidoService, 
                             IReservaService reservaService) {
        this.atenderService = atenderService;
        this.empleadoService = empleadoService;
        this.pedidoService = pedidoService;
        this.reservaService = reservaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("asignaciones", atenderService.buscarTodos());
        return "atender/lista";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("asignacion", new Atender());
        model.addAttribute("empleados", empleadoService.buscarTodos());
        model.addAttribute("pedidos", pedidoService.buscarTodos());
        model.addAttribute("reservas", reservaService.buscarTodos());
        return "atender/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Atender atender, Model model) {
        
  
        boolean hasPedido = atender.getPedido() != null && atender.getPedido().getId() != null;
        boolean hasReserva = atender.getReserva() != null && atender.getReserva().getId() != null;
        
        if (hasPedido == hasReserva) { 
            model.addAttribute("asignacion", atender);
            model.addAttribute("empleados", empleadoService.buscarTodos());
            model.addAttribute("pedidos", pedidoService.buscarTodos());
            model.addAttribute("reservas", reservaService.buscarTodos());
            model.addAttribute("error", "Debes seleccionar Pedido o Reserva (solo uno).");
            return "atender/formulario";
        }

        atenderService.guardar(atender);
        return "redirect:/atender";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        atenderService.eliminar(id);
        return "redirect:/atender";
    }
}

