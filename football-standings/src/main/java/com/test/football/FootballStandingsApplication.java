package com.test.football;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ApplConfiguration.class)
public class FootballStandingsApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(FootballStandingsApplication.class, args);
	}

}
