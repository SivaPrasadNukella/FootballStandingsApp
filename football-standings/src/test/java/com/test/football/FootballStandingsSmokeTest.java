package com.test.football;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.test.football.controller.FootballStandingsController;
import com.test.football.service.StandingsService;

@SpringBootTest
public class FootballStandingsSmokeTest {
	
	@Autowired
	private FootballStandingsController controller;
	
	@Autowired
	private StandingsService standingsService;

	@Test
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
		assertThat(standingsService).isNotNull();
	}
}
