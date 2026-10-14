package com.uniquindio.nexuscultural;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.uniquindio.nexuscultural.infraestructure.persistence.repository")
@EntityScan(basePackages = "com.uniquindio.nexuscultural.infraestructure.persistence.entity")
public class NexusculturalApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexusculturalApplication.class, args);
	}

}
