package isa.restaurante.controller;

import isa.restaurante.modelo.Cliente;
import isa.restaurante.modelo.Empleado;
import isa.restaurante.modelo.Puesto;
import isa.restaurante.modelo.Usuario;
import isa.restaurante.service.IClienteService; // 1. Importar servicio de clientes
import isa.restaurante.service.IEmpleadoService;
import isa.restaurante.service.IUsuarioService;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException; // Para manejar errores si tiene historial
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final IEmpleadoService empleadoService;
    private final IUsuarioService usuarioService;
    private final IClienteService clienteService; // 2. Inyección del servicio

    public EmpleadoController(IEmpleadoService empleadoService, 
                              IUsuarioService usuarioService,
                              IClienteService clienteService) { // 3. Constructor actualizado
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
    }

    // -------------------------------------------------
    // MÉTODOS DE "MI PERFIL" (Actualizado con limpieza)
    // -------------------------------------------------

    @GetMapping("/completar-perfil")
    public String mostrarFormularioEmpleado(Model model, Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        
        Usuario usuario = usuarioService.buscarPorUsername(authentication.getName());
        Empleado empleado = empleadoService.buscarPorClave(usuario.getUsername());
        
        if (empleado == null) {
            empleado = new Empleado();
            empleado.setClave(usuario.getUsername()); 
            empleado.setNombreCompleto(usuario.getNombre());
        }

        model.addAttribute("empleado", empleado);
        model.addAttribute("puestos", Puesto.values());
        return "empleados/completar-perfil";
    }

    /**
     * Guarda los datos del EMPLEADO y elimina el registro residual de CLIENTE si existe.
     */
    @PostMapping("/guardar-perfil")
    public String guardarPerfilEmpleado(@ModelAttribute Empleado empleadoForm, 
                                        Authentication authentication, 
                                        RedirectAttributes flash) {
        
        Usuario usuario = usuarioService.buscarPorUsername(authentication.getName());
        
        // 1. Guardar/Actualizar Empleado
        Empleado empleadoDB = empleadoService.buscarPorClave(usuario.getUsername());

        if (empleadoDB == null) {
            empleadoDB = empleadoForm;
            empleadoDB.setClave(usuario.getUsername()); 
        } else {
            empleadoDB.setNombreCompleto(empleadoForm.getNombreCompleto());
            empleadoDB.setPuesto(empleadoForm.getPuesto());
        }

        empleadoService.guardar(empleadoDB);
        
        // -------------------------------------------------------------------
        // 2. LIMPIEZA DE DATOS RESIDUALES (Borrar registro de Cliente inicial)
        // -------------------------------------------------------------------
        Optional<Cliente> clienteResidual = clienteService.buscarPorEmail(usuario.getEmail());
        
        if (clienteResidual.isPresent()) {
            try {
                // Intentamos eliminar el registro de cliente sobrante
                clienteService.eliminar(clienteResidual.get().getId());
                System.out.println("INFO: Se eliminó el registro residual de Cliente para el usuario: " + usuario.getUsername());
            } catch (DataIntegrityViolationException e) {
                // Si falla es porque el cliente ya tiene historial (Pedidos/Reservas).
                // En ese caso, NO lo borramos para proteger la integridad de los datos históricos.
                System.out.println("WARN: No se borró el cliente residual porque tiene historial de ventas/reservas.");
            } catch (Exception e) {
                System.out.println("WARN: Error intentando limpiar cliente residual: " + e.getMessage());
            }
        }
        // -------------------------------------------------------------------
        
        flash.addFlashAttribute("success", "¡Perfil de Empleado actualizado con éxito!");
        return "redirect:/"; 
    }


    // -------------------------------------------------
    // MÉTODOS DE ADMIN (Sin cambios)
    // -------------------------------------------------

    @GetMapping({"", "/"})
    public String listar(@RequestParam(required = false) Puesto puesto,
                         @RequestParam(required = false) String q,
                         Model model) {
        if (puesto != null) {
            model.addAttribute("empleados", empleadoService.buscarPorPuesto(puesto));
        } else if (q != null && !q.isBlank()) {
            model.addAttribute("empleados", empleadoService.buscarPorNombre(q));
        } else {
            model.addAttribute("empleados", empleadoService.listar());
        }
        model.addAttribute("puestos", Puesto.values());
        model.addAttribute("puestoActivo", puesto);
        model.addAttribute("q", q);
        return "empleados/lista";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("empleado", new Empleado());
        model.addAttribute("puestos", Puesto.values());
        return "empleados/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Empleado e = empleadoService.buscarPorId(id);
        if (e == null) return "redirect:/empleados";
        model.addAttribute("empleado", e);
        model.addAttribute("puestos", Puesto.values());
        return "empleados/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Empleado empleado) {
        Empleado guardado = empleadoService.guardar(empleado);
        return "redirect:/empleados/ver/" + guardado.getId();
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        Empleado e = empleadoService.buscarPorId(id);
        if (e == null) return "redirect:/empleados";
        model.addAttribute("empleado", e);
        return "empleados/detalle";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        empleadoService.eliminar(id);
        return "redirect:/empleados";
    }
    
    @GetMapping("/buscar-clave")
    public String buscarPorClave(@RequestParam String clave, Model model) {
        if (clave == null || clave.isBlank()) {
            return "redirect:/empleados";
        }
        var e = empleadoService.buscarPorClave(clave);
        model.addAttribute("empleados", e != null ? List.of(e) : List.of());
        model.addAttribute("puestos", Puesto.values());
        model.addAttribute("q", clave); 
        model.addAttribute("puestoActivo", null);
        return "empleados/lista";
    }
}