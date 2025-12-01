package isa.restaurante.controller;

import isa.restaurante.modelo.Cliente;
import isa.restaurante.modelo.Empleado; // <-- AÑADIDO
import isa.restaurante.modelo.Producto;
import isa.restaurante.modelo.Usuario;
import isa.restaurante.service.IClienteService; 
import isa.restaurante.service.IEmpleadoService; // <-- AÑADIDO
import isa.restaurante.service.IProductoService;
import isa.restaurante.service.IUsuarioService; 

import org.springframework.security.core.Authentication; 
import org.springframework.security.core.GrantedAuthority; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set; // <-- AÑADIDO
import java.util.stream.Collectors; // <-- AÑADIDO

@Controller
public class InicioController {

    private final IProductoService productoService;
    private final IClienteService clienteService; 
    private final IUsuarioService usuarioService; 
    private final IEmpleadoService empleadoService; // <-- AÑADIDO

    // 🚨 ASEGÚRATE DE QUE TU CONSTRUCTOR INCLUYA TODOS LOS SERVICIOS
    public InicioController(IProductoService productoService,
                            IClienteService clienteService,
                            IUsuarioService usuarioService,
                            IEmpleadoService empleadoService) { 
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        this.empleadoService = empleadoService; 
    }

    @GetMapping({"/", "/inicio", "/home"})
    public String inicio(Model model, Authentication authentication) { 
        
        List<Producto> todosLosProductos = productoService.buscarTodos();
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("destacados", todosLosProductos); 

        // -------------------------------------------------
        // LÓGICA DE NOTIFICACIÓN (Paso 8 y 9)
        // -------------------------------------------------
        if (authentication != null && authentication.isAuthenticated()) {
            
            Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

            boolean esCliente = roles.contains("ROLE_CLIENTE");
            boolean esEmpleado = roles.contains("ROLE_MESERO") || 
                                 roles.contains("ROLE_COCINERO") || 
                                 roles.contains("ROLE_CAJERO") ||
                                 roles.contains("ROLE_SUPERVISOR");

            Usuario usuario = usuarioService.buscarPorUsername(authentication.getName());

            // 1. Lógica de Cliente
            if (esCliente) {
                Optional<Cliente> clienteOpt = clienteService.buscarPorEmail(usuario.getEmail());
                if (clienteOpt.isPresent()) {
                    Cliente cliente = clienteOpt.get();
                    if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
                        model.addAttribute("notificacionCliente", true);
                    }
                }
            } 
            
            // 2. Lógica de Empleado (Esta es la que fallaba)
            if (esEmpleado) {
                // Busca al empleado por 'clave' (que es el 'username')
                Empleado empleado = empleadoService.buscarPorClave(usuario.getUsername());
                
                // Si NO se encuentra (como tu 'Empty set'), muestra la notificación
                if (empleado == null) {
                    model.addAttribute("notificacionEmpleado", true);
                }
            }
        }
        // -------------------------------------------------

        return "inicio"; 
    }
}