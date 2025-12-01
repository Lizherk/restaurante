package isa.restaurante.service;
import isa.restaurante.modelo.Cliente;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.util.LinkedList;
import java.io.IOException;
public interface IClienteService {
	   List<Cliente> buscarTodosClientes();
	    Cliente buscarPorIdCliente(Integer idCliente);
	    void guardar(Cliente cliente);
	    void guardar(Cliente cliente, MultipartFile archivo) throws IOException;
	    void eliminar(Integer idCliente);
	    Cliente crear(Cliente cliente);
	    Cliente actualizar(Cliente cliente); 
		 Optional<Cliente> buscarPorNombre(String nombre);                         // 1
		    List<Cliente> buscarNombreContiene(String cadena);                     // 2
		    Optional<Cliente> buscarPorEmail(String email);                        // 3
		    List<Cliente> buscarEmailGmail();                                      // 4
		    List<Cliente> buscarCreditoEntre(Double min, Double max);              // 5
		    List<Cliente> buscarCreditoMayorA(Double monto);                        // 6
		    List<Cliente> buscarDestacados();                                      // 7
		    List<Cliente> buscarPorNombreYCreditoMayor(String nombre, Double m);   // 8
		    List<Cliente> buscarFotoNoImagen();                                    // 9
		    List<Cliente> buscarDestacadosConCreditoMayor(Double m);               // 10
		    List<Cliente> top5PorCredito();                                        // 11
}
