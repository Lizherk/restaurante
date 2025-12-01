package isa.restaurante.modelo;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
@Entity
@Table(name="cliente")
public class Cliente {
//id
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;
	    // Atributos
	    private String nombre;
	    private String apellido;
	    private String email;
	    private double credito;
	    private String telefono;
	    private Integer destacado;   
	    private String fotoCliente;  

	    public Integer getId() {
	        return id;
	    }
	    public void setId(Integer id) {
	        this.id = id;
	    }

	    public String getNombre() {
	        return nombre;
	    }
	    public void setNombre(String nombre) {
	        this.nombre = nombre;
	    }

	    public String getApellido() {
	        return apellido;
	    }
	    public void setApellido(String apellido) {
	        this.apellido = apellido;
	    }

	    public String getEmail() {
	        return email;
	    }
	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public double getCredito() {
	        return credito;
	    }
	    public void setCredito(double credito) {
	        this.credito = credito;
	    }

	    public String getTelefono() {
	        return telefono;
	    }
	    public void setTelefono(String telefono) {
	        this.telefono = telefono;
	    }

	    public Integer getDestacado() {
	        return destacado;
	    }
	    public void setDestacado(Integer destacado) {
	        this.destacado = destacado;
	    }

	    public String getFotoCliente() {
	        return fotoCliente;
	    }
	    public void setFotoCliente(String fotoCliente) {
	        this.fotoCliente = fotoCliente;
	    }

	    @Override
	    public String toString() {
	        return "Cliente [id=" + id +
	               ", nombre=" + nombre +
	               ", apellido=" + apellido +
	               ", email=" + email +
	               ", credito=" + credito +
	               ", telefono=" + telefono +
	               ", destacado=" + destacado +
	               ", fotoCliente=" + fotoCliente + "]";
	    }
	}