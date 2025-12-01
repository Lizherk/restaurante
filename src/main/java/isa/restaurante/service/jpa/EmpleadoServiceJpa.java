package isa.restaurante.service.jpa;
import isa.restaurante.modelo.Empleado;
import isa.restaurante.modelo.Puesto;
import isa.restaurante.repository.EmpleadoRepository;
import isa.restaurante.service.IEmpleadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class EmpleadoServiceJpa implements IEmpleadoService {
	   private final EmpleadoRepository repo;

	    public EmpleadoServiceJpa(EmpleadoRepository repo) {
	        this.repo = repo;
	    }

	    @Override public List<Empleado> listar() { return repo.findAll(); }

	    @Override public Empleado buscarPorId(Integer id) {
	        return repo.findById(id).orElse(null);
	    }

	    @Override public Empleado guardar(Empleado empleado) {
	        return repo.save(empleado);
	    }

	    @Override public void eliminar(Integer id) {
	        repo.deleteById(id);
	    }

	    @Override public List<Empleado> buscarPorPuesto(Puesto puesto) {
	        return repo.findByPuesto(puesto);
	    }

	    @Override public List<Empleado> buscarPorNombre(String q) {
	        return repo.findByNombreCompletoContainingIgnoreCase(q == null ? "" : q.trim());
	    }

	    @Override public Empleado buscarPorClave(String clave) {
	        return repo.findByClave(clave).orElse(null);
	    }
	    @Override
	    public List<Empleado> buscarTodos() {
	        return repo.findAll();
	    }
	}
