package isa.restaurante.controller;

import isa.restaurante.modelo.Perfil;
import isa.restaurante.modelo.Usuario;
import isa.restaurante.service.IUsuarioService;
import isa.restaurante.repository.PerfilRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder; // Importación para encriptar
import org.springframework.web.bind.WebDataBinder;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder; // Inyección para encriptar

    public UsuarioController(IUsuarioService usuarioService,
                             PerfilRepository perfilRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- LISTA (CORREGIDO) ----------
    @GetMapping
    public String listar(Model model) {
        // 🚨 CORRECCIÓN: Cambiado de "lista" a "usuarios" para que coincida con la vista
        model.addAttribute("usuarios", usuarioService.buscarTodos());
        return "usuarios/lista"; // templates/usuarios/lista.html
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Perfil.class, new PropertyEditorSupport() {
            @Override public void setAsText(String text) {
                if (text == null || text.isBlank()) { setValue(null); return; }
                try {
                    Integer id = Integer.parseInt(text);
                    setValue(perfilRepository.findById(id).orElse(null));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("ID de perfil inválido: " + text);
                }
            }
        });
    }

    // ---------- FORM NUEVO / EDITAR ----------
    @GetMapping({"/crear", "/editar/{id}"})
    public String prepararFormulario(@PathVariable Optional<Integer> id, Model model) {
        Usuario u = id.isPresent()
                ? usuarioService.buscarPorId(id.get()).orElse(new Usuario())
                : nuevoUsuario();
        model.addAttribute("usuario", u);
        model.addAttribute("perfilesDisponibles", perfilRepository.findAll());
        return "usuarios/formulario";
    }

    private Usuario nuevoUsuario() {
        Usuario u = new Usuario();
        u.setEstatus(1);
        u.setFechaRegistro(LocalDate.now());
        return u;
    }

    // ---------- GUARDAR (CON ENCRIPTACIÓN) ----------
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                          @RequestParam(required = false) List<Integer> perfilesIds,
                          RedirectAttributes flash) {

        String raw = usuario.getPassword(); // Contraseña en texto plano

        if (usuario.getId() == null) { // CREAR
            if (raw == null || raw.isBlank()) {
                flash.addFlashAttribute("error", "La contraseña es obligatoria.");
                return "redirect:/usuarios/crear";
            }
            // Encripta la nueva contraseña
            usuario.setPassword(passwordEncoder.encode(raw)); // -> {bcrypt}...
            usuario.setFechaRegistro(LocalDate.now());
            if (usuario.getEstatus() == null) usuario.setEstatus(1);
        } else { // EDITAR
            Usuario actual = usuarioService.buscarPorId(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            if (raw == null || raw.isBlank()) {
                // Si no se cambia la contraseña, conserva la existente (ya sea {noop} o {bcrypt})
                usuario.setPassword(actual.getPassword());  
            } else {
                // Si se cambia, encripta la nueva
                usuario.setPassword(passwordEncoder.encode(raw));
            }
            usuario.setFechaRegistro(actual.getFechaRegistro());
        }

        // Manejo de Perfiles
        if (usuario.getPerfiles() != null) usuario.getPerfiles().clear();
        if (perfilesIds != null) {
            for (Integer pid : perfilesIds) {
                perfilRepository.findById(pid).ifPresent(usuario::agregarPerfilAUsuario);
            }
        }

        usuarioService.guardar(usuario);
        flash.addFlashAttribute("success", "Usuario guardado correctamente.");
        return "redirect:/usuarios";
    }

    // ---------- ELIMINAR ----------
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        usuarioService.eliminar(id);
        flash.addFlashAttribute("success", "Usuario eliminado con éxito.");
        return "redirect:/usuarios";
    }
}
