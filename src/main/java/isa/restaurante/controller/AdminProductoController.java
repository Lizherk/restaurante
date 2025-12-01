package isa.restaurante.controller;

import isa.restaurante.modelo.Producto;
import isa.restaurante.modelo.TipoProducto;
import isa.restaurante.service.IProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    private final IProductoService productoService;

    public AdminProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    // --- LISTADO ---
    @GetMapping({"", "/"})
    public String admin(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) TipoProducto tipo,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(required = false) Boolean ordenarAsc,
            Model model
    ) {
        model.addAttribute("productos",
                productoService.buscarAdmin(nombre, tipo, min, max, ordenarAsc));

        model.addAttribute("tipos",      TipoProducto.values());
        model.addAttribute("nombre",     nombre);
        model.addAttribute("tipo",       tipo);
        model.addAttribute("min",        min);
        model.addAttribute("max",        max);
        model.addAttribute("ordenarAsc", ordenarAsc != null ? ordenarAsc : false);

        return "productos/admin";
    }

    // --- CREAR ---
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("tipos", TipoProducto.values());
        return "productos/formulario";
    }

    // --- EDITAR ---
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Producto p = productoService.buscarPorId(id);
        if (p == null) return "redirect:/admin/productos";
        model.addAttribute("producto", p);
        model.addAttribute("tipos", TipoProducto.values());
        return "productos/formulario";
    }

    // --- GUARDAR (CLOUD) ---
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam(name = "archivo", required = false) MultipartFile archivo,
                          RedirectAttributes flash) {
        
        // Si editamos y no suben foto nueva, mantenemos la anterior
        if(producto.getId() != null && (archivo == null || archivo.isEmpty())){
             Producto existente = productoService.buscarPorId(producto.getId());
             if(existente != null){
                 producto.setFotoProducto(existente.getFotoProducto());
             }
        }

        try {
            // El servicio se encarga de subir a Cloudinary
            productoService.guardar(producto, archivo);
            flash.addFlashAttribute("success", "Producto guardado correctamente.");
            
        } catch (IOException e) {
            flash.addFlashAttribute("error", "Error al subir imagen: " + e.getMessage());
            return "redirect:/admin/productos/crear";
        }
        
        return "redirect:/admin/productos";
    }

    // --- ELIMINAR ---
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        productoService.eliminar(id);
        flash.addFlashAttribute("success", "Producto eliminado.");
        return "redirect:/admin/productos";
    }
}