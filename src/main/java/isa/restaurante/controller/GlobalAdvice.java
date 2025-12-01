package isa.restaurante.controller;

import isa.restaurante.modelo.Usuario;
import isa.restaurante.service.IUsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalAdvice {

    private final IUsuarioService usuarioService;

    public GlobalAdvice(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Este método se ejecuta en CADA petición.
     * Busca el usuario en la BD y devuelve su nombre real para usarlo en el HTML.
     * Variable disponible en HTML: ${nombreUsuarioReal}
     */
    @ModelAttribute("nombreUsuarioReal")
    public String agregarNombreReal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // 1. Obtenemos el username de la sesión (ej. "cocinero7")
            String username = auth.getName();
            
            // 2. Buscamos los datos completos en la BD
            Usuario usuario = usuarioService.buscarPorUsername(username);
            
            // 3. Devolvemos el nombre real (ej. "Juan Pérez")
            if (usuario != null && usuario.getNombre() != null) {
                return usuario.getNombre();
            }
            return username; // Fallback si no tiene nombre
        }
        return null;
    }
}