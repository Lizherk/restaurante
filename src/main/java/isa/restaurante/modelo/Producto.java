package isa.restaurante.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto")
public class Producto {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    private String nombre;
	    private String descripcion;
	    private double precio;

	    @Enumerated(EnumType.STRING)
	    private TipoProducto tipo;

	    private String fotoProducto;

	    // Getters y Setters
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

	    public String getDescripcion() {
	        return descripcion;
	    }
	    public void setDescripcion(String descripcion) {
	        this.descripcion = descripcion;
	    }

	    public double getPrecio() {
	        return precio;
	    }
	    public void setPrecio(double precio) {
	        this.precio = precio;
	    }

	    public TipoProducto getTipo() {
	        return tipo;
	    }
	    public void setTipo(TipoProducto tipo) {
	        this.tipo = tipo;
	    }

	    public String getFotoProducto() {
	        return fotoProducto;
	    }
	    public void setFotoProducto(String fotoProducto) {
	        this.fotoProducto = fotoProducto;
	    }

	    @Override
	    public String toString() {
	        return "Producto{" +
	                "id=" + id +
	                ", nombre='" + nombre + '\'' +
	                ", descripcion='" + descripcion + '\'' +
	                ", precio=" + precio +
	                ", tipo=" + tipo +
	                ", fotoProducto='" + fotoProducto + '\'' +
	                '}';
	    }
	}