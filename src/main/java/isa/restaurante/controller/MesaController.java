package isa.restaurante.controller;

import isa.restaurante.modelo.Mesa;
import isa.restaurante.service.IMesaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mesas")
public class MesaController {

    private final IMesaService mesaService;

    public MesaController(IMesaService mesaService) {
        this.mesaService = mesaService;
    }

    // LISTA
    @GetMapping({"", "/"})
    public String listar(Model model) {
        model.addAttribute("mesas", mesaService.listar());
        return "mesas/lista";
    }

    // FORM NUEVO
    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("mesa", new Mesa());
        return "mesas/formulario";
    }

    // FORM EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Mesa m = mesaService.buscarPorId(id);
        if (m == null) return "redirect:/mesas";
        model.addAttribute("mesa", m);
        return "mesas/formulario";
    }

    // GUARDAR
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Mesa mesa) {
        Mesa guardada = mesaService.guardar(mesa);
        return "redirect:/mesas/ver/" + guardada.getId();
    }

    // DETALLE
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        Mesa m = mesaService.buscarPorId(id);
        if (m == null) return "redirect:/mesas";
        model.addAttribute("mesa", m);
        return "mesas/detalle";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        mesaService.eliminar(id);
        return "redirect:/mesas";
    }
}