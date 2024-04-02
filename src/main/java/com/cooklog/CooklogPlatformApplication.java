package com.cooklog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CooklogPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(CooklogPlatformApplication.class, args);
	}

}
