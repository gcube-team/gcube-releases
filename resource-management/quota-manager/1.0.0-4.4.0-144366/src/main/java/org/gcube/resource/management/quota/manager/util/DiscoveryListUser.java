package org.gcube.resource.management.quota.manager.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Retrieves the list user form the base url of the social-networking service in the scope provided
 * @author Alessandro Pieve at ISTI-CNR 
 * (alessandro.pieve@isti.cnr.it)
 */
public class DiscoveryListUser {

	private static Logger log = LoggerFactory.getLogger(DiscoveryListUser.class);
	private final static String NAME_SERVICE_ALL_USERNAMES = "2/users/get-all-fullnames-and-usernames?gcube-token";
	

	
	
	private List<String> listUser = null;
	
	private static Map<String, String> listFullUser;
	
	public DiscoveryListUser(String context)  {
		
		String token =SecurityTokenProvider.instance.get();
		log.debug("scope:{} ,tokend:{}",context,token);
		DiscoveryService discoveryList= new DiscoveryService(context);
		String urlService=discoveryList.getBasePath()+NAME_SERVICE_ALL_USERNAMES+"="+token;
		log.debug("service DiscoveryServiceListUser:"+urlService);
		String data = getJSON(urlService);
		//log.debug("data read:{}",data);
		ListUserInfo msg = new Gson().fromJson(data, ListUserInfo.class);
		listFullUser=msg.getResult();
		listUser = new ArrayList<String>(listFullUser.keySet());
		log.debug("listuser:{}",listUser);	
	}
	
	/**
	 * Get the base path of the social networking service
	 * @return
	 */
	public List<String> getListUser() {
		return listUser;
	}

	
	public  static String getMapUser(String identifier) {
		return listFullUser.get(identifier);
	}
	
	
	/**
	 * 
	 * @param url
	 * @return String response content
	 */
	public String getJSON(String url) {
	    HttpURLConnection c = null;
	    try {
	        URL u = new URL(url);
	        c = (HttpURLConnection) u.openConnection();
	        c.setRequestMethod("GET");
	        c.setRequestProperty("Content-length", "0");
	        c.setUseCaches(false);
	        c.setAllowUserInteraction(false);	  
	        c.setInstanceFollowRedirects(true);
	        c.connect();
	        int status = c.getResponseCode();

	        switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	                br.close();
	                return sb.toString();
	        }

	    } catch (MalformedURLException ex) {
	        log.error("MalformedURLException:{}",ex);
	        
	    } catch (IOException ex) {
	    	log.error("IOException:{}",ex);
	    } finally {
	       if (c != null) {
	          try {
	              c.disconnect();
	          } catch (Exception ex) {
	        	  log.error("Exception:{}",ex);
	          }
	       }
	    }
	    return null;
	}
}

