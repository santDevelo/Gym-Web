package com.ProyectoFinal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada de la aplicación Spring Boot. @SpringBootApplication
// combina @Configuration + @EnableAutoConfiguration + @ComponentScan, así
// que Spring detecta automáticamente los @Controller/@Service/@Repository
// de todo el paquete com.ProyectoFinal.
@SpringBootApplication
public class ProyectoFinalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoFinalApplication.class, args);
	}

}
