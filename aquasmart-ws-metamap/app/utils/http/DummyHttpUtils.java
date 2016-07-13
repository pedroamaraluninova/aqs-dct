package utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import utils.json.JSON;
import play.Logger;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DummyHttpUtils {


	
	public static ObjectNode jsonGET(String urlString) throws IOException
	{
		
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		Logger.debug("jsonGET( "+urlString+" )");
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String line;
		String output = "";
	
		while ((line = br.readLine()) != null) {
			output += line + "\n";
		}

		Logger.debug("jsonGET( "+urlString+" ) :"+output);
		
		conn.disconnect();
		
		return (ObjectNode) JSON.stringToJson(output);
	}
	
	public static ObjectNode jsonPOST(String urlString, ObjectNode param) throws IOException
	{
		
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		
		OutputStream os = conn.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		String input = mapper.writeValueAsString(param);
        os.write(input.getBytes());
        os.flush();

		if (conn.getResponseCode() != 200)
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());

		
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String line;
		String output = "";
	
		while ((line = br.readLine()) != null)
			output += line + "\n";
		
		Logger.debug("jsonPost( "+urlString+" ) payload is: "+input);
		Logger.debug("response is "+output);
		conn.disconnect();
		
		return (ObjectNode) JSON.stringToJson(output);
	}
	
	public static String buildRestURL(String host, String... params)
	{
		String finalURL = host.endsWith("/") ? host : host + "/" ;
		for(int i = 0; i < params.length ; i++)
			finalURL += ( i == 0 ? "" : "/" ) + params[i];
	
		Logger.debug("Preparing URL: "+finalURL);
		return finalURL;
	}
	
	public static String buildURLWithParams(String host, String... params)
	{
		String finalURL = host.endsWith("/") ? host : host + "/" ;
		for(int i = 0; i < params.length ; i++)
		{
			String paramName = params[i++],
					paramValue = params[i] ;
			
			finalURL += ( i == 1 ? "" : "&" ) + paramName + "=" + paramValue;
		}
		Logger.debug("Preparing URL: "+finalURL);
		return finalURL;
	}
}
