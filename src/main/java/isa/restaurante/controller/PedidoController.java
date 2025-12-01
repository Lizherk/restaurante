package isa.restaurante.controller;

import isa.restaurante.modelo.DetallePedido;
import isa.restaurante.modelo.Pedido;
import isa.restaurante.modelo.Producto;
import isa.restaurante.modelo.Cliente;
import isa.restaurante.modelo.Reserva;
import isa.restaurante.modelo.Atender; 
import isa.restaurante.modelo.Empleado; 
import isa.restaurante.service.IDetallePedidoService;
import isa.restaurante.service.IPedidoService;
import isa.restaurante.service.IProductoService;
import isa.restaurante.service.IClienteService;
import isa.restaurante.service.IAtenderService;
import isa.restaurante.service.IReservaService;
import isa.restaurante.service.IEmpleadoService; 
import isa.restaurante.service.IUsuarioService; 
import isa.restaurante.restaurante.service.reporte.GeneradorTicketService; 
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.GrantedAuthority; 
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import jakarta.servlet.http.HttpServletResponse; 
import java.io.IOException; 
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final IPedidoService pedidoService;
    private final IDetallePedidoService detalleService;
    private final IClienteService clienteService;
    private final IProductoService productoService;
    private final IAtenderService atenderService;
    private final IReservaService reservaService; 
    private final GeneradorTicketService generadorTicketService;
    private final IUsuarioService usuarioService; 
    private final IEmpleadoService empleadoService; 

    public PedidoController(IPedidoService pedidoService, IDetallePedidoService detalleService, IClienteService clienteService, IProductoService productoService, IAtenderService atenderService, IReservaService reservaService, GeneradorTicketService generadorTicketService, IUsuarioService usuarioService, IEmpleadoService empleadoService) { 
        this.pedidoService = pedidoService;
        this.detalleService = detalleService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.atenderService = atenderService;
        this.reservaService = reservaService; 
        this.generadorTicketService = generadorTicketService;
        this.usuarioService = usuarioService;
        this.empleadoService = empleadoService;
    }

    public record ItemResumen(String nombre, Integer cantidad) {}
    public record PedidoCard(
            Integer id,
            LocalDate fecha,
            Integer clienteId,
            String clienteNombre,
            Double total,
            String atiendeNombre,
            Integer reservaId,
            Integer mesaId,
            String mesaUbicacion,
            List<ItemResumen> items,
            int itemsCount
    ) {}
   
    @GetMapping({"", "/"})
    public String lista(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta, 
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String q,
            Model model,
            Authentication authentication 
    ) {
        
        List<Pedido> base; 

        Set<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        // 🚨 CORRECCIÓN: Usamos 'ROLE_ADMIN' para coincidir con la BD
        boolean veTodo = roles.contains("ROLE_ADMIN") || 
                         roles.contains("ROLE_CAJERO") || 
                         roles.contains("ROLE_COCINERO");
        
        boolean esMesero = roles.contains("ROLE_MESERO");

        if (veTodo) {
            base = pedidoService.buscarPorFiltros(desde, hasta, clienteId, q);
            model.addAttribute("esAdminOCajero", true); 
        } else if (esMesero) {
            String username = authentication.getName();
            Empleado empleado = empleadoService.buscarPorClave(username);
            
            if (empleado != null) {
                base = pedidoService.buscarPorEmpleadoAsignado(empleado.getId()); 
            } else {
                base = List.of(); 
            }
            model.addAttribute("esAdminOCajero", false); 
        } else {
            base = List.of();
            model.addAttribute("esAdminOCajero", false);
        }

        List<PedidoCard> cards = base.stream().map(p -> {
            List<DetallePedido> dets = detalleService.buscarPorPedido(p.getId());
            List<ItemResumen> items = dets.stream()
                    .map(d -> new ItemResumen(
                            d.getProducto() != null ? Optional.ofNullable(d.getProducto().getNombre()).orElse("Producto") : "Producto (eliminado)",
                            Optional.ofNullable(d.getCantidad()).orElse(0)))
                    .limit(3)
                    .collect(Collectors.toList());
            int itemsCount = dets.size();
            String atiendeNombre = p.getAtenciones().stream()
                .findFirst()
                .map(a -> a.getEmpleado() != null ? a.getEmpleado().getNombreCompleto() : null)
                .orElse("Sin asignar");
            Integer rid = Optional.ofNullable(p.getReserva()).map(Reserva::getId).orElse(null);
            Integer mid = null;
            String mub = null;
            if (p.getReserva() != null && p.getReserva().getMesa() != null) {
                mid = p.getReserva().getMesa().getId();
                mub = p.getReserva().getMesa().getUbicacion();
            }
            Integer cid = Optional.ofNullable(p.getCliente()).map(Cliente::getId).orElse(null);
            String cn = (p.getCliente() != null) ? ((Optional.ofNullable(p.getCliente().getNombre()).orElse("")) + " " + (Optional.ofNullable(p.getCliente().getApellido()).orElse(""))).trim() : "-";
            String clienteMostrar = (cn == null || cn.isBlank()) ? (cid != null ? ("#" + cid) : "-") : cn;
            return new PedidoCard(
                    p.getId(), p.getFecha(), cid, clienteMostrar,
                    Optional.ofNullable(p.getTotal()).orElse(0.0),
                    atiendeNombre, rid, mid, mub, items, itemsCount
            );
        }).sorted(Comparator.comparing(PedidoCard::id).reversed()).collect(Collectors.toList());

        model.addAttribute("cards", cards);
        model.addAttribute("clientes", clienteService.buscarTodosClientes());
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("q", q);
        return "pedidos/lista";
    }

    // 🚨 PROTEGIDO: Cocinero NO puede crear. ADMIN actualizado.
    @GetMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'MESERO')")
    public String crear(@RequestParam(required = false) Integer reservaId, 
                        @RequestParam(required = false) Integer clienteId, 
                        RedirectAttributes ra, Model model) {
        
        Pedido pedido = new Pedido();
        Reserva reservaActiva = null;
        LocalDate hoy = LocalDate.now(); 
        if (clienteId != null) {
            Cliente cliente = clienteService.buscarPorIdCliente(clienteId);
            if (cliente != null) {
                pedido.setCliente(cliente);
                reservaActiva = reservaService.buscarReservaConfirmadaPorCliente(clienteId);
                if (reservaActiva != null && reservaActiva.getFecha().equals(hoy)) {
                    pedido.setReserva(reservaActiva); 
                }
            }
        } else if (reservaId != null) {
            reservaActiva = reservaService.buscarPorId(reservaId);
            if (reservaActiva != null) {
                pedido.setReserva(reservaActiva);
                pedido.setCliente(reservaActiva.getCliente());
            }
        }
        if (pedido.getCliente() != null) {
             model.addAttribute("clienteFijo", pedido.getCliente());
        }
        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteService.buscarTodosClientes());
        return "pedidos/formulario";
    }

    // 🚨 PROTEGIDO: Cocinero NO puede guardar. ADMIN actualizado.
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'MESERO')")
    public String guardar(@ModelAttribute Pedido pedido, Authentication authentication) {
        if (pedido.getId() == null && pedido.getTotal() == null) {
            pedido.setTotal(0.0);
        }
        
        Pedido guardado = pedidoService.guardar(pedido);

        if (pedido.getId() == null && authentication != null) { 
            autoAsignarPedidoAEmpleado(authentication, guardado);
        }
        
        return "redirect:/pedidos/ver/" + guardado.getId();
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id);
        if (pedido == null) return "redirect:/pedidos";
        model.addAttribute("pedido", pedido);
        model.addAttribute("detalles", detalleService.buscarPorPedido(id));
        model.addAttribute("asignaciones", atenderService.buscarPorPedidoId(id));
        return "pedidos/detalle";
    }

    // 🚨 PROTEGIDO: Cocinero NO puede eliminar. ADMIN actualizado.
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'MESERO')")
    public String eliminar(@PathVariable Integer id) {
        pedidoService.eliminar(id);
        return "redirect:/pedidos";
    }

    @GetMapping("/seleccionar")
    public String seleccionarPedidoParaProducto(@RequestParam Integer productoId, Model model) {
        Producto producto = productoService.buscarPorId(productoId);
        if (producto == null) return "redirect:/menu";
        model.addAttribute("producto", producto);
        model.addAttribute("pedidos", pedidoService.buscarTodos());
        return "pedidos/seleccionar";
    }

    @PostMapping("/seleccionar/continuar")
    public String continuarSeleccion(@RequestParam Integer pedidoId,
                                     @RequestParam Integer productoId) {
        return "redirect:/pedidos/" + pedidoId + "/detalles/crear?productoId=" + productoId;
    }

    // 🚨 PROTEGIDO: Cocinero NO puede crear. ADMIN actualizado.
    @PostMapping("/crear-desde-menu")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'MESERO')")
    @Transactional
    public String crearDesdeMenu(
            @RequestParam Integer clienteId,
            @RequestParam(name = "productoId", required = false) List<Integer> productoIds,
            @RequestParam(name = "cantidad", required = false) List<Integer> cantidades,
            RedirectAttributes ra,
            Authentication authentication
    ) {
        if (productoIds == null || cantidades == null || productoIds.size() != cantidades.size()) {
            return "redirect:/menu";
        }

        Cliente cliente = clienteService.buscarPorIdCliente(clienteId);
        if (cliente == null) {
            ra.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/menu";
        }
        
        Reserva reservaActiva = reservaService.buscarReservaConfirmadaPorCliente(clienteId);
        LocalDate hoy = LocalDate.now();
        
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDate.now());
        pedido.setTotal(0.0);
     
        if (reservaActiva != null && reservaActiva.getFecha().equals(hoy)) {
            pedido.setReserva(reservaActiva); 
        }
        
        pedido = pedidoService.guardar(pedido);

        if (pedido.getReserva() != null) {
            List<Atender> asignacionesReserva = pedido.getReserva().getAtenciones();
            if (!asignacionesReserva.isEmpty()) {
                for (Atender asignacion : asignacionesReserva) {
                    Atender nuevaAsignacion = new Atender();
                    nuevaAsignacion.setEmpleado(asignacion.getEmpleado()); 
                    nuevaAsignacion.setPedido(pedido); 
                    atenderService.guardar(nuevaAsignacion); 
                }
            }
        } else {
            autoAsignarPedidoAEmpleado(authentication, pedido);
        }
   
        double total = 0.0;
        for (int i = 0; i < productoIds.size(); i++) {
            Integer pid = productoIds.get(i);
            Integer qty = Optional.ofNullable(cantidades.get(i)).orElse(0);
            if (qty == null || qty <= 0) continue;
            Producto prod = productoService.buscarPorId(pid);
            if (prod == null) continue;
            DetallePedido det = new DetallePedido();
            det.setPedido(pedido);
            det.setProducto(prod);
            det.setCantidad(qty);
            det.setPrecioUnitario(prod.getPrecio());
            det.setSubtotal(prod.getPrecio() * qty);
            detalleService.guardar(det);
            total += det.getSubtotal();
        }

        if (total == 0.0) {
            pedidoService.eliminar(pedido.getId());
            ra.addFlashAttribute("error", "No se seleccionó ningún producto válido.");
            return "redirect:/menu";
        }

        pedido.setTotal(total);
        pedidoService.guardar(pedido);

        return "redirect:/pedidos/ver/" + pedido.getId();
    }
    
    @GetMapping("/exportar/{id}")
    public void exportarPdf(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Pedido pedido = pedidoService.buscarPorId(id);
        if (pedido == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Pedido no encontrado.");
            return;
        }
        String nombreArchivo = "ticket_pedido_" + id + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);
        generadorTicketService.exportar(pedido, response);
    }
    
    private void autoAsignarPedidoAEmpleado(Authentication authentication, Pedido pedido) {
        if (authentication == null) return; 

        Set<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        // ADMIN actualizado aquí también
        if (roles.contains("ROLE_MESERO") || roles.contains("ROLE_CAJERO") || roles.contains("ROLE_ADMIN")) {
            String username = authentication.getName();
            Empleado empleado = empleadoService.buscarPorClave(username);

            if (empleado != null) {
                Atender nuevaAsignacion = new Atender();
                nuevaAsignacion.setEmpleado(empleado);
                nuevaAsignacion.setPedido(pedido);
                atenderService.guardar(nuevaAsignacion);
            }
        }
    }
}