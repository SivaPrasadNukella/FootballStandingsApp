package com.test.football.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.football.model.HttpUtils;
import com.test.football.model.Standings;

@Service
public class StandingsService {
	
	private Logger logger;
	private HttpUtils httpUtils;
	private static final String TEAM_ARCHIVE_URL = "https://football.fantasysports.yahoo.com/archive";
	
	public StandingsService() {
		logger = Logger.getLogger(getClass().getName());
		readCredentials();
	}
	
	@Autowired
	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}
	
	public HttpUtils getHttpUtils() {
		return httpUtils;
	}
	
	private void readCredentials() {
		
		logger.info("Reading Credentials.");
		
        try {
            File file = ResourceUtils.getFile("classpath:credentials.json");
            
            if(!file.exists()) {
            	getInitialAuthorization();
            }
        } catch (IOException e) {
        	
            e.printStackTrace();
            getInitialAuthorization();
        }
	}
	
	private void getInitialAuthorization() {
		try {
			String result = httpUtils.getInitialAuthorization();
			ClassLoader classLoader = getClass().getClassLoader();
			
			File file = new File(classLoader.getResource(".").getFile() + "/credentials.json");
			
			FileWriter writer = new FileWriter(file);    
			writer.write(result);    
			writer.close();
			logger.info("File credentials.json created!");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getFootballStandings() {
		
		JSONObject jsonObj = null;
		List<Standings> standingsList = new ArrayList<>();
		
		try {
			jsonObj =  httpUtils.apiRequestForStandings();
			
			JSONArray result = jsonObj.
					getJSONObject("fantasy_content").
					getJSONObject("league").
					getJSONObject("standings").
					getJSONObject("teams").
					getJSONArray("team");
			
			String gameCode = jsonObj.
					getJSONObject("fantasy_content").
					getJSONObject("league").getString("game_code");
			String season = Integer.toString(jsonObj.
					getJSONObject("fantasy_content").
					getJSONObject("league").getInt("season"));
			String leagueId = Integer.toString(jsonObj.
					getJSONObject("fantasy_content").
					getJSONObject("league").getInt("league_id"));
			
			boolean matchesURlSet = false; 
			
			for (Object object : result) {
				
				Standings standings = new Standings();
				
				JSONObject josonObj = (JSONObject)object;
				
				standings.setTeamId(Integer.toString(josonObj.getInt("team_id")));
				standings.setClub(josonObj.getString("name"));
				standings.setRank(Integer.toString(josonObj.getJSONObject("team_standings").getInt("rank")));
				standings.setMatches(
						Integer.toString(
						josonObj.getJSONObject("team_standings").getJSONObject("outcome_totals").
						getInt("wins") + 
						josonObj.getJSONObject("team_standings").getJSONObject("outcome_totals").
						getInt("ties") + 
						josonObj.getJSONObject("team_standings").getJSONObject("outcome_totals").
						getInt("losses")));
				standings.setPoints(Float.toString(josonObj.getJSONObject("team_points").getFloat("total")));
				standings.setWin(Integer.toString(josonObj.getJSONObject("team_standings").getJSONObject("outcome_totals").
						getInt("wins")));
				standings.setDraw(Integer.toString(josonObj.getJSONObject("team_standings").getJSONObject("outcome_totals").
						getInt("ties")));
				standings.setLost(Integer.toString(josonObj.getJSONObject("team_standings").getJSONObject("outcome_totals").
						getInt("losses")));
				standings.setUrl(TEAM_ARCHIVE_URL + "/" + gameCode + "/" + season + "/" + leagueId + "/" + standings.getTeamId());
				
				if(!matchesURlSet) {
					standings.setMatchesUrl(TEAM_ARCHIVE_URL + "/" + gameCode + "/" + season + "/" + leagueId + "?lhst=sched&sctype=team");
					matchesURlSet = true;
				}
				
				standings.setImageUrl(josonObj.getJSONObject("team_logos").getJSONObject("team_logo").getString("url"));
				
				standingsList.add(standings);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getJSONString(standingsList);
	}
	
	private <T> String getJSONString(List<T> list) {
		
		String result = null;
		final ObjectMapper mapper = new ObjectMapper();
		
		try {
			result = mapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
}
