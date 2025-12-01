package isa.restaurante.serviceimp;
import isa.restaurante.modelo.Cliente;
import isa.restaurante.service.IClienteService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
@Service
public class ClienteServiceImpl implements IClienteService {
	  private final List<Cliente> listaCliente = new LinkedList<>();
	    private final AtomicInteger secuencia = new AtomicInteger(3);
	    private final Path carpetaSrc = Paths.get("src", "main", "resources", "static", "imagen", "clientes");
	    private Path carpetaRuntime;
	    public ClienteServiceImpl() {
	        // Semillas
	        Cliente c1 = new Cliente();
	        c1.setId(1); c1.setNombre("Antony"); c1.setEmail("antony@gmail.com");
	        c1.setCredito(10000.00); c1.setTelefono("7474752364"); c1.setDestacado(0); c1.setFotoCliente("cat01.png");

	        Cliente c2 = new Cliente();
	        c2.setId(2); c2.setNombre("Ali"); c2.setEmail("ali@gmail.com");
	        c2.setCredito(8000.00); c2.setTelefono("7474752464"); c2.setDestacado(1); c2.setFotoCliente("cat02.jpg");

	        Cliente c3 = new Cliente();
	        c3.setId(3); c3.setNombre("Carlos"); c3.setEmail("carlos@outlook.com");
	        c3.setCredito(5000.00); c3.setTelefono("7474752564"); c3.setDestacado(0); c3.setFotoCliente("cat03.jpg");

	        listaCliente.add(c1); listaCliente.add(c2); listaCliente.add(c3);

	        try { Files.createDirectories(carpetaSrc); } catch (IOException ignored) {}

	        try {
	            var resource = new ClassPathResource("static/imagen/clientes/");
	            carpetaRuntime = resource.getFile().toPath();
	            Files.createDirectories(carpetaRuntime);
	        } catch (Exception e) {
	            carpetaRuntime = Paths.get("target", "classes", "static", "imagen", "clientes");
	            try { Files.createDirectories(carpetaRuntime); } catch (IOException ignored) {}
	        }
	    }

	    @Override
	    public List<Cliente> buscarTodosClientes() {
	        return listaCliente;
	    }

	    @Override
	    public Cliente buscarPorIdCliente(Integer idCliente) {
	        return listaCliente.stream()
	                .filter(c -> c.getId().equals(idCliente))
	                .findFirst()
	                .orElse(null);
	    }

	    @Override
	    public void guardar(Cliente cliente) {
	        if (cliente.getId() == null) cliente.setId(secuencia.incrementAndGet());
	        if (cliente.getFotoCliente() == null || cliente.getFotoCliente().isBlank()) {
	            cliente.setFotoCliente("cat01.png"); // imagen por defecto
	        }
	        listaCliente.add(cliente);
	    }

	    @Override
	    public void guardar(Cliente cliente, MultipartFile archivo) throws IOException {
	        if (cliente.getId() == null) cliente.setId(secuencia.incrementAndGet());

	        String nombreFinal = cliente.getFotoCliente();

	        if (archivo != null && !archivo.isEmpty()) {
	            String nombreOriginal = archivo.getOriginalFilename();
	            String extension = (nombreOriginal != null && nombreOriginal.contains("."))
	                    ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.'))
	                    : "";
	            nombreFinal = "cli_" + System.currentTimeMillis() + extension;

	           
	            byte[] bytes = archivo.getBytes();

	           
	            try {
	                Files.createDirectories(carpetaSrc);
	                Files.write(carpetaSrc.resolve(nombreFinal), bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	            } catch (IOException ignored) {}

	        
	            Files.createDirectories(carpetaRuntime);
	            Files.write(carpetaRuntime.resolve(nombreFinal), bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	        }

	        if (nombreFinal == null || nombreFinal.isBlank()) {
	            nombreFinal = "cat01.png";
	        }
	        cliente.setFotoCliente(nombreFinal);

	        listaCliente.add(cliente);
	    }

		@Override
		public void eliminar(Integer idCliente) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Cliente crear(Cliente cliente) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Cliente actualizar(Cliente cliente) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<Cliente> buscarPorNombre(String nombre) {
			// TODO Auto-generated method stub
			return Optional.empty();
		}

		@Override
		public List<Cliente> buscarNombreContiene(String cadena) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Optional<Cliente> buscarPorEmail(String email) {
			// TODO Auto-generated method stub
			return Optional.empty();
		}

		@Override
		public List<Cliente> buscarEmailGmail() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Cliente> buscarCreditoEntre(Double min, Double max) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Cliente> buscarCreditoMayorA(Double monto) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Cliente> buscarDestacados() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Cliente> buscarPorNombreYCreditoMayor(String nombre, Double m) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Cliente> buscarFotoNoImagen() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Cliente> buscarDestacadosConCreditoMayor(Double m) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Cliente> top5PorCredito() {
			// TODO Auto-generated method stub
			return null;
		}    }
