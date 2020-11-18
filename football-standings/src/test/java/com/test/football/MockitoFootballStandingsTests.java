package com.test.football;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.test.football.model.HttpUtils;
import com.test.football.service.StandingsService;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@Import(ApplTestConfiguration.class)
public class MockitoFootballStandingsTests {

	@Autowired
	private StandingsService standingsService;
	
	private HttpUtils httpUtils;
	
	@Test
	public void testGetInitialAuthorization() throws Exception {
		httpUtils = standingsService.getHttpUtils();
		String result = httpUtils.getInitialAuthorization();
		System.out.println("TestGetInitialAuthorization Result: "+result);
		Assert.assertNotNull(result);
	}
}
