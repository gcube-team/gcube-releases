/**
 * 
 */
package org.gcube.portlets.user.workspace;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 28, 2013
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

 
public final class GoogleUrlShortener {
 
	/**
	 * 
	 */
	protected static final String APPLICATION_JSON = "application/json";
	protected static final String LONG_URL = "longUrl";
	
	static String shortnerServerUrl = "https://www.googleapis.com/urlshortener/v1/url";
	
	static String urlMethod = "";
	static String authenticationKeyValue ="AIzaSyDfMO0VY3o8GjRUqnTfqScjm_EsFEuBa3g";
	static String authenticationKeyParam = "key";
	
//	static String shortUrl = "https://dev.d4science.org/group/data-e-infrastructure-gateway/workspace?itemid=062c558c-c7ce-4de3-a4c7-e1411816cc12&operation=gotofolder";
	
	
	
    public static String shorten(String longUrl) throws Exception {
    	
        if (longUrl == null) {
            return longUrl;
        }
        
        try {

            URL url = new URL(shortnerServerUrl);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", APPLICATION_JSON);

            JSONObject jsonObj = new JSONObject();
            
            jsonObj.put(LONG_URL, longUrl);
            jsonObj.put(authenticationKeyParam, authenticationKeyValue);
            
            System.out.println("sending request json : "+jsonObj.toString());
            
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(jsonObj.toString());
            wr.flush();


            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JSONTokener tokener = new JSONTokener(rd);
            JSONObject jsonObject = new JSONObject(tokener);

            System.out.println("response received json : "+jsonObject.toString());
            
            wr.close();
            rd.close();
    		return (String) jsonObject.get("id"); //is shorted url

        } catch (MalformedURLException e) {
        	
        	e.printStackTrace();
            return longUrl;
        } catch (IOException e) {
        	e.printStackTrace();
            return longUrl;
        }
    }
    
    
    public static String shorten4(String longUrl) throws Exception {
    	
        if (longUrl == null) {
            return longUrl;
        }
        
        try {

        	
            JSONObject jsonObj = new JSONObject();
            
            jsonObj.put(LONG_URL, longUrl);
            jsonObj.put(authenticationKeyParam, authenticationKeyValue);
            String body = jsonObj.toString();
        	
        	HttpCallerUtil httpCaller = new HttpCallerUtil(shortnerServerUrl, "", "");

			String json = httpCaller.callPost("", body, APPLICATION_JSON);
        	
        	System.out.println("json: "+json);
        	
        	
        	JSONTokener tokener = new JSONTokener(json);
            JSONObject jsonObject = new JSONObject(tokener);

            System.out.println("response received json : "+jsonObject.toString());

    		return (String) jsonObject.get("id"); //is shorted url

        } catch (MalformedURLException e) {
        	
        	e.printStackTrace();
            return longUrl;
        } catch (IOException e) {
        	e.printStackTrace();
            return longUrl;
        }
    }
    
    
    
    
    public static String shorten3(String longUrl) throws Exception {
    	
        if (longUrl == null) {
            return longUrl;
        }
        
        try {
        	
        	
        	HttpCallerUtil httpCaller = new HttpCallerUtil(shortnerServerUrl, "", "");
        	
//        	Map<String, String> parameters = new HashMap<String, String>();
//         	
//        	parameters.put("shortUrl", longUrl);
//        	parameters.put("key", authenticationKey);
       
        	
//        	String json = httpCaller.callGet(ulrMethod, parameters);
        	
        	String body = "{\"longUrl\":\"" + longUrl + "\", \"key\":\"" + authenticationKeyValue + "\"}";
        	
			urlMethod += authenticationKeyParam +"="+authenticationKeyValue;
			
			String json = httpCaller.callPost("", body, APPLICATION_JSON);
        	
        	System.out.println("json: "+json);
        	
        	
//        	return json.substring(json.indexOf("http"), json.indexOf("\"", json.indexOf("http")));
        	
        	return json;
        			
//        	httpCaller.callGet(urlMethod, parameters);

//            return json.substring(json.indexOf("http"), json.indexOf("\"", json.indexOf("http")));
            
            
            
        } catch (MalformedURLException e) {
        	e.printStackTrace();
            return longUrl;
        } catch (IOException e) {
        	e.printStackTrace();
            return longUrl;
        }
    }
    
    
    private static String googUrl = "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyDfMO0VY3o8GjRUqnTfqScjm_EsFEuBa3g";

    
    public static String shorten2(String longUrl)
    {
        String shortUrl = "";

        try
        {
        	HttpURLConnection conn = (HttpURLConnection) new URL(googUrl).openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", APPLICATION_JSON);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write("{\"longUrl\":\"" + longUrl + "\"}");
            wr.flush();

            
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = rd.readLine()) != null) {
                sb.append(line + '\n');
            }
            String json = sb.toString();
            System.out.println("json: "+json);
            
            wr.close();
            rd.close();
            return json.substring(json.indexOf("http"), json.indexOf("\"", json.indexOf("http")));
            
            // Get the response
//            BufferedReader rd =  new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line;
//            while ((line = rd.readLine()) != null)
//            {
//                if (line.indexOf("id") > -1)
//                {
//                    // I'm sure there's a more elegant way of parsing
//                    // the JSON response, but this is quick/dirty =)
//                    shortUrl = line.substring(8, line.length() - 2);
//                    break;
//                }
//            }

           
        }
        catch (MalformedURLException ex)
        {
        	ex.printStackTrace();
        }
        catch (IOException ex)
        {
        	ex.printStackTrace();
        }

        return shortUrl;
    }

    
    public static void main(String[] args) {
    	String shorten;
    	
		try {
			shorten = GoogleUrlShortener.shorten4("https://dev.d4science.org/group/data-e-infrastructure-gateway/workspace?itemid=062c558c-c7ce-4de3-a4c7-e1411816cc12&operation=gotofolder");
			
			System.out.println("Shorted: "+shorten);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    	
    	
	}
 
}