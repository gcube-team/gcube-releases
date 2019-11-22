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
import java.util.Map;

import org.gcube.common.encryption.encrypter.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
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
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 31, 2019
 */
public final class UrlShortener {


	protected static final String APPLICATION_JSON = "application/json";
	protected static final String LONG_DYNAMIC_LINK = "longDynamicLink";
	protected static final String SHORTENER = "HTTP-URL-Shortener-DL";
	protected static final String KEY = "key";
	protected static final String DYNAMIC_LINK = "dynamic-link";
	protected static final String SUFFIX = "suffix";
	private static String shortnerServiceUrl = null;
	private static String decryptedKEY = null;
	private static String suffixOption = null;
	private static String dynamicLink = null;

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
			logger.info("Trying to read the Runtime Resource with name {}, in the scope: {}",SHORTENER,scope);

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
				throw new Exception("No Runtime Resource with name: "+SHORTENER+" is available in the scope: "+scope);
			}
			
			AccessPoint shortnerAP = endpoints.get(0);
			
		    if(shortnerAP!=null){
		    	shortnerServiceUrl = shortnerAP.address();
		    	//READING THE PROPERTIES
		    	Map<String, Property> propertiesMap = shortnerAP.propertyMap();
		    	if(propertiesMap==null || propertiesMap.size()==0) {
		    		throw new Exception("You need to add the configurations as properties to "+SHORTENER);
		    	}
		    	
		    	//READING KEY
		    	String encrKey = readPropertyValue(propertiesMap, KEY);
		    	logger.debug("Read property with name {} and value {}", KEY, encrKey);
		    	if(encrKey!=null) {
		    		decryptedKEY = StringEncrypter.getEncrypter().decrypt(encrKey);
		    		logger.debug("Decrypted {}: {}+++MASKED-KEY+++", KEY, decryptedKEY.substring(0, decryptedKEY.length()-10));
		    	}
		    	
		    	dynamicLink = readPropertyValue(propertiesMap, DYNAMIC_LINK);
		    	logger.debug("Read property with name {} and value {}", DYNAMIC_LINK, dynamicLink);
		    	suffixOption = readPropertyValue(propertiesMap, SUFFIX);
		    	logger.debug("Read property with name {} and value {}", SUFFIX, suffixOption);
		    }
		    
		    if(shortnerServiceUrl==null || decryptedKEY==null) {
		    	String error = "The access point in the RR: "+SHORTENER+" for scope "+scope+" is not available correctly. Impossible to read the service URL or decrypt the password";
		    	logger.warn(error);
		    	throw new Exception(error);
		    }
		    
			logger.trace("The shortner service URL is: {}",shortnerServiceUrl);
		   	String getParam = "";

	         //ACTIVE THIS AFTER RELEASE
            if(decryptedKEY!=null && !decryptedKEY.isEmpty()){
            	getParam = "?"+KEY +"="+decryptedKEY;
            	//getParam = "?"+KEY +"=AIzaSyCRGq1oO76Xi-pQEMPbdGWIUGVwueVbE5Y";
            }

		    uriRequest  = shortnerServiceUrl+getParam;
		    logger.info("HTTP-URL-Shortener found in scope {}, uriRequest: {}... KEY MASKED",scope, uriRequest.substring(0,uriRequest.length()-10));
		    isAvailable = true;

		}catch (Exception e) {
			logger.error("An error occurred reading Runtime Resource for name: "+SHORTENER+", the scope is: "+scope, e);
			isAvailable = false;
			throw new Exception("No "+SHORTENER+" available!");
		}
	}
	
	
	/**
	 * Read property value.
	 *
	 * @param propertiesMap the properties map
	 * @param key the key
	 * @return the string
	 * @throws Exception the exception
	 */
	public String readPropertyValue(Map<String, Property> propertiesMap, String key) throws Exception{
		Property theProperty = propertiesMap.get(key)!=null?propertiesMap.get(key):null;
		if(theProperty==null)
			throw new Exception("You need to add the "+key+" Property in the "+SHORTENER);
		
		return theProperty.value();
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

        	logger.debug("shorten the input longUrl: "+longUrl);

            URL url = new URL(uriRequest);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", APPLICATION_JSON);

            JSONObject jsonObj = new JSONObject();
            //jsonObj.put(LONG_URL, longUrl);
            String toLongURL = String.format("%s?link=%s",dynamicLink,longUrl);
            jsonObj.put(LONG_DYNAMIC_LINK, toLongURL);
            
            JSONObject jsonObjSuffix = null;
            //suffixOption = null;
            if(suffixOption!=null) {
            	 jsonObjSuffix = new JSONObject(suffixOption);
            }else {
            	//DEFAULT OPTION IS SHORT
	            jsonObjSuffix = new JSONObject();
	            jsonObjSuffix.put("option", "SHORT");
            }
            
            jsonObj.put("suffix", jsonObjSuffix);
//            //ACTIVE THIS AFTER RELEASE
//            if(paramKeyValue!=null && !paramKeyValue.isEmpty()){
//            	jsonObj.put(KEY, paramKeyValue);
//            }
            
            logger.debug("Performing POST request to URI: {}+++MASKED-KEY+++",uriRequest.substring(0,uriRequest.length()-10));
        	logger.debug("the body contains the JSON request: \n{}",jsonObj.toString(3));
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(jsonObj.toString());
            wr.flush();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            JSONTokener tokener = new JSONTokener(rd);
            JSONObject jsonObject = new JSONObject(tokener);
            logger.debug("response contains the json: "+jsonObject.toString());

            wr.close();
            rd.close();
    		return (String) jsonObject.get("shortLink"); //it is the SHORT URL

        } catch (MalformedURLException e) {
        	logger.error("MalformedURLException error in UrlShortener", e);
            return longUrl;

        } catch (IOException e) {
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
	@Deprecated
	public static String getShortnerServerUrl() {
		return shortnerServiceUrl;
	}
	
	/**
	 * Gets the shortner service url.
	 *
	 * @return the shortner service url
	 */
	public static String getShortnerServiceUrl() {
		return shortnerServiceUrl;
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
		return decryptedKEY;
	}

	/**
	 * Gets the uri request.
	 *
	 * @return the uri request
	 */
	public String getUriRequest() {
		return uriRequest;
	}
	
	

	/**
	 * Gets the suffix option.
	 *
	 * @return the suffix option
	 */
	public static String getSuffixOption() {
		return suffixOption;
	}


	/**
	 * Gets the dynamic link.
	 *
	 * @return the dynamic link
	 */
	public static String getDynamicLink() {
		return dynamicLink;
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