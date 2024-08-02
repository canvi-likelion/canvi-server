package com.canvi.hama;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "https://hama.anhye0n.com", description = "Default Server URL")})
@SpringBootApplication
public class HamaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HamaApplication.class, args);
	}

}
