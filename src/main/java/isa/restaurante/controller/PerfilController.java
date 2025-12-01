package isa.restaurante.controller;

import isa.restaurante.modelo.Perfil;
import isa.restaurante.service.IPerfilService;
import org.springframework.dao.DataIntegrityViolationException; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/perfiles")
public class PerfilController {

    private final IPerfilService perfilService;

    public PerfilController(IPerfilService perfilService) {
        this.perfilService = perfilService;
    }

    // LISTAR TODOS (sin cambios)
    @GetMapping({"", "/"})
    public String listar(Model model) {
        List<Perfil> perfiles = perfilService.buscarTodos();
        model.addAttribute("perfiles", perfiles);
        return "perfiles/lista"; 
    }

    // FORMULARIO NUEVO (sin cambios)
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("perfil", new Perfil());
        return "perfiles/formulario";
    }

    // FORMULARIO EDITAR (sin cambios)
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Optional<Perfil> perfil = perfilService.buscarPorId(id);
        if (perfil.isEmpty()) {
            return "redirect:/perfiles";
        }
        model.addAttribute("perfil", perfil.get());
        return "perfiles/formulario";
    }

    // GUARDAR (CREAR / ACTUALIZAR) - CORREGIDO
    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer id, // Recibimos el ID
                          @RequestParam String perfil, // 🚨 CORRECCIÓN: Acepta 'perfil' (no 'nombrePerfil')
                          RedirectAttributes flash) {
        
        try {
            Perfil perfilAGuardar;
            
            if (id != null) {
                // Es edición
                perfilAGuardar = perfilService.buscarPorId(id).orElseThrow(
                    () -> new IllegalArgumentException("Perfil a editar no encontrado."));
                perfilAGuardar.setPerfil(perfil); // Asigna el nuevo nombre
            } else {
                // Es creación
                perfilAGuardar = new Perfil();
                perfilAGuardar.setPerfil(perfil);
            }
            
            perfilService.guardar(perfilAGuardar); 
            flash.addFlashAttribute("success", "Perfil guardado con éxito.");
            
        } catch (DataIntegrityViolationException e) {
            // Error si el nombre del perfil ya existe (UNIQUE constraint)
            flash.addFlashAttribute("error", "El nombre de perfil '" + perfil + "' ya existe.");
            if (id != null) {
                return "redirect:/perfiles/editar/" + id;
            }
            return "redirect:/perfiles/crear";
            
        } catch (Exception e) {
             flash.addFlashAttribute("error", "Error: " + e.getMessage());
             return "redirect:/perfiles";
        }
        return "redirect:/perfiles";
    }

    // ELIMINAR (sin cambios)
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            perfilService.eliminar(id);
            flash.addFlashAttribute("success", "Perfil eliminado con éxito.");
        } catch (Exception e) {
            // Error si el perfil está asignado a un usuario (FK constraint)
            flash.addFlashAttribute("error", "No se pudo eliminar el perfil. Puede estar asociado a un usuario.");
        }
        return "redirect:/perfiles";
    }
}