package com.test.football;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * The Application Spring configuration.
 * 
 * @author SivaPrasad
 */
@Configuration
@ComponentScan("com.test.football.model")
//@PropertySource(ignoreResourceNotFound = true, value={"classpath:oauth-config.properties"})
@PropertySource(value={"classpath:oauth-config.properties"})
public class ApplConfiguration {

	private Logger logger;
	
	@Value("${consumer.key}")
	private String consumerKey;
	
	@Value("${consumer.secret}")
	private String consumerSecret;
	
	@Value("${yahoo.auth.code}")
	private String yahooAuthCode;
	
	@Value("${league.key}")
	private String leagueKey;
	
	@Value("${team}")
	private String team;
	
	@Value("${auth.file}")
	private String authFile;
	
	public ApplConfiguration() {
		logger = Logger.getLogger(getClass().getName());
	}
	
	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getYahooAuthCode() {
		return yahooAuthCode;
	}

	public String getLeagueKey() {
		return leagueKey;
	}

	public String getTeam() {
		return team;
	}

	public String getAuthFile() {
		return authFile;
	}
	
	public void printConfiguration() {
		logger.info("Reading OAuth Config File.");
		logger.info("consumerKey: "+consumerKey+", consumerSecret: "+consumerSecret+", yahooAuthCode: "+yahooAuthCode+
				", leagueKey: "+leagueKey+", team: "+team+", authFile: "+authFile+".");
	}

}
