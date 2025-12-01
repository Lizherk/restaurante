package isa.restaurante.service.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import isa.restaurante.service.IClienteService;
import isa.restaurante.service.ImagenService; // <--- IMPORTANTE
import isa.restaurante.modelo.Cliente;
import isa.restaurante.repository.ClienteRepository;

@Service
@Primary
public class ClienteServicejpa implements IClienteService {

    private final ClienteRepository clienteRepo;
    private final ImagenService imagenService; // <--- NUEVO

    // Constructor actualizado
    public ClienteServicejpa(ClienteRepository clienteRepo, ImagenService imagenService) {
        this.clienteRepo = clienteRepo;
        this.imagenService = imagenService;
    }

    // MÉTODO MODIFICADO
    @Override
    public void guardar(Cliente cliente, MultipartFile archivo) throws IOException {
        if (archivo != null && !archivo.isEmpty()) {
            // Sube a Cloudinary
            String url = imagenService.subirImagen(archivo);
            cliente.setFotoCliente(url);
        }
        clienteRepo.save(cliente);
    }

    // --- El resto de métodos NO cambian, copia los que ya tenías ---
    @Override public List<Cliente> buscarTodosClientes() { return clienteRepo.findAll(); }
    @Override public Cliente buscarPorIdCliente(Integer idCliente) { return clienteRepo.findById(idCliente).orElse(null); }
    @Override public void guardar(Cliente cliente) { clienteRepo.save(cliente); }
    @Override public void eliminar(Integer idCliente) { clienteRepo.deleteById(idCliente); }
    // ... agrega aquí los métodos de búsqueda restantes (buscarPorEmail, etc.) tal como los tenías ...
    @Override public Optional<Cliente> buscarPorNombre(String nombre) { return clienteRepo.findByNombre(nombre); }
    @Override public List<Cliente> buscarNombreContiene(String cadena) { return clienteRepo.findByNombreContainingIgnoreCase(cadena); }
    @Override public Optional<Cliente> buscarPorEmail(String email) { return clienteRepo.findByEmail(email); }
    @Override public List<Cliente> buscarEmailGmail() { return clienteRepo.findByEmailEndingWith("@gmail.com"); }
    @Override public List<Cliente> buscarCreditoEntre(Double min, Double max) { return clienteRepo.findByCreditoBetween(min, max); }
    @Override public List<Cliente> buscarCreditoMayorA(Double monto) { return clienteRepo.findByCreditoGreaterThan(monto); }
    @Override public List<Cliente> buscarDestacados() { return clienteRepo.findByDestacado(1); }
    @Override public List<Cliente> buscarPorNombreYCreditoMayor(String nombre, Double monto) { return clienteRepo.findByNombreAndCreditoGreaterThan(nombre, monto); }
    @Override public List<Cliente> buscarFotoNoImagen() { return clienteRepo.findByFotoCliente("no_imagen.jpg"); }
    @Override public List<Cliente> buscarDestacadosConCreditoMayor(Double monto) { return clienteRepo.findByDestacadoAndCreditoGreaterThan(1, monto); }
    @Override public List<Cliente> top5PorCredito() { return clienteRepo.findTop5ByOrderByCreditoDesc(); }
    @Override public Cliente crear(Cliente cliente) { return clienteRepo.save(cliente); } // (Método legacy)
    @Override public Cliente actualizar(Cliente cliente) { return clienteRepo.save(cliente); } // (Método legacy)
}