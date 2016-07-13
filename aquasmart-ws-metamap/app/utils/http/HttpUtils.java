package utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import utils.json.JSON;
import play.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HttpUtils {

	public static String currentCookie = null;
	
	public static ObjectNode jsonGET(String urlString) throws IOException
	{
		
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		
		if(currentCookie != null)
			conn.setRequestProperty( "Cookie", currentCookie );
		
		if(HttpUtils.debug)
			Logger.debug("json GET( "+urlString+" )");
		
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		String cooks = conn.getHeaderField("Set-Cookie");
	
		
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String line;
		String output = "";
	
		while ((line = br.readLine()) != null) {
			output += line + "\n";
		}
		
		if(HttpUtils.debug)
			Logger.debug("GET output is " +output);

		//Logger.debug("jsonGET( "+urlString+" ) :"+output);
		
		conn.disconnect(); 
		
		return JSON.stringToJson(output);
	}
	
	public static String stringGET(String urlString) throws IOException
	{
		
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

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
		if(HttpUtils.debug)
		{
			Logger.debug("GET output is " +output);
			Logger.debug("json GET( "+urlString+" )");
		}
		conn.disconnect();
		
		return output;
	}
	
	public static boolean debug = true;
	public static ObjectNode jsonPOST(String urlString, ObjectNode param) throws IOException
	{
		
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		if(currentCookie != null)
			conn.setRequestProperty( "Cookie", currentCookie );
		
		OutputStream os = conn.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		String input = mapper.writeValueAsString(param);
		
		if(HttpUtils.debug)
			Logger.debug("JSON POST( "+urlString+" ) payload is: \n"+input);
		
        os.write(input.getBytes());
        os.flush();

		if (conn.getResponseCode() != 200)
		{
			if(HttpUtils.debug)
				Logger.debug(conn.getResponseMessage());
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String line;
		String output = "";
	
		while ((line = br.readLine()) != null)
			output += line + "\n";
		
		if(HttpUtils.debug)
			Logger.debug("\tPOST Response is :"+output);
		
		conn.disconnect();
		
		return (ObjectNode) JSON.stringToJson(output);
	}
	
	public static ObjectNode jsonPATCH(String urlString, ObjectNode param) throws IOException, URISyntaxException
	{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPatch httpPatch = new HttpPatch(new URI(urlString));
		ObjectMapper mapper = new ObjectMapper();
		String input = mapper.writeValueAsString(param);
		
		if(HttpUtils.debug)
			Logger.debug("JSON PATCH( "+urlString+" ) payload is: \n"+input);
		
		
		httpPatch.setHeader("Content-Type", "application/json");
		httpPatch.setHeader("Accept", "application/json");
		httpPatch.setEntity( new StringEntity( input ));
		if(currentCookie != null)
			httpPatch.setHeader( "Cookie", currentCookie );
		
		CloseableHttpResponse response = httpClient.execute(httpPatch);
		
	
		if (response.getStatusLine().getStatusCode() != 200)
		{
			if(HttpUtils.debug)
				Logger.debug(response.getStatusLine().getReasonPhrase());
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatusLine().getStatusCode() );
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(response.getEntity().getContent())));

		String line;
		String output = "";
	
		while ((line = br.readLine()) != null)
			output += line + "\n";
		
		if(HttpUtils.debug)
			Logger.debug("\tPATCH Response is :"+output);
		
		response.close();
		
		return (ObjectNode) JSON.stringToJson(output);
	}
	
	public static String buildRestURL(boolean endWithSlash, String host, String... params)
	{
		String finalURL = host.endsWith("/") ? host : host + "/" ;
		for(int i = 0; i < params.length ; i++)
			finalURL += ( i == 0 ? "" : "/" ) + params[i];
		
		if(endWithSlash)
			finalURL += "/";
			
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
		if(HttpUtils.debug)
			Logger.debug("Prepared URL: "+finalURL);
		return finalURL;
	}
}
