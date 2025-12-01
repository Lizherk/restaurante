package isa.restaurante.restaurante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(basePackages = "isa.restaurante.repository")
@EntityScan(basePackages = "isa.restaurante.modelo")
@SpringBootApplication(scanBasePackages = "isa.restaurante")
public class RestauranteApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestauranteApplication.class, args);
    }
}