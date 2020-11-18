package com.test.football.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.football.service.StandingsService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class FootballStandingsController {
	
	private Logger logger;
	private StandingsService standingsService;
	
	public FootballStandingsController() {
		logger = Logger.getLogger(getClass().getName());
	}
	
	@Autowired
	public void setStandingsService(StandingsService standingsService) {
		this.standingsService = standingsService;
	}
	
	@GetMapping("/standings")
	public String getStandings(){
		
		logger.info("Controller getStandings Request Received.");
		String responseStr = standingsService.getFootballStandings();
		
		logger.info("Controller getStandings Response: "+responseStr);
		
		return responseStr;
	}
	
	
}
