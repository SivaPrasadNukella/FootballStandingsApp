package com.test.football;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.test.football.service.StandingsService;

@Profile("test")
@Configuration
public class ApplTestConfiguration {
	
	/*
	 * @Bean
	 * 
	 * @Primary public ApplConfiguration applConfiguration() { return
	 * Mockito.mock(ApplConfiguration.class); }
	 */
	
	@Bean
	@Primary
	public StandingsService standingsService() {
		return Mockito.mock(StandingsService.class);
	}

}
