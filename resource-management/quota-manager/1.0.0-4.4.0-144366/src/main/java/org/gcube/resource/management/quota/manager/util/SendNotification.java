package org.gcube.resource.management.quota.manager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resource.management.quota.manager.check.QuotaUsage;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class SendNotification {
	private String identifier;
	private Double quotaAssigned;
	private Double quotaUsage;
	private Double percent;
	private String urlService;

	private DiscoveryConfiguration discoveryCheck;
	private final static String WRITE_MESSAGE = "2/messages/write-message";
	private final static String NAME_USERNAME_GLOBAL_ROLE = "2/users/get-usernames-by-global-role";
	private static Logger log = LoggerFactory.getLogger(QuotaUsage.class);	 
	
	public SendNotification(DiscoveryConfiguration discoveryCheck){
		this.discoveryCheck=discoveryCheck;
		String context = ScopeProvider.instance.get();
		DiscoveryService discoveryList= new DiscoveryService(context);
		urlService=discoveryList.getBasePath()+WRITE_MESSAGE;
	}
	
	public void configure(String identifier,Double quotaAssigned,Double quotaUsage,Double percent){
		this.identifier=identifier;
		this.quotaAssigned=quotaAssigned;
		this.quotaUsage=quotaUsage;
		this.percent=percent;
	}
	
	public boolean sendNotificationUser(String identifier, Double quotaAssigned, Double quotaUsage, Double percent, String type) throws JSONException{
		Boolean resultSend= false;
		if (discoveryCheck.getNotifierUser()){
			
			configure(identifier, quotaAssigned, quotaUsage, percent);
			log.debug("Notification Quota limit used:{} identifier:{} quotaAssigned:{} quotaUsage:{} ",this.percent,this.identifier,this.quotaAssigned,this.quotaUsage);
			
			String body=null;
			String subject=null;
			if (type=="warning"){
				body=CleanNotification(discoveryCheck.getTextQuotaUserWarning());
				subject=discoveryCheck.getTextQuotaUserWarningSubject();
			}
			if (type=="exceed"){
				body=CleanNotification(discoveryCheck.getTextQuotaUserExceed());
				subject=discoveryCheck.getTextQuotaUserExceedSubject();
			}
			if (type=="reset"){
				body=CleanNotification(discoveryCheck.getTextQuotaUserReset());
				subject=discoveryCheck.getTextQuotaUserResetSubject();
			}
			if (type=="change"){
				body=CleanNotification(discoveryCheck.getTextQuotaUserChange());
				subject=discoveryCheck.getTextQuotaUserChangeSubject();
			}
			String token = SecurityTokenProvider.instance.get();
			JSONObject urlParameters = new JSONObject();	
			List<String> user=new ArrayList<String>();			
			user.add(identifier);						
			urlParameters.put("recipients", user);
			urlParameters.put("body", body);
			urlParameters.put("subject", subject);		
			try {
				resultSend = writeMsg(urlService, urlParameters,token);				
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.debug("sendNotification user:{} url:{}, body:{}, subject:{}, send:{}",user,body,subject,resultSend);
		}
		else{
			log.debug("Not notification Quota limit used:{} identifier:{} quotaAssigned:{} quotaUsage:{} ",percent,identifier,quotaAssigned,quotaUsage);
		}
		return resultSend;
	}

	
	/**
	 * Send notification with a list of user exceed  to admin role  
	 * @param userExceedQuota
	 * @throws Exception
	 */
	public boolean SendNotificationAdmin(List<String> userExceedQuota) throws Exception{
		log.debug("SendNotificationAdmin for user:{}",userExceedQuota.toString());
		Boolean resultSend= false;
		if (userExceedQuota.size()>0){
			if (discoveryCheck.getNotifierAdmin()){
				 
				
				String token = SecurityTokenProvider.instance.get();
				String context = ScopeProvider.instance.get();

				DiscoveryService discoveryList= new DiscoveryService(context);
				
				
				String urlServiceRole=discoveryList.getBasePath()+NAME_USERNAME_GLOBAL_ROLE;
							
				
				log.debug("SendNotificationAdminservice DiscoveryServiceListUserRole:"+urlService);
				String data = getJsonResult(urlServiceRole,token,discoveryCheck.getRoleNotifier());
				log.debug("data read:{}",data);
				ListUser msg = new Gson().fromJson(data, ListUser.class);
				log.debug("SendNotificationAdmin msg:{}"+msg.toString());
				List<String> sendersList=msg.getResult();
				log.debug("SendNotificationAdmin sendersList:{}"+sendersList.toString());
				
				
				
				configure(identifier, quotaAssigned, quotaUsage, percent);
				
				String text=CleanNotification(discoveryCheck.getTextQuotaAdminExceed());
				String listUser = "";
				for (String s : userExceedQuota)
				{
					if (s!=null){
						listUser += "\n"+ s;
					}
				}
				text=text.replace("{listuser}", listUser);
				discoveryList= new DiscoveryService(context);
				String urlService=discoveryList.getBasePath()+WRITE_MESSAGE;
				log.debug("urlService:{}, text msg:{}",urlService,text);
			
				JSONObject urlParameters = new JSONObject();	
				List<String> user=new ArrayList<String>();
				for (String cc :sendersList){
					user.add(cc);
				}
				urlParameters.put("recipients", user);
				urlParameters.put("body", text);
				urlParameters.put("subject", discoveryCheck.getTextQuotaAdminExceedSubject());		
				try {
					resultSend = writeMsg(urlService, urlParameters,token);
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.debug("send notification quota exceed:{} send:{}",userExceedQuota.toString(),resultSend);
			}
			else{
				log.debug("not send notification quota exceed :{}",userExceedQuota.toString());
			}
		}
		return resultSend;
	
	}
	
	/**
	 * Clean a quota msg for notification
	 * @param notification
	 * @return notification with user,quota and percent
	 */
	private String CleanNotification(String notification){
		
		String fullname=DiscoveryListUser.getMapUser(identifier);
		log.debug("Clean Notification from username:{} to fullname:{}",identifier,fullname);

		if (notification.contains("{identifier}")){
			notification=notification.replace("{identifier}", fullname);
		}
		
		String quotaAssignedLabel;
		String quotaUsageLabel;
		String percentLabel;
		if (quotaAssigned>=1024){
			quotaAssignedLabel=String.valueOf(Math.round((quotaAssigned/1024) * 100.0) / 100.0)+"GB";
			quotaUsageLabel= String.valueOf(Math.round((quotaUsage/1024) * 100.0) / 100.0)+"GB";
			percentLabel=percent.toString();
		}
		else{
			quotaAssignedLabel=String.valueOf(quotaAssigned)+"MB";
			quotaUsageLabel= String.valueOf(quotaUsage)+"MB";
			percentLabel=percent.toString();
		}		
		if (quotaAssigned==-1){
			quotaAssignedLabel="∞";
			percentLabel="0";
		}
		
		if (notification.contains("{quotaAssigned}")){
			notification=notification.replace("{quotaAssigned}", quotaAssignedLabel);
		}
		if (notification.contains("{quotaUsage}")){			
				notification=notification.replace("{quotaUsage}", quotaUsageLabel);
		}
		if (notification.contains("{percent}")){
			notification=notification.replace("{percent}",percentLabel);
		}
		return notification;
	}
	
	
	/**
	 * 
	 * @param url
	 * @return String response content
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public Boolean writeMsg(String path, JSONObject urlParameters,String token) throws Exception {
	   
		Boolean result=false;
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = clientBuilder.build();
		HttpPost request = new HttpPost(path);		
		request.addHeader("gcube-token", token);
		request.addHeader("Content-type", ContentType.APPLICATION_JSON.toString());
		StringEntity params = new StringEntity(urlParameters.toString(), ContentType.APPLICATION_JSON);
		request.setEntity(params);
		HttpResponse response = httpClient.execute(request);
		log.debug("writeMsg path:{} urlParameters:{} request:{}",path,urlParameters,request.toString());
		int status = response.getStatusLine().getStatusCode();
		 
		// check the response status and look if it was a redirect problem
		if ((status ==HttpURLConnection.HTTP_INTERNAL_ERROR)|| (status==HttpURLConnection.HTTP_BAD_GATEWAY)){
			result=false;
		}
		else if (status != HttpURLConnection.HTTP_OK && (status == HttpURLConnection.HTTP_MOVED_TEMP ||
		        status == HttpURLConnection.HTTP_MOVED_PERM ||
		        status == HttpURLConnection.HTTP_SEE_OTHER)) {
		 
		    // redirect -> fetch new location
		    Header[] locations = response.getHeaders("Location");
		    Header lastLocation = locations[locations.length - 1];
		    String realLocation = lastLocation.getValue();
		    log.debug("New location is " + realLocation);
		 
		    // perform again the same request
		    request = new HttpPost(realLocation);
		    request.addHeader("gcube-token", token);
		    request.addHeader("Content-type", ContentType.APPLICATION_JSON.toString());
		    params = new StringEntity(urlParameters.toString(), ContentType.APPLICATION_JSON);
		    request.setEntity(params);
		    response = httpClient.execute(request);
		    log.info(" " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());
		    result=true;
		}else{
			log.debug(" " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());
			result=true;
		}
		return result;
	}

	/**
	 * Return a list user from global role
	 * @param url
	 * @param token
	 * @param role
	 * @return list of identifier for send msg 
	 */
	/*
	public String sendersList(String url,String token,String role){
		url=url+"?role-name="+role+"&gcube-token="+token;
		List<String>userList=new ArrayList<String>();
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
			log.debug("url:{} return list:{}",url,userList.toString());
			return null;		
	}
	*/
	/**
	 * 
	 * @param url
	 * @return String response content
	 */
	public String getJsonResult(String url,String token,String role) {
		url=url+"?role-name="+role+"&gcube-token="+token;
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
