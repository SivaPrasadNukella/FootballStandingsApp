package com.test.football.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.test.football.exceptions.EmptyFileException;
import com.test.football.model.HttpUtils;
import com.test.football.model.Standings;

@Service
public class StandingsService {
	
	private Logger logger;
	private HttpUtils httpUtils;
	private static final String TEAM_ARCHIVE_URL = "https://football.fantasysports.yahoo.com/archive";
	
	public StandingsService() {
		logger = Logger.getLogger(getClass().getName());
	}
	
	@Autowired
	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
		readCredentials();
	}
	
	public HttpUtils getHttpUtils() {
		return httpUtils;
	}
	
	private void readCredentials() {
		
		logger.info("Reading Credentials File.");
		
        try {
            File file = ResourceUtils.getFile("classpath:credentials.json");
            
            if(!file.exists()) {
            	getInitialAuthorization();
            }else {
                FileReader reader = new FileReader(file);
                
                JSONParser parser = new JSONParser(reader);
                @SuppressWarnings("unchecked")
				Map<String, String> map = (LinkedHashMap<String, String>) parser.parse();
                if(map.isEmpty()) {
                	throw new EmptyFileException("File credentials.json is Empty.");
                }
                
            }
        } catch (IOException | EmptyFileException | ParseException e) {
        	
            e.printStackTrace();
            getInitialAuthorization();
        }
	}
	
	private void getInitialAuthorization() {
		try {
			String result = httpUtils.getInitialAuthorization();
			
			if(!result.toUpperCase().contains("INVALID")) {
			
				FileWriter writer = new FileWriter(ResourceUtils.getFile("src/main/resources/credentials.json"));    
				writer.write(result);    
				writer.close();
				
				logger.info("File credentials.json created.");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Standings> getFootballStandings() {
		
		List<Standings> standingsList = new ArrayList<>();
		
		try {
			JSONObject jsonObj = null;
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
		
		return standingsList;
	}
	
}
