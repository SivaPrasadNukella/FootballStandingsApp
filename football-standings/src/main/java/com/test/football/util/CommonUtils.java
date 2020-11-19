package com.test.football.util;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {
	
	public static <T> String getJSONString(List<T> list) {
			
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

