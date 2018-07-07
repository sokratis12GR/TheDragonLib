package net.thedragonteam.thedragonlib.network.internet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class POSTRequest {
	
	public String target;
	public String params;
	public String useragent;
	
	public POSTRequest(){
		target = "";
		params = "";
		useragent = "";
	}
	
	public POSTRequest(String tar){
		target = tar;
		params = "";
		useragent = "TheDragonLib";
	}
	
	public POSTRequest(String tar, String par){
		target = tar;
		params = par;
		useragent = "TheDragonLib";
	}
	
	public POSTRequest(String tar, String par, String agent){
		target = tar;
		params = par;
		useragent = agent;
	}
	
	public void  setTarget(String tar){
		target = tar;
	}
	
	public void setParameters(String par){
		params = par;
	}
	
	public void addParameter(String par){
		params = params + "&" + par;
	}
	
	public void setUserAgent(String agent){
		useragent = agent;
	}
	
	public POSTResponse execute() throws IOException{
		
		URL obj = new URL(target);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", useragent);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = params;
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		return new POSTResponse(responseCode, response.toString(), target, params, useragent);
		
	}

}