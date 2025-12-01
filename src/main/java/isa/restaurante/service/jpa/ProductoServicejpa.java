package isa.restaurante.service.jpa;

import isa.restaurante.modelo.Producto;
import isa.restaurante.modelo.TipoProducto;
import isa.restaurante.repository.ProductoRepository;
import isa.restaurante.service.IProductoService;
import isa.restaurante.service.ImagenService; // <--- IMPORTANTE

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service("productoServicejpa") 
public class ProductoServicejpa implements IProductoService {

    private final ProductoRepository repo;
    private final ImagenService imagenService; // <--- NUEVO

    // Constructor actualizado
    public ProductoServicejpa(ProductoRepository repo, ImagenService imagenService) {
        this.repo = repo;
        this.imagenService = imagenService;
    }
    
    // MÉTODO MODIFICADO: Ahora sube a Cloudinary
    @Override
    public void guardar(Producto p, MultipartFile archivo) throws IOException {
        if (archivo != null && !archivo.isEmpty()) {
            // Sube la imagen a la nube y obtiene el link (https://...)
            String url = imagenService.subirImagen(archivo);
            p.setFotoProducto(url);
        }
        repo.save(p);
    }
    
    // El resto se queda igual...
    @Override public void guardar(Producto p) { repo.save(p); } 
    @Override public List<Producto> buscarTodos() { return repo.findAll(); }
    @Override public Producto buscarPorId(Integer id) { return repo.findById(id).orElse(null); }
    @Override public void eliminar(Integer id) { repo.deleteById(id); }
    @Override public List<Producto> buscarPorTipo(TipoProducto tipo){ return repo.findByTipo(tipo); }
    @Override public List<Producto> buscarPorPrecioEntre(Double min, Double max){ return repo.findByPrecioBetween(min, max); }
    @Override public List<Producto> buscarPorTipoYPrecioEntre(TipoProducto tipo, Double min, Double max){ return repo.findByTipoAndPrecioBetween(tipo, min, max); }
    @Override public List<Producto> buscarNombreContiene(String q){ return repo.findByNombreContainingIgnoreCase(q); }
    @Override public List<Producto> buscarPorTipoOrdenPrecio(TipoProducto tipo, boolean asc){
        return asc ? repo.findByTipoOrderByPrecioAsc(tipo) : repo.findByTipoOrderByPrecioDesc(tipo);
    }

    private String cleanStringParam(String param) {
        if (param == null || param.isBlank()) return null;
        return param.trim();
    }

    @Override
    public List<Producto> buscarPublico(TipoProducto tipo, Double min, Double max, String q, Boolean ordenarAsc) {
        String nombreLimpio = cleanStringParam(q);
        List<Producto> productos = repo.searchPublico(tipo, min, max, nombreLimpio);
        if (Boolean.TRUE.equals(ordenarAsc)) {
            return productos.stream()
                    .sorted(Comparator.comparing(Producto::getPrecio, Comparator.nullsLast(Double::compareTo)))
                    .collect(Collectors.toList());
        }
        return productos;
    }

    @Override
    public List<Producto> buscarAdmin(String nombre, TipoProducto tipo, Double min, Double max, Boolean ordenarAsc) {
        String nombreLimpio = cleanStringParam(nombre);
        List<Producto> productos = repo.searchAdmin(nombreLimpio, tipo, min, max);
        if (Boolean.TRUE.equals(ordenarAsc)) {
            return productos.stream()
                    .sorted(Comparator.comparing(Producto::getPrecio, Comparator.nullsLast(Double::compareTo)))
                    .collect(Collectors.toList());
        }
        return productos;
    }
}
