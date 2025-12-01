package isa.restaurante.serviceimp;
import isa.restaurante.modelo.Producto;
import isa.restaurante.modelo.TipoProducto;
import isa.restaurante.service.IProductoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
@Service
public class ProductoServiceImpl  implements IProductoService{

    private final List<Producto> lista = new LinkedList<>();
    private final AtomicInteger secuencia = new AtomicInteger(4); 

    public ProductoServiceImpl() {
        // Producto 1
        Producto p1 = new Producto();
        p1.setId(1);
        p1.setNombre("Arrachera a la Brasa");
        p1.setDescripcion("Corte marinado, sellado al carbón, con cebollitas y nopal.");
        p1.setPrecio(179.00);
        p1.setTipo(TipoProducto.PLATILLO);
        p1.setFotoProducto("arrachera.jpg");

        // Producto 2
        Producto p2 = new Producto();
        p2.setId(2);
        p2.setNombre("Costillas BBQ");
        p2.setDescripcion("Costillas de cerdo glaseadas con salsa BBQ casera.");
        p2.setPrecio(199.00);
        p2.setTipo(TipoProducto.PLATILLO);
        p2.setFotoProducto("costillas.jpg");

        // Producto 3
        Producto p3 = new Producto();
        p3.setId(3);
        p3.setNombre("Limonada con Carbón Activado");
        p3.setDescripcion("Refrescante con toque cítrico y carbón activado.");
        p3.setPrecio(39.00);
        p3.setTipo(TipoProducto.BEBIDA);
        p3.setFotoProducto("limonada.jpg");

        // Producto 4
        Producto p4 = new Producto();
        p4.setId(4);
        p4.setNombre("Piña a las Brasas");
        p4.setDescripcion("Rebanadas de piña caramelizadas con helado de vainilla.");
        p4.setPrecio(59.00);
        p4.setTipo(TipoProducto.POSTRE);
        p4.setFotoProducto("pina.jpg");

        // Agregar a la lista inicial
        lista.add(p1);
        lista.add(p2);
        lista.add(p3);
        lista.add(p4);
    }

    @Override
    public List<Producto> buscarTodos() {
        return lista;
    }

    @Override
    public Producto buscarPorId(Integer idProducto) {
        for (Producto p : lista) {
            if (p.getId().equals(idProducto)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void guardar(Producto producto) {
        if (producto.getId() == null) {
            producto.setId(secuencia.incrementAndGet()); // genera nuevo id
        }
        lista.add(producto);
    }

	@Override
	public void eliminar(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Producto> buscarPorTipo(TipoProducto tipo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorPrecioEntre(Double min, Double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorTipoYPrecioEntre(TipoProducto tipo, Double min, Double max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarNombreContiene(String q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPorTipoOrdenPrecio(TipoProducto tipo, boolean asc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producto> buscarPublico(TipoProducto tipo, Double min, Double max, String q, Boolean ordenarAsc) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public List<Producto> buscarAdmin(String nombre, TipoProducto tipo, Double min, Double max, Boolean ordenarAsc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void guardar(Producto producto, MultipartFile archivo) throws IOException {
		// TODO Auto-generated method stub
		
	}
}