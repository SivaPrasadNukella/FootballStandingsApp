package com.test.football.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.football.model.Standings;
import com.test.football.service.StandingsService;
import com.test.football.util.CommonUtils;

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
	public List<Standings> getStandings(){
		
		logger.info("Controller getStandings Request Received.");
		//String responseStr = standingsService.getFootballStandings();
		List<Standings> responseStr = standingsService.getFootballStandings();
		
		logger.info("Controller getStandings Response: "+CommonUtils.getJSONString(responseStr));
		
		return responseStr;
	}
	
	
}
