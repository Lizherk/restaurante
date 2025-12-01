package isa.restaurante.controller;

import isa.restaurante.modelo.Cliente;
import isa.restaurante.modelo.Usuario; 
import isa.restaurante.service.IClienteService;
import isa.restaurante.service.IUsuarioService; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import java.io.IOException; 
import java.util.Optional; 

@Controller
@RequestMapping("/clientes")
public class ClienteController {
	
    private final IClienteService service;
    private final IUsuarioService usuarioService;

    // 🚨 ELIMINAMOS @Value("${restaurante.upload.dir}") - ESTO ERA EL ERROR

    @Autowired
    public ClienteController(IClienteService service, IUsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }
    
    // --- MÉTODOS MI PERFIL ---
    @GetMapping("/completar-perfil")
    public String mostrarFormularioCliente(Model model, Authentication authentication, RedirectAttributes flash) {
        if (authentication == null) return "redirect:/login";
        Usuario usuario = usuarioService.buscarPorUsername(authentication.getName());
        Optional<Cliente> clienteOpt = service.buscarPorEmail(usuario.getEmail());
        if (clienteOpt.isEmpty()) {
            flash.addFlashAttribute("error", "No se encontró tu perfil.");
            return "redirect:/";
        }
        model.addAttribute("cliente", clienteOpt.get());
        return "clientes/completar-perfil";
    }

    @PostMapping("/guardar-perfil")
    public String guardarPerfilCliente(@ModelAttribute Cliente clienteForm, 
                                       @RequestParam(name = "imagenFile", required = false) MultipartFile imagenFile, 
                                       Authentication authentication, 
                                       RedirectAttributes flash) throws IOException { 
        Usuario usuario = usuarioService.buscarPorUsername(authentication.getName());
        Cliente clienteDB = service.buscarPorEmail(usuario.getEmail())
                                .orElseThrow(() -> new IllegalStateException("Cliente no encontrado."));
        
        clienteDB.setNombre(clienteForm.getNombre());
        clienteDB.setApellido(clienteForm.getApellido()); 
        clienteDB.setTelefono(clienteForm.getTelefono());

        // El servicio se encarga de Cloudinary
        service.guardar(clienteDB, imagenFile);
        
        flash.addFlashAttribute("success", "Perfil actualizado.");
        return "redirect:/"; 
    }

    // --- MÉTODOS ADMIN ---
    @GetMapping
    public String lista(Model model) {
        model.addAttribute("clientes", service.buscarTodosClientes());
        return "clientes/listaClientes";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Cliente c = service.buscarPorIdCliente(id);
        if (c == null) return "redirect:/clientes";
        model.addAttribute("cliente", c);
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente,
                          @RequestParam(name = "imagenFile", required = false) MultipartFile file,
                          RedirectAttributes flash) throws IOException {
        if (cliente.getId() != null && (file == null || file.isEmpty())) {
            Cliente existente = service.buscarPorIdCliente(cliente.getId());
            if (existente != null) cliente.setFotoCliente(existente.getFotoCliente());
        }
        service.guardar(cliente, file);
        flash.addFlashAttribute("success", "Cliente guardado.");
        return "redirect:/clientes";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        Cliente c = service.buscarPorIdCliente(id);
        if (c == null) return "redirect:/clientes";
        model.addAttribute("cliente", c);
        return "clientes/detalle";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return "redirect:/clientes";
    } 
}