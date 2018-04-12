/**
 * 
 */
package org.gcube.applicationsupportlayer.social.storage;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 26, 2013
 *
 */
public class UriResolverReaderParameter {
	
	//Base Address
//	private String uri = "";
	private String baseUri = "";
	
    //Query URL parameter
	private String smpParameter = "";
	private String fileNameParameter = "";
	private String contentTypeParameter = "";
	
	
	private String query = "";
	
	private boolean isAvailable = false;
	
	
	public static Logger logger = Logger.getLogger(UriResolverReaderParameter.class);
	
	
	/**
	 * @param context the current infrastructure context (scope)
	 * @throws Exception 
	 * 
	 */
	public UriResolverReaderParameter(String context) throws Exception {
	
		ScopeProvider.instance.set(context);
		 
		XQuery query = queryFor(ServiceEndpoint.class);
	 
		query.addCondition("$resource/Profile/Name/text() eq 'HTTP-URI-Resolver'").setResult("$resource/Profile/AccessPoint");
	 
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
	 
		List<AccessPoint> endpoints = client.submit(query);
	 
		if (endpoints.size() == 0)
			throw new Exception("No Resolver available");

		baseUri = endpoints.get(0)!=null?endpoints.get(0).address():"";
	        
	    if(endpoints.get(0)!=null){
	    	
	    	smpParameter = endpoints.get(0).propertyMap()!=null?endpoints.get(0).propertyMap().get("SMP_URI_parameter").value():"";
	    	fileNameParameter = endpoints.get(0).propertyMap()!=null?endpoints.get(0).propertyMap().get("fileName_parameter").value():"";
	    	contentTypeParameter = endpoints.get(0).propertyMap()!=null?endpoints.get(0).propertyMap().get("contentType_parameter").value():"";
	    }
	    
//	    uriRequest  = uri+"?"+smpParameter;
	    
	    isAvailable = true;
	     //Query URL parameter
//		System.out.println(endpoints.get(0).propertyMap().get("parameter").value());
	    
	}
	
	/**
	 * Resolve - open stream with http get method
	 * @param smp
	 * @return
	 * @throws IOException
	 */
	public InputStream resolveAsInputStream(String smp, String fileName, String contentType) throws Exception{

		String query = resolveAsUriRequest(smp, fileName, contentType, true);

		URL url = new URL(query);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
	    connection.setDoOutput(true); 
	    connection.setInstanceFollowRedirects(false); 
	    connection.setRequestMethod("GET"); 
	    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
	    connection.setRequestProperty("charset", "utf-8");
	    connection.connect();
	    return connection.getInputStream();

	}
	

	/**
	 * 
	 * @param smp
	 * @param fileName
	 * @param contentType
	 * @param encodeParams - if true, translates a string into <code>application/x-www-form-urlencoded</code>
     * format using a specific encoding scheme
	 * @return the http url to get file
	 * @throws Exception
	 */
	public String resolveAsUriRequest(String smp, String fileName, String contentType, boolean encodeParams) throws Exception{
		
		if(smp==null || smp.isEmpty())
			throw new Exception("smp url is null or empty");
		

		
		if(!encodeParams){ //ENCODE URI
			query = smpParameter+"="+smp;
		
			if(fileName!=null && !fileName.isEmpty())
				query+="&"+fileNameParameter+"="+fileName;
		
			if(contentType!=null && !contentType.isEmpty())
				query+="&"+contentTypeParameter+"="+contentType;
		}
		else{
			Map<String, String> hashParameters = getHashParemeters(smp, fileName, contentType);
			query = UrlEncoderUtil.encodeQuery(hashParameters);
		}
			
		String uriRequest = baseUri+"?"+query;
		
		logger.trace("resolve url request: "+uriRequest);
		
		return uriRequest;

	}
	
	
	
	/**
	 * 
	 * @param smp
	 * @param fileName
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getHashParemeters(String smp, String fileName, String contentType) throws Exception{
		
		Map<String, String> hashParameters = new HashMap<String, String>();
		
		if(smp==null || smp.isEmpty())
			throw new Exception("smp url is null or empty");
		
		hashParameters.put(smpParameter, smp);
		
		if(fileName!=null && !fileName.isEmpty())
			hashParameters.put(fileNameParameter, fileName);
		
		if(contentType!=null && !contentType.isEmpty())
			hashParameters.put(contentTypeParameter, contentType);	

		return hashParameters;
	}
	
	
	public boolean isAvailable() {
		return isAvailable;
	}
	
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UriResolverReaderParameter [baseUri=");
		builder.append(baseUri);
		builder.append(", smpParameter=");
		builder.append(smpParameter);
		builder.append(", fileNameParameter=");
		builder.append(fileNameParameter);
		builder.append(", contentTypeParameter=");
		builder.append(contentTypeParameter);
		builder.append(", isAvailable=");
		builder.append(isAvailable);
		builder.append("]");
		return builder.toString();
	}

	public String getBaseUri() {
		return baseUri;
	}

	public String getSmpParameter() {
		return smpParameter;
	}

	public String getFileNameParameter() {
		return fileNameParameter;
	}

	public String getContentTypeParameter() {
		return contentTypeParameter;
	}

	

	public String getQuery() {
		return query;
	}

}
