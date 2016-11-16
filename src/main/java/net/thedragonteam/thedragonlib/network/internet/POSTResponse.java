package net.thedragonteam.thedragonlib.network.internet;

public class POSTResponse {

	public int statuscode = 200;
	public String result = new String();
	public String target = new String();
	public String params = new String();
	public String useragent = new String();
	
	public POSTResponse(int code){
		
		statuscode = code;
		result = "";
		target = "";
		params = "";
		useragent = "";
		
	}
	
	public POSTResponse(String res){
		
		statuscode = 200;
		result = res;
		target = "";
		params = "";
		useragent = "";
		
	}
	
	public POSTResponse(int code, String res){
		
		statuscode = code;
		result = res;
		target = "";
		params = "";
		useragent = "";
		
	}
	
	public POSTResponse(int code, String res, String tar){
		
		statuscode = code;
		result = res;
		target = tar;
		params = "";
		useragent = "";
		
	}
	
	public POSTResponse(int code, String res, String tar, String par){
		
		statuscode = code;
		result = res;
		target = tar;
		params = par;
		useragent = "";
		
	}
	
	public POSTResponse(int code, String res, String tar, String par, String agent){
		
		statuscode = code;
		result = res;
		target = tar;
		params = par;
		useragent = agent;
		
	}
	
}
