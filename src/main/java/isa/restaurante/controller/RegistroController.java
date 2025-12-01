package isa.restaurante.controller;

import isa.restaurante.modelo.Cliente; // <-- AÑADIDO
import isa.restaurante.modelo.Perfil;
import isa.restaurante.modelo.Usuario;
import isa.restaurante.repository.PerfilRepository;
import isa.restaurante.service.IClienteService; // <-- AÑADIDO
import isa.restaurante.service.IUsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final IUsuarioService usuarioService;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final IClienteService clienteService; // <-- AÑADIDO

    public RegistroController(IUsuarioService usuarioService,
                              PerfilRepository perfilRepository,
                              PasswordEncoder passwordEncoder,
                              IClienteService clienteService) { // <-- AÑADIDO
        this.usuarioService = usuarioService;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteService = clienteService; // <-- AÑADIDO
    }

    /**
     * Muestra el formulario de registro público.
     */
    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; // -> registro.html
    }

    /**
     * Procesa el registro de un nuevo usuario.
     * Rol por defecto: CLIENTE.
     * ACCIÓN AÑADIDA: Crea la entidad Cliente.
     */
    @PostMapping
    public String procesarRegistro(Usuario usuario, RedirectAttributes flash) {

        // 1. Validar que el username no exista
        Usuario existente = usuarioService.buscarPorUsername(usuario.getUsername());
        if (existente != null) {
            flash.addFlashAttribute("error", "El nombre de usuario ya existe. Por favor, elige otro.");
            return "redirect:/registro";
        }
        
        // 1. (Opcional) Validar que el email no exista en Clientes
        if (clienteService.buscarPorEmail(usuario.getEmail()).isPresent()) {
             flash.addFlashAttribute("error", "El email ya está registrado. Por favor, inicia sesión.");
            return "redirect:/login";
        }

        // 2. Buscar el perfil "CLIENTE"
        Perfil perfilCliente = perfilRepository.findByPerfil("CLIENTE");
        if (perfilCliente == null) {
            flash.addFlashAttribute("error", "Error crítico: El perfil 'CLIENTE' no existe en la base de datos.");
            return "redirect:/registro";
        }

        // 3. Encriptar contraseña (BCrypt)
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada); // -> {bcrypt}$2a...

        // 4. Settear datos por defecto
        usuario.setEstatus(1); // Activo
        usuario.setFechaRegistro(LocalDate.now());

        // 5. Asignar el rol CLIENTE
        usuario.agregarPerfilAUsuario(perfilCliente);

        // 6. Guardar el Usuario (para el login)
        usuarioService.guardar(usuario);
        
        // -------------------------------------------------
        // PASO 6: CREAR LA ENTIDAD CLIENTE (para reservas/pedidos)
        // -------------------------------------------------
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(usuario.getNombre());
        nuevoCliente.setEmail(usuario.getEmail());
        // Setteamos valores por defecto (como dijiste, "completar datos" después)
        nuevoCliente.setApellido(""); // El admin o el cliente lo completarán después
        nuevoCliente.setTelefono("");
        nuevoCliente.setCredito(0.0);
        nuevoCliente.setDestacado(0);
        
        clienteService.guardar(nuevoCliente);
        // -------------------------------------------------

        flash.addFlashAttribute("success", "¡Registro exitoso! Ahora puedes iniciar sesión.");
        return "redirect:/login"; // Redirige al login
    }
}