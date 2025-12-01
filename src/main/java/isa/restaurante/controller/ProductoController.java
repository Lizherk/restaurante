package isa.restaurante.controller;

import isa.restaurante.modelo.Producto;
import isa.restaurante.modelo.TipoProducto;
import isa.restaurante.service.IClienteService;
import isa.restaurante.service.IProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/menu")
public class ProductoController {

    private final IProductoService productoService;
    private final IClienteService clienteService;

    public ProductoController(IProductoService productoService,
                              IClienteService clienteService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
    }


    @GetMapping({"", "/"})
    public String lista(
            @RequestParam(required = false) TipoProducto tipo,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean ordenarAsc,
            @RequestParam(required = false) Integer clienteId, 
            Model model
    ) {
        List<Producto> productos = productoService.buscarPublico(tipo, min, max, q, ordenarAsc);

        model.addAttribute("productos", productos);
        model.addAttribute("clientes", clienteService.buscarTodosClientes());
        
       
        model.addAttribute("clienteActivoId", clienteId); 

        
        model.addAttribute("tipoActivo", tipo);
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        model.addAttribute("q", q);
        model.addAttribute("ordenarAsc", ordenarAsc != null ? ordenarAsc : false);

        return "productos/lista";
    }

    
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        Producto p = productoService.buscarPorId(id);
        if (p == null) return "redirect:/menu";
        model.addAttribute("producto", p);
        return "productos/detalle";
    }
}
