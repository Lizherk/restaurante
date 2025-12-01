package isa.restaurante.controller;
import isa.restaurante.modelo.Cliente;
import isa.restaurante.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/busquedas/clientes")
public class ClienteBusquedaController {
	 @Autowired
	    private IClienteService service;

	    // base
	    @GetMapping({"", "/"})
	    public String vista(Model model) {
	        model.addAttribute("resultado", List.of());
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/por-nombre")
	    public String porNombre(@RequestParam String nombre, Model model) {
	        model.addAttribute("resultado",
	            service.buscarPorNombre(nombre).map(List::of).orElse(List.of()));
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/nombre-contiene")
	    public String nombreContiene(@RequestParam String q, Model model) {
	        model.addAttribute("resultado", service.buscarNombreContiene(q));
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/email-exacto")
	    public String emailExacto(@RequestParam String email, Model model) {
	        model.addAttribute("resultado",
	            service.buscarPorEmail(email).map(List::of).orElse(List.of()));
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/email-gmail")
	    public String emailGmail(Model model) {
	        model.addAttribute("resultado", service.buscarEmailGmail());
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/credito-entre")
	    public String creditoEntre(@RequestParam Double min, @RequestParam Double max, Model model) {
	        model.addAttribute("resultado", service.buscarCreditoEntre(min, max));
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/credito-mayor")
	    public String creditoMayor(@RequestParam Double monto, Model model) {
	        model.addAttribute("resultado", service.buscarCreditoMayorA(monto));
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/destacados")
	    public String destacados(Model model) {
	        model.addAttribute("resultado", service.buscarDestacados());
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/nombre-credito-mayor")
	    public String nombreYCredito(@RequestParam String nombre, @RequestParam Double monto, Model model) {
	        model.addAttribute("resultado", service.buscarPorNombreYCreditoMayor(nombre, monto));
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/foto-no-imagen")
	    public String fotoNoImagen(Model model) {
	        model.addAttribute("resultado", service.buscarFotoNoImagen());
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/destacados-credito-mayor")
	    public String destacadosCredito(@RequestParam Double monto, Model model) {
	        model.addAttribute("resultado", service.buscarDestacadosConCreditoMayor(monto));
	        return "clientes/BusquedasCliente";
	    }

	    @GetMapping("/top5-credito")
	    public String top5(Model model) {
	        model.addAttribute("resultado", service.top5PorCredito());
	        return "clientes/BusquedasCliente";
	    }
	}
