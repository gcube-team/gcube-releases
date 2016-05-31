/**
 *
 */
package org.gcube.portlets.user.workspace.server.shortener;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 28, 2013
 *
 */
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.workspace.server.util.scope.ScopeUtilFilter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.json.JSONObject;
import org.json.JSONTokener;



public final class UrlShortener {

	/**
	 *
	 */
	protected static final String NO_HTTP_URL_SHORTENER_AVAILABLE = "No HTTP URL Shortener available";
	protected static final String APPLICATION_JSON = "application/json";
	protected static final String LONG_URL = "longUrl";

//	protected static String shortnerServerUrl = "https://www.googleapis.com/urlshortener/v1/url";
//	protected static String urlMethod = "";
//	protected static String authenticationKeyParam = "key";
//	protected static String authenticationKeyValue ="AIzaSyDfMO0VY3o8GjRUqnTfqScjm_EsFEuBa3g";

	private static String shortnerServerUrl = "";
	protected static final String KEY = "key";
	private static String paramKeyValue ="";

	protected static Logger logger = Logger.getLogger(UrlShortener.class);
	private String uriRequest;
	private boolean isAvailable = false;

	/*
 * @throws Exception
 * @throws InvalidResultException
 * @throws DiscoveryException
 * @throws IllegalStateException *
	 *
	 */
	public UrlShortener(String scope) throws Exception {

		try{
			logger.trace("Tentative reading HTTP-URL-Shortener RR");

			ScopeUtilFilter scopeUtil = new ScopeUtilFilter(scope,true);

			ScopeProvider.instance.set(scopeUtil.getScopeRoot());

			XQuery query = queryFor(ServiceEndpoint.class);

			query.addCondition("$resource/Profile/Name/text() eq 'HTTP-URL-Shortener'").setResult("$resource/Profile/AccessPoint");

			DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);

			List<AccessPoint> endpoints = client.submit(query);

			if (endpoints.size() == 0){
				isAvailable = false;
				logger.trace("HTTP-URL-Shortener not found");
				throw new Exception(NO_HTTP_URL_SHORTENER_AVAILABLE);
			}

			//Base Address
	//	    System.out.println(endpoints.get(0).address());

			shortnerServerUrl = endpoints.get(0)!=null?endpoints.get(0).address():"";

		    if(endpoints.get(0)!=null){

		    	paramKeyValue = endpoints.get(0).propertyMap()!=null?endpoints.get(0).propertyMap().get(KEY).value():"";
		    }

		   	String getParam = "";
		   	logger.trace("paramKeyValue: "+paramKeyValue);

	         //ACTIVE THIS AFTER RELEASE
            if(paramKeyValue!=null && !paramKeyValue.isEmpty()){
            	getParam = "?"+KEY +"="+paramKeyValue;
            }

		    uriRequest  = shortnerServerUrl+getParam;

		    logger.trace("HTTP-URL-Shortener found, uriRequest: "+uriRequest);

		    //DEBUG
//		    System.out.println("HTTP-URL-Shortener found, uriRequest: "+uriRequest);

		    isAvailable = true;

		}catch (Exception e) {
			isAvailable = false;
			logger.error("an error occurred in reading RR: ", e);
			throw new Exception(NO_HTTP_URL_SHORTENER_AVAILABLE);
		}finally{
			ScopeProvider.instance.reset();
		}
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	/**
	 *
	 * @param longUrl
	 * @return a shorten url
	 */
    public String shorten(String longUrl) throws Exception{

        if (longUrl == null) {
            return longUrl;
        }

        try {

        	logger.trace("shorten longUrl: "+longUrl);

            URL url = new URL(uriRequest);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", APPLICATION_JSON);

            JSONObject jsonObj = new JSONObject();
            jsonObj.put(LONG_URL, longUrl);

//            //ACTIVE THIS AFTER RELEASE
//            if(paramKeyValue!=null && !paramKeyValue.isEmpty()){
//            	jsonObj.put(KEY, paramKeyValue);
//            }

        	logger.trace("request json : "+jsonObj.toString());

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(jsonObj.toString());
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            JSONTokener tokener = new JSONTokener(rd);
            JSONObject jsonObject = new JSONObject(tokener);
//            JSONParser parser = new JSONParser();
//            JSONObject jsonObject = (JSONObject) parser.parse(rd);
            logger.trace("response received json : "+jsonObject.toString());

            wr.close();
            rd.close();
    		return (String) jsonObject.get("id"); //is shorted url

        } catch (MalformedURLException e) {

        	logger.error("MalformedURLException error in UrlShortener", e);
            return longUrl;

        } catch (IOException e) {
        	e.printStackTrace();
        	logger.error("IOException error in UrlShortener", e);
            return longUrl;
        }
    }

	public static String getShortnerServerUrl() {
		return shortnerServerUrl;
	}

	public static String getKey() {
		return KEY;
	}

	public static String getParamKeyValue() {
		return paramKeyValue;
	}

	public String getUriRequest() {
		return uriRequest;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UrlShortener [uriRequest=");
		builder.append(uriRequest);
		builder.append(", isAvailable=");
		builder.append(isAvailable);
		builder.append("]");
		return builder.toString();
	}



    public static void main(String[] args) {
    	String shorten;

		try {

			UrlShortener urlSh = new UrlShortener("/d4science.research-infrastructures.eu");
			System.out.println("UrlShortener: "+urlSh);

			shorten = urlSh.shorten("https://dev.d4science.org/group/data-e-infrastructure-gateway/workspace?itemid=056648-c7ce-4de3-a4c7-e1411816cc12&operation=gotofolder");

			System.out.println("Shorted: "+shorten);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}