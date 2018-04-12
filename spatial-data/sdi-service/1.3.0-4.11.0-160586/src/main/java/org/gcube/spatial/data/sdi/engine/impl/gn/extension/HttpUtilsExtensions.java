package org.gcube.spatial.data.sdi.engine.impl.gn.extension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;

import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.HTTPUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtilsExtensions extends HTTPUtils {


	private final static String JSON_CONTENT_TYPE="application/json";
	private final static String XML_CONTENT_TYPE="text/xml";





	public HttpUtilsExtensions() {
		super();
		// TODO Auto-generated constructor stub
	}

	String username;
	String pw;
	private int lastHttpStatus=0;

	public HttpUtilsExtensions(String userName, String password) {
		super(userName, password);
		this.username=userName;
		this.pw=password;
	}


	HttpClient client=new HttpClient();


	public String getJSON(String url) throws MalformedURLException, GNServerException {

		GetMethod httpMethod = null;
		try {            
			setAuth(client, url, username, pw);

			// creating call

			httpMethod = new GetMethod(url);

			//only actual difference from superclass
			httpMethod.setRequestHeader("Accept", JSON_CONTENT_TYPE);


			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			lastHttpStatus = client.executeMethod(httpMethod);
			if(lastHttpStatus == HttpStatus.SC_OK) {
				InputStream is = httpMethod.getResponseBodyAsStream();
				String response = IOUtils.toString(is);
				if(response.trim().length()==0) { // sometime gs rest fails
					log.warn("ResponseBody is empty");
					return null;
				} else {
					return response;
				}
			} else {
				log.info("("+lastHttpStatus+") " + HttpStatus.getStatusText(lastHttpStatus) + " -- " + url );
				throw new GNServerException("ERROR from calling "+url, lastHttpStatus);
			}
		} catch (ConnectException e) {
			log.info("Couldn't connect to ["+url+"]");
		} catch (IOException e) {
			log.info("Error talking to ["+url+"]", e);
		} finally {
			if(httpMethod != null)
				httpMethod.releaseConnection();
		}

		return null;
	}


	public String putJSON(String url, String content) throws UnsupportedEncodingException, GNServerException{
		PutMethod httpMethod=null;
		try {
			setAuth(client, url, username, pw);
			httpMethod=new PutMethod(url);
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			httpMethod.setRequestEntity(new StringRequestEntity(content,JSON_CONTENT_TYPE,"UTF-8"));


			//only actual difference from superclass
			httpMethod.setRequestHeader("Accept", JSON_CONTENT_TYPE);


			lastHttpStatus = client.executeMethod(httpMethod);

			
			
			if((lastHttpStatus>=200)&&(lastHttpStatus<300)){
				//OK responses
				log.debug("HTTP "+ httpMethod.getStatusText() + " <-- " + url);
				InputStream responseStream=httpMethod.getResponseBodyAsStream();
				if(super.isIgnoreResponseContentOnSuccess()||responseStream==null)
					return "";
				return IOUtils.toString(responseStream);
			}else{
				//NOT OK responses
				String badresponse = IOUtils.toString(httpMethod.getResponseBodyAsStream());
				String message = super.getGeoNetworkErrorMessage(badresponse);

				log.warn("Bad response: "+lastHttpStatus
						+ " " + httpMethod.getStatusText()
						+ " -- " + httpMethod.getName()
						+ " " +url
						+ " : "
						+ message
						);

				log.debug("GeoNetwork response:\n"+badresponse);
				throw new GNServerException("ERROR from calling "+url+". Message is "+badresponse, lastHttpStatus);
			}
			
		} catch (ConnectException e) {
			log.info("Couldn't connect to ["+url+"]");
			return null;
		} catch (IOException e) {
			log.error("Error talking to " + url + " : " + e.getLocalizedMessage());
			return null;
		} finally {
			if(httpMethod != null)
				httpMethod.releaseConnection();
		}
	}




	protected void setAuth(HttpClient client, String url, String username, String pw) throws MalformedURLException {
		URL u = new URL(url);
		if(username != null && pw != null) {
			Credentials defaultcreds = new UsernamePasswordCredentials(username, pw);
			client.getState().setCredentials(new AuthScope(u.getHost(), u.getPort()), defaultcreds);
			client.getParams().setAuthenticationPreemptive(true); // if we have the credentials, force them!
		} else {
			log.trace("Not setting credentials to access to " + url);
		}
	}
	
	private void reset(){
		// resets stats in subclass
		this.lastHttpStatus=0;
	}
	
	private boolean isReset(){
		return lastHttpStatus==0;
	}
	
	@Override
	public int getLastHttpStatus() {
		if(isReset())
		return super.getLastHttpStatus();
		else return this.lastHttpStatus;
	}

	
	// OVERRIDING superclass methods in order to discriminate on lastHttpStatus member
	
	@Override
	public boolean delete(String arg0) {
		reset();
		return super.delete(arg0);
	}
	
	@Override
	public boolean exists(String arg0) {
		reset();
		return super.exists(arg0);
	}
	
	@Override
	public String get(String arg0) throws MalformedURLException {
		reset();
		return super.get(arg0);
	}
	
	@Override
	public boolean httpPing(String arg0) {
		reset();
		return super.httpPing(arg0);
	}
	
	@Override
	public String post(String arg0, String arg1, String arg2, String arg3) {
		reset();
		return super.post(arg0, arg1, arg2, arg3);
	}
	
	@Override
	public String post(String url, File file, String contentType) {
		reset();
		return super.post(url, file, contentType);
	}
	
	@Override
	public String post(String url, InputStream content, String contentType) {
		reset();
		return super.post(url, content, contentType);
	}
	
	@Override
	public String post(String url, RequestEntity requestEntity) {
		reset();
		return super.post(url, requestEntity);
	}
	
	@Override
	public String post(String url, String content, String contentType) {
		reset();
		return super.post(url, content, contentType);
	}
	
	@Override
	public String postXml(String url, InputStream content) {
		reset();
		return super.postXml(url, content);
	}
	
	@Override
	public String postXml(String url, String content) {
		reset();
		return super.postXml(url, content);
	}
	
	@Override
	public String postXml(String url, String content, String encoding) {
		reset();
		return super.postXml(url, content, encoding);
	}
	
	@Override
	public String put(String arg0, String arg1, String arg2) {
		reset();
		return super.put(arg0, arg1, arg2);
	}
	
	@Override
	public String put(String url, File file, String contentType) {
		reset();
		return super.put(url, file, contentType);
	}
	@Override
	public String put(String url, RequestEntity requestEntity) {
		reset();
		return super.put(url, requestEntity);
	}
	
	@Override
	public String putXml(String url, String content) {
		reset();
		return super.putXml(url, content);
	}
	
	
}
