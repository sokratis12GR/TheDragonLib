package net.thedragonteam.thedragonlib.network.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GETRequest {

	public String target = "";
	public String params = "";
	public String useragent = "";

	public GETRequest(){

		target = "";
		params = "";
		useragent = "";

	}

	public GETRequest(String tar){

		target = tar;
		params = "";
		useragent = "TheDragonLib";

	}

	public GETRequest(String tar, String par){

		target = tar;
		params = par;
		useragent = "TheDragonLib";

	}

	public GETRequest(String tar, String par, String agent){

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

	public GETResponse execute() throws IOException{

		URL obj = new URL(target + "?" + params);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		con.setRequestProperty("User-Agent", useragent);

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return new GETResponse(responseCode, response.toString(), target, params, useragent);

	}

}