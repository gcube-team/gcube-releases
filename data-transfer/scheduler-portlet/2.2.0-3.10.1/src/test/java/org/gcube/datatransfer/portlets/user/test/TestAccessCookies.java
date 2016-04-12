package org.gcube.datatransfer.portlets.user.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TestAccessCookies {
	static public String usedURL;
	static public String username;
	static public String password;
	static public String specificPath;
	static public HttpURLConnection connection = null;
	static public BufferedReader rd  = null;
	static public StringBuilder sb = null;
	static public List<String> errors;
	static public HashMap<String,String> cookies;

	
	public TestAccessCookies(){		
	}
	public static void main(String[] args) {
		//initialization
		errors=new ArrayList<String>();
		cookies = new HashMap<String,String>();
		
		//default ones 
		usedURL = "https://devportal.d4science.research-infrastructures.eu/group/devvre/workspace";
		
		//testing 
		process();
		disconnect();
		
	}
	
	static public void process(){

		String line = null;
		String headerName = null;
		URL serverAddress = null;
		try {
			serverAddress = new URL(usedURL+specificPath);
			//set up out communications stuff
			connection = null;

			//Set up the initial connection
			connection = (HttpURLConnection)serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(15000);

			// authorization
			if(username!=null && password !=null){
				if(username.compareTo("")!=0 && password.compareTo("")!=0){
					String userPassword = username + ":" + password;
					String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
					connection.setRequestProperty("Authorization", "Basic " + encoding);
				}
			}
			//connection
			connection.connect();

			//getting-storing cookies
			for(int i=1; (headerName=connection.getHeaderFieldKey(i))!=null; i++){
				if(headerName.equals("Set-Cookie")){
					String cookie = connection.getHeaderField(i);
					storeCookie(cookie);
				}
			}
			//reading cookies 
			System.out.println("Reading cookies... ");
			for(Map.Entry<String,String> entry : cookies.entrySet()){
				System.out.println("Name="+entry.getKey()+" - Value="+entry.getValue());
			}
			
			
		} catch (MalformedURLException e) {
			errors.add("ConnectionHTTP(process) - MalformedURLException\n"+e.getMessage());
			e.printStackTrace();
			return ;
		} catch (ProtocolException e) {
			errors.add("ConnectionHTTP(process) - ProtocolException\n"+e.getMessage());
			e.printStackTrace();
			return ;
		} catch (IOException e) {
			errors.add("ConnectionHTTP(process) - IOException\n"+e.getMessage());
			e.printStackTrace();
			return ;
		}
	}
	
	static public void storeCookie(String input){
		String cookie = input.substring(0, input.indexOf(";"));
		String cookieName = cookie.substring(0, cookie.indexOf("="));
		String cookieValue=cookie.substring(cookie.indexOf("=")+1, cookie.length());
		cookies.put(cookieName, cookieValue);
	}
	
	static public void disconnect(){
		//close the connection, set all objects to null
		connection.disconnect();
		rd = null;
		sb = null;
		connection = null;
	}
}
