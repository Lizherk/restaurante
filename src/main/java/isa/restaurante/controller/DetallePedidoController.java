package isa.restaurante.controller;

import isa.restaurante.modelo.DetallePedido;
import isa.restaurante.modelo.Pedido;
import isa.restaurante.modelo.Producto;
import isa.restaurante.service.IDetallePedidoService;
import isa.restaurante.service.IPedidoService;
import isa.restaurante.service.IProductoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pedidos/{pedidoId}/detalles")
public class DetallePedidoController {

    private final IDetallePedidoService detalleService;
    private final IPedidoService pedidoService;
    private final IProductoService productoService;

    public DetallePedidoController(IDetallePedidoService detalleService,
                                   IPedidoService pedidoService,
                                   IProductoService productoService) {
        this.detalleService = detalleService;
        this.pedidoService = pedidoService;
        this.productoService = productoService;
    }

    //  FORMULARIO NUEVO DETALLE
    @GetMapping("/crear")
    public String crear(@PathVariable Integer pedidoId, Model model) {
        Pedido pedido = pedidoService.buscarPorId(pedidoId);
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);

        model.addAttribute("pedido", pedido);
        model.addAttribute("detalle", detalle);
        model.addAttribute("productos", productoService.buscarTodos());
        return "pedidos/detalle-formulario";
    }

    // GUARDAR DETALLE
    @PostMapping("/guardar")
    public String guardar(@PathVariable Integer pedidoId, @ModelAttribute DetallePedido detalle) {
        Pedido pedido = pedidoService.buscarPorId(pedidoId);
        detalle.setPedido(pedido);

        Producto prod = productoService.buscarPorId(detalle.getProducto().getId());
        if (prod != null) {
            detalle.setPrecioUnitario(prod.getPrecio());
            detalle.setSubtotal(prod.getPrecio() * detalle.getCantidad());
        }

        detalleService.guardar(detalle);
        recalcularTotal(pedidoId);

        return "redirect:/pedidos/ver/" + pedidoId;
    }

    //  ELIMINAR DETALLE
    @GetMapping("/eliminar/{detalleId}")
    public String eliminar(@PathVariable Integer pedidoId, @PathVariable Integer detalleId) {
        detalleService.eliminar(detalleId);
        recalcularTotal(pedidoId);
        return "redirect:/pedidos/ver/" + pedidoId;
    }


    private void recalcularTotal(Integer pedidoId) {
        List<DetallePedido> dets = detalleService.buscarPorPedido(pedidoId);
        double total = dets.stream()
                .mapToDouble(d -> d.getSubtotal() == null ? 0.0 : d.getSubtotal())
                .sum();

        Pedido p = pedidoService.buscarPorId(pedidoId);
        if (p != null) {
            p.setTotal(total);
            pedidoService.guardar(p);
        }
    }
}
