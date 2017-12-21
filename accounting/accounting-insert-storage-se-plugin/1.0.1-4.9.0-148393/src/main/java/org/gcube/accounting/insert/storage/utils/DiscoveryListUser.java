package org.gcube.accounting.insert.storage.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

	private List<String> listUser = null;
	public DiscoveryListUser(String context,String urlService)  {


		try{
			String token =SecurityTokenProvider.instance.get();
			DiscoveryServiceListUser discoveryList= new DiscoveryServiceListUser(context);
			if (urlService==null){
				urlService=discoveryList.getBasePath()+"="+token;
			}
			log.debug("service DiscoveryServiceListUser:"+urlService);
			log.debug("scope:{} ,tokend:{}",context,token);
			String data = getJSON(urlService);
			log.debug("data read:{}",data);
			ListUser msg = new Gson().fromJson(data, ListUser.class);
			//TODO for debug limit a list user:
			listUser=msg.getResult();
		}
		catch (Exception ex) {
			log.error("DiscoveryListUserException:{}",ex);
			throw ex ;
		}
	}

	/**
	 * Get the base path of the social networking service
	 * @return
	 */
	public List<String> getListUser() {
		return listUser;
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
			c.setFollowRedirects(true);	        
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);	     
			c.connect();
			int status = c.getResponseCode();
			log.debug("status:{}",status);
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

