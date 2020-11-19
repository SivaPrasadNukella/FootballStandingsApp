package com.test.football.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.test.football.ApplConfiguration;
import com.test.football.exceptions.InvalidAuthorizationCode;
import com.test.football.exceptions.UnAuthorizedException;

@Component
public class HttpUtils {
	
	private Logger logger;
	private ApplConfiguration applConfiguration;
	public final String AUTH_ENDPOINT = "https://api.login.yahoo.com/oauth2/get_token";
	public final String YAHOO_ENDPOINT = "https://fantasysports.yahooapis.com/fantasy/v2";
	public final String GAMEKEY_ENDPOINT = YAHOO_ENDPOINT + "/game/nfl";
	
	public static String STANDINGS_ENDPOINT;
	private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36";
	
	public HttpUtils() {
		logger = Logger.getLogger(getClass().getName());
	}
	
	@Autowired
	public void setApplConfiguration(ApplConfiguration applConfiguration) {
		this.applConfiguration = applConfiguration;
		this.applConfiguration.printConfiguration();
		STANDINGS_ENDPOINT = YAHOO_ENDPOINT + "/league/" + applConfiguration.getLeagueKey() + "/standings";
	}

    public String getInitialAuthorization() throws Exception {
    	
    	logger.info("Initializing Authorization.");
    	
    	String result = null;

        HttpPost post = new HttpPost(AUTH_ENDPOINT);
        post.addHeader("Authorization", "Basic "+getAuthHeader());
        post.addHeader("Content-Type", CONTENT_TYPE);
        post.addHeader("User-Agent", USER_AGENT);
        
        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("client_id", applConfiguration.getConsumerKey()));
        urlParameters.add(new BasicNameValuePair("client_secret", applConfiguration.getConsumerSecret()));
        urlParameters.add(new BasicNameValuePair("redirect_uri", "oob"));
        urlParameters.add(new BasicNameValuePair("code", applConfiguration.getYahooAuthCode()));
        urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

        	result = EntityUtils.toString(response.getEntity());
            logger.info("Initial Authorization Result: "+result);
            
            if(result.toUpperCase().contains("INVALID")) {
            	throw new InvalidAuthorizationCode("OAuth authorization code expired or invalid. "
            			+ "Update the new Authorization Code in oauth-config.properties, by making use of URL: "
            			+ "https://api.login.yahoo.com/oauth2/request_auth?client_id="+ applConfiguration.getConsumerKey() +"&redirect_uri=oob&response_type=code&language=en-us");
            }
            
        } catch(InvalidAuthorizationCode invalidAuthCode) {
        	logger.info(invalidAuthCode.getMessage());
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        return result;

    }
    
    public String refreshAuthorizationToken() throws Exception {
    	
    	logger.info("Refreshing Authorization Token.");
    	
    	String result = null;
    	
    	HttpPost post = new HttpPost(AUTH_ENDPOINT);
        post.addHeader("Authorization", "Basic "+getAuthHeader());
        post.addHeader("Content-Type", CONTENT_TYPE);
        post.addHeader("User-Agent", USER_AGENT);
        
        File file = ResourceUtils.getFile("classpath:" + applConfiguration.getAuthFile());
        FileReader reader = new FileReader(file);
        
        JSONParser parser = new JSONParser(reader);
        @SuppressWarnings("unchecked")
		Map<String, String> map= (LinkedHashMap<String, String>) parser.parse();

        String refreshToken = map.get("refresh_token");
        reader.close();
        
        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("redirect_uri", "oob"));
        urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
        urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
        
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

        	result = EntityUtils.toString(response.getEntity());
            logger.info("Refresh Authorization Result: "+result);
            
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        return result;
        
    }
    
    public JSONObject apiRequestForStandings() throws Exception {
    	
    	logger.info("Making API call to get Football Standings.");
    	
    	String result = null;
    	
    	JSONObject jsonObj = null;
    	File file = ResourceUtils.getFile("classpath:" + applConfiguration.getAuthFile());
        FileReader reader = new FileReader(file);
        
        JSONParser parser = new JSONParser(reader);
        @SuppressWarnings("unchecked")
		Map<String, String> map = (LinkedHashMap<String, String>) parser.parse();

        String accessToken = map.get("access_token");
        reader.close();

        HttpGet get = new HttpGet(STANDINGS_ENDPOINT);

        get.addHeader("Authorization", "Bearer "+accessToken);
        get.addHeader("Content-Type", CONTENT_TYPE);
        get.addHeader("User-Agent", USER_AGENT);

        try (CloseableHttpResponse response = HTTP_CLIENT.execute(get)) {

            // Get HttpResponse Status
        	logger.info("Http Response Status: "+response.getStatusLine().toString());
            
            if(response.getStatusLine().getStatusCode() == 401) {
            	throw new UnAuthorizedException(response.getStatusLine().getReasonPhrase());
            }

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            logger.info("Http Response Headers: "+headers);

            if (entity != null) {
                
            	// convert it as a String
                result = EntityUtils.toString(entity);
                //logger.info("API call for Football Standings Result: "+result);
                
                // convert it as a JSON Object
                jsonObj = XML.toJSONObject(result);
            }

        } catch(UnAuthorizedException ex) {
        	
        	logger.info("Trying to get New OAuth Token as existing one Expired.");
        	
        	result = refreshAuthorizationToken();
			
			FileWriter fw = new FileWriter(ResourceUtils.getFile("src/main/resources/credentials.json"));
			fw.write(result);
			fw.close();
			logger.info("File credentials.json created.");
			  
			apiRequestForStandings();
			 
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        return jsonObj;

    }
    
    public String getAuthHeader() {
		byte[] encodedBytes = Base64.encodeBase64((applConfiguration.getConsumerKey() + ":"+ applConfiguration.getConsumerSecret()).getBytes());
		return new String(encodedBytes);
	}
    
}
