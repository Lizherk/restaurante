package isa.restaurante.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutPageController {
    

    @GetMapping("/logout-confirmado") 
    public String logoutConfirmado() {
        return "logout-page"; 
    }
}