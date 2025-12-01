package isa.restaurante.controller;

import isa.restaurante.modelo.Cliente;
import isa.restaurante.modelo.EstatusReserva;
import isa.restaurante.modelo.Reserva;
import isa.restaurante.modelo.Usuario;
import isa.restaurante.service.IReservaService;
import isa.restaurante.service.IClienteService;
import isa.restaurante.service.IMesaService;
import isa.restaurante.service.IEmpleadoService;
import isa.restaurante.service.IUsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.GrantedAuthority; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final IReservaService reservaService;
    private final IClienteService clienteService;
    private final IMesaService mesaService;
    private final IEmpleadoService empleadoService;
    private final IUsuarioService usuarioService; 

    public ReservaController(IReservaService reservaService,
                             IClienteService clienteService,
                             IMesaService mesaService,
                             IEmpleadoService empleadoService,
                             IUsuarioService usuarioService) {
        this.reservaService = reservaService;
        this.clienteService = clienteService;
        this.mesaService = mesaService;
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
    }

    // LISTAR (Manejo de Filtros Y LÓGICA DE ROL)
    @GetMapping({"", "/"})
    public String listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer clienteId, 
            @RequestParam(required = false) Integer mesaId,
            @RequestParam(required = false) Integer capacidadMin,
            @RequestParam(required = false) EstatusReserva estatus,
            Model model,
            Authentication authentication
    ) {
        
        Integer clienteIdFiltrado = clienteId;

        boolean esCliente = false;
        if (authentication != null) {
            esCliente = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_CLIENTE"));
        }

        if (esCliente) {
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username);
            
            if (usuario != null) {
                Optional<Cliente> clienteOpt = clienteService.buscarPorEmail(usuario.getEmail());
                if (clienteOpt.isPresent()) {
                    clienteIdFiltrado = clienteOpt.get().getId(); 
                } else {
                    model.addAttribute("reservas", List.of());
                    model.addAttribute("error", "Tu usuario de cliente no está completamente configurado.");
                    return "reservas/lista";
                }
            }
        }
        
        Integer capacidadFiltrar = (capacidadMin != null && capacidadMin > 0) ? capacidadMin : null;
        
        List<Reserva> reservas = reservaService.buscarPorFiltros(
            desde, hasta, clienteIdFiltrado, mesaId, capacidadFiltrar, estatus
        );
        
        model.addAttribute("reservas", reservas);
        model.addAttribute("clientes", clienteService.buscarTodosClientes());
        model.addAttribute("mesas", mesaService.listar());
        
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("mesaId", mesaId);
        model.addAttribute("capacidadMin", capacidadMin);
        model.addAttribute("estatus", estatus);

        return "reservas/lista";
    }

    // 🚨 MODIFICADO: CREAR (Restricción de Cliente)
    @GetMapping("/crear")
    public String crear(Model model, Authentication authentication) {
        Reserva reserva = new Reserva();
        List<Cliente> listaClientes;

        // 1. Detectar si es CLIENTE
        boolean esCliente = false;
        if (authentication != null) {
            esCliente = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_CLIENTE"));
        }

        if (esCliente) {
            // 2. Si es Cliente, buscar SOLO su registro
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username);
            Optional<Cliente> clienteOpt = clienteService.buscarPorEmail(usuario.getEmail());

            if (clienteOpt.isPresent()) {
                // La lista solo tiene 1 elemento: ÉL MISMO
                listaClientes = List.of(clienteOpt.get());
                // Pre-seleccionamos al cliente en el objeto reserva
                reserva.setCliente(clienteOpt.get());
            } else {
                listaClientes = List.of(); // Error de configuración de perfil
            }
        } else {
            // 3. Si es Admin o Cajero, ve TODOS los clientes
            listaClientes = clienteService.buscarTodosClientes();
        }

        model.addAttribute("reserva", reserva);
        model.addAttribute("clientes", listaClientes); // Enviamos la lista (filtrada o completa)
        model.addAttribute("mesas", mesaService.listar());
        model.addAttribute("empleados", empleadoService.buscarTodos());
        return "reservas/formulario";
    }

    // EDITAR - CRUD Admin para cambiar el estatus
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Reserva r = reservaService.buscarPorId(id);
        if (r == null) return "redirect:/reservas";
        
        model.addAttribute("reserva", r);
        model.addAttribute("clientes", clienteService.buscarTodosClientes());
        model.addAttribute("mesas", mesaService.listar());
        model.addAttribute("empleados", empleadoService.buscarTodos());
        model.addAttribute("estatusLista", EstatusReserva.values());
        
        return "reservas/formulario";
    }

    // VER (sin cambios)
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        Reserva r = reservaService.buscarPorId(id);
        if (r == null) return "redirect:/reservas";
        model.addAttribute("reserva", r);
        return "reservas/detalle";
    }


    // GUARDAR (crear/editar)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Reserva rForm, 
                          @RequestParam(name = "empleadosIds", required = false) List<Integer> empleadosIds, 
                          RedirectAttributes flash, 
                          Model model) {
        
        Reserva reservaAguardar; 
        Reserva reservaParaRecarga = rForm; 

        try {
            if (rForm.getId() != null) {
                Reserva reservaExistente = reservaService.buscarPorId(rForm.getId());
                if (reservaExistente == null) {
                     flash.addFlashAttribute("error", "Reserva a editar no encontrada.");
                     return "redirect:/reservas";
                }
                reservaExistente.setEstatus(rForm.getEstatus()); 
                reservaAguardar = reservaExistente;

                if (EstatusReserva.CONFIRMADA.equals(rForm.getEstatus())) {
                    LocalDate hoy = LocalDate.now();
                    if (!reservaAguardar.getFecha().equals(hoy)) {
                        String mensaje = String.format("La reserva solo puede ser CONFIRMADA si su fecha es HOY (%s). La fecha de la reserva es: %s", 
                                                        hoy, reservaAguardar.getFecha());
                        throw new IllegalStateException(mensaje); 
                    }
                }

            } else {
                rForm.setEstatus(EstatusReserva.PENDIENTE);
                reservaAguardar = rForm;
            }
            
            if (reservaAguardar.getFecha() != null && reservaAguardar.getFecha().isBefore(LocalDate.now())) {
                 String mensaje = String.format("No se puede agendar una reserva para una fecha pasada: %s.", reservaAguardar.getFecha());
                 throw new IllegalArgumentException(mensaje);
            }
           
            Integer excluirId = reservaAguardar.getId();
            Integer mesaId = Optional.ofNullable(reservaAguardar.getMesa()).map(m -> m.getId()).orElse(null);
            
            if (mesaId != null && reservaAguardar.getFecha() != null && reservaAguardar.getHora() != null) {
                boolean choque = reservaService.existeChoqueMesaFechaHora(mesaId, reservaAguardar.getFecha(), reservaAguardar.getHora(), excluirId);
                
                if (choque && reservaAguardar.getEstatus() != EstatusReserva.CANCELADA) {
                    model.addAttribute("error", "Ya existe una reserva para esa mesa en la misma fecha y hora.");
                    reservaParaRecarga = reservaAguardar; 
                    throw new IllegalStateException("Choque de reserva detectado."); 
                }
            }

            Reserva guardada = reservaService.guardar(reservaAguardar);

            if (guardada == null) { 
                flash.addFlashAttribute("ok", "Reserva cancelada y eliminada.");
                return "redirect:/reservas";
            }
            
            reservaService.guardarAsignacionesDeReserva(guardada, empleadosIds);

            if (EstatusReserva.CONFIRMADA.equals(guardada.getEstatus())) {
                flash.addFlashAttribute("ok", "Reserva Confirmada. Proceda a tomar el pedido desde el menú.");
                Integer clienteId = guardada.getCliente() != null ? guardada.getCliente().getId() : null;
                if (clienteId != null) {
                    return "redirect:/menu?clienteId=" + clienteId; 
                }
                return "redirect:/menu"; 
            }

            flash.addFlashAttribute("ok", "Reserva guardada correctamente.");
            return "redirect:/reservas/ver/" + guardada.getId();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("reserva", reservaParaRecarga); 
            // En caso de error, volver a cargar la lista correcta (filtrada o no)
            // Para simplificar, recargamos todos, pero en producción podrías replicar la lógica de 'crear'
            model.addAttribute("clientes", clienteService.buscarTodosClientes()); 
            model.addAttribute("mesas", mesaService.listar());
            model.addAttribute("empleados", empleadoService.buscarTodos());
            
            if (reservaParaRecarga.getId() != null || ex instanceof IllegalStateException) { 
                model.addAttribute("estatusLista", EstatusReserva.values());
            }
            return "reservas/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        reservaService.eliminar(id);
        flash.addFlashAttribute("ok", "Reserva eliminada.");
        return "redirect:/reservas";
    }
}