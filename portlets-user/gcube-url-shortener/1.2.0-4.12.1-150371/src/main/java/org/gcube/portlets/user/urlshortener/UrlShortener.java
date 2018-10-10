/**
 *
 */
package org.gcube.portlets.user.urlshortener;

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

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class UrlShortener.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 14, 2017
 */
public final class UrlShortener {

	protected static final String APPLICATION_JSON = "application/json";
	protected static final String LONG_URL = "longUrl";
	protected static final String SHORTENER = "HTTP-URL-Shortener";
	private static String shortnerServerUrl = "";
	protected static final String KEY = "key";
	private static String paramKeyValue ="";

	protected static Logger logger = LoggerFactory.getLogger(UrlShortener.class);
	private String uriRequest;
	private boolean isAvailable = false;


	/**
	 * Instantiates a new url shortener.
	 * It uses the scope from {@link ScopeProvider#instance} to read the Runtime Resource {@link UrlShortener#SHORTENER}}
	 * @throws Exception the exception
	 */
	public UrlShortener() throws Exception{
		this(ScopeProvider.instance.get());
	}


	/**
	 * Instantiates a new url shortener.
	 *
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	private UrlShortener(String scope) throws Exception {

		try{
			logger.info("Tentative reading Runtime Resource for name: "+SHORTENER+", the scope is: "+scope);

			if(scope==null || scope.isEmpty()){
				String msg = "Scope is null or empty! You must set the scope into: "+ScopeProvider.instance.getClass();
				throw new Exception(msg);
			}

			XQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/text() eq '"+SHORTENER+"'").setResult("$resource/Profile/AccessPoint");
			DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
			List<AccessPoint> endpoints = client.submit(query);

			if (endpoints.size() == 0){
				isAvailable = false;
				logger.warn("Runtime Resource " +SHORTENER+ " not found");
				throw new Exception("No Runtime Resource naming: "+SHORTENER+" available in the scope: "+scope);
			}

			shortnerServerUrl = endpoints.get(0)!=null?endpoints.get(0).address():"";

		    if(endpoints.get(0)!=null){
		    	paramKeyValue = endpoints.get(0).propertyMap()!=null?endpoints.get(0).propertyMap().get(KEY).value():"";
		    }

		   	String getParam = "";

	         //ACTIVE THIS AFTER RELEASE
            if(paramKeyValue!=null && !paramKeyValue.isEmpty()){
            	getParam = "?"+KEY +"="+paramKeyValue;
            }

		    uriRequest  = shortnerServerUrl+getParam;
		    logger.info("HTTP-URL-Shortener found in scope "+scope+", uriRequest: "+uriRequest);
		    isAvailable = true;

		}catch (Exception e) {
			logger.error("An error occurred reading Runtime Resource for name: "+SHORTENER+", the scope is: "+scope, e);
			isAvailable = false;
			throw new Exception("No "+SHORTENER+" available!");
		}
	}

	/**
	 * Shorten.
	 *
	 * @param longUrl the long url
	 * @return a shorten url
	 * @throws Exception the exception
	 */
    public String shorten(String longUrl) throws Exception{

        if (longUrl == null) {
            return longUrl;
        }

        try {

        	logger.debug("shorten longUrl: "+longUrl);

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

        	logger.debug("request json : "+jsonObj.toString());

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(jsonObj.toString());
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            JSONTokener tokener = new JSONTokener(rd);
            JSONObject jsonObject = new JSONObject(tokener);
//            JSONParser parser = new JSONParser();
//            JSONObject jsonObject = (JSONObject) parser.parse(rd);
            logger.debug("response received json : "+jsonObject.toString());

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

	/**
	 * Checks if is available.
	 *
	 * @return true, if is available
	 */
	public boolean isAvailable() {
		return isAvailable;
	}

	/**
	 * Gets the shortner server url.
	 *
	 * @return the shortner server url
	 */
	public static String getShortnerServerUrl() {
		return shortnerServerUrl;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public static String getKey() {
		return KEY;
	}

	/**
	 * Gets the param key value.
	 *
	 * @return the param key value
	 */
	public static String getParamKeyValue() {
		return paramKeyValue;
	}

	/**
	 * Gets the uri request.
	 *
	 * @return the uri request
	 */
	public String getUriRequest() {
		return uriRequest;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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

}