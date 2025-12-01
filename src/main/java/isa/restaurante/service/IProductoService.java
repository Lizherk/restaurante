package isa.restaurante.service;

import isa.restaurante.modelo.Producto;
import isa.restaurante.modelo.TipoProducto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException; // Required for file operations
import java.util.List;

public interface IProductoService {
    List<Producto> buscarTodos();
    Producto buscarPorId(Integer id);
    
    // 1. Original method (for entity persistence only)
    void guardar(Producto producto);
    
    // 2. NEW OVERLOADED METHOD: Contract required by AdminProductoController to handle the file upload logic.
    void guardar(Producto producto, MultipartFile archivo) throws IOException; 
    
    void eliminar(Integer id);

 
    List<Producto> buscarPorTipo(TipoProducto tipo);
    List<Producto> buscarPorPrecioEntre(Double min, Double max);
    List<Producto> buscarPorTipoYPrecioEntre(TipoProducto tipo, Double min, Double max);
    List<Producto> buscarNombreContiene(String q);
    List<Producto> buscarPorTipoOrdenPrecio(TipoProducto tipo, boolean asc);
    List<Producto> buscarPublico(TipoProducto tipo, Double min, Double max, String q, Boolean ordenarAsc);
    List<Producto> buscarAdmin(String nombre, TipoProducto tipo, Double min, Double max, Boolean ordenarAsc);
}