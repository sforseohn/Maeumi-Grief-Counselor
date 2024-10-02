package com.gcl.maeumi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MaeumiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaeumiApplication.class, args);
	}
}
