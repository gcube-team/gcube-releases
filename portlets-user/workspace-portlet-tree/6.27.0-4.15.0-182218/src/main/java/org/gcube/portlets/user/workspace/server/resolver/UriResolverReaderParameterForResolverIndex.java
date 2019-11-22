/**
 *
 */
package org.gcube.portlets.user.workspace.server.resolver;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.workspace.server.util.UrlEncoderUtil;
import org.gcube.portlets.user.workspace.server.util.scope.ScopeUtilFilter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class UriResolverReaderParameter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jan 12, 2016
 */
public class UriResolverReaderParameterForResolverIndex {

	protected static final String CONTENT_TYPE_PARAMETER = "contentType_parameter";
	protected static final String FILE_NAME_PARAMETER = "fileName_parameter";
	protected static final String SMP_ID_PARAMETER = "SMP_ID_parameter";

	//Base Address
	private String baseUri = "";
    //Query URL parameter
	private String storageIDParameter = "";
	private String fileNameParameter = "";
	private String contentTypeParameter = "";
	private String query = "";
	private boolean isAvailable = false;
	public static Logger logger = LoggerFactory.getLogger(UriResolverReaderParameterForResolverIndex.class);

	/**
	 * The Enum RESOLVER_TYPE.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
	 * Jan 12, 2016
	 */
	public static enum RESOLVER_TYPE {SMP_URI, SMP_ID};

	/**
	 * Instantiates a new uri resolver reader parameter for resolver index.
	 *
	 * @param scope the scope
	 * @param resolverType the resolver type
	 * @throws Exception the exception
	 */
	public UriResolverReaderParameterForResolverIndex(String scope, RESOLVER_TYPE resolverType) throws Exception {

		try{
			logger.trace("UriResolverReaderParameter is istancing with scope: "+scope);
			ScopeUtilFilter scopeUtil = new ScopeUtilFilter(scope,true);
			ScopeProvider.instance.set(scopeUtil.getScopeRoot());
			XQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/text() eq 'HTTP-URI-Resolver'").setResult("$resource/Profile/AccessPoint");

			DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
			List<AccessPoint> endpoints = client.submit(query);

			if (endpoints.size() == 0)
				throw new Exception("No Resolver available");

			//THE FIRST ACCESS POINT (endpoints.get(0)) IS SMP-URI, THE SECOND (endpoints.get(1)) IS SMP-ID
			int useResolverIndex;

			switch(resolverType){
			case SMP_ID:
				useResolverIndex = 1;
				break;
			case SMP_URI:
				useResolverIndex = 0;
				break;
			default:
				useResolverIndex = 1;
			}

			AccessPoint ap2 = endpoints.get(useResolverIndex);
			baseUri = ap2!=null?endpoints.get(useResolverIndex).address():"";
		    if(ap2!=null){
		    	storageIDParameter = ap2.propertyMap()!=null?ap2.propertyMap().get(SMP_ID_PARAMETER).value():"";
		    	fileNameParameter = ap2.propertyMap()!=null?ap2.propertyMap().get(FILE_NAME_PARAMETER).value():"";
		    	contentTypeParameter = ap2.propertyMap()!=null?ap2.propertyMap().get(CONTENT_TYPE_PARAMETER).value():"";
		    }
		    isAvailable = true;
		}catch(Exception e){

		}finally{
			ScopeProvider.instance.reset();
		}

	}

	/**
	 * Resolve - open stream with http get method.
	 *
	 * @param smp the smp
	 * @param fileName the file name
	 * @param contentType the content type
	 * @return the input stream
	 * @throws Exception the exception
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
	 * Resolve as uri request.
	 *
	 * @param storageID the storage id
	 * @param fileName the file name
	 * @param contentType the content type
	 * @param encodeParams - if true, translates a string into <code>application/x-www-form-urlencoded</code>
	 * format using a specific encoding scheme
	 * @return the http url to get file
	 * @throws Exception the exception
	 */
	public String resolveAsUriRequest(String storageID, String fileName, String contentType, boolean encodeParams) throws Exception{

		if(storageID==null || storageID.isEmpty())
			throw new Exception("storage ID is null or empty");

		if(!encodeParams){ //ENCODE URI
			query = storageIDParameter+"="+storageID;

			if(fileName!=null && !fileName.isEmpty())
				query+="&"+fileNameParameter+"="+fileName;

			if(contentType!=null && !contentType.isEmpty())
				query+="&"+contentTypeParameter+"="+contentType;
		}
		else{
			Map<String, String> hashParameters = getHashParameters(storageID, fileName, contentType);
			query = UrlEncoderUtil.encodeQueryValues(hashParameters);
		}
		String uriRequest = baseUri+"?"+query;
		logger.trace("resolve url request: "+uriRequest);
		return uriRequest;
	}



	/**
	 * Resolve as storage id request.
	 *
	 * @param storageID the storage id
	 * @param encodeParams the encode params
	 * @return the string
	 * @throws Exception the exception
	 */
	public String resolveAsStorageIdRequest(String storageID, boolean encodeParams) throws Exception{

		if(storageID==null || storageID.isEmpty())
			throw new Exception("storage ID is null or empty");

		if(!encodeParams)//ENCODE URI
			query = storageID;
		else
			query = UrlEncoderUtil.encodeQueryValue(storageID);

		if(baseUri.endsWith("/id"))
			baseUri = baseUri.substring(0, baseUri.length()-3);

		String uriRequest = baseUri+"/"+query;
		logger.trace("resolve storageID request: "+uriRequest);
		return uriRequest;
	}


	/**
	 * Gets the hash parameters.
	 *
	 * @param storageID the storage id
	 * @param fileName the file name
	 * @param contentType the content type
	 * @return the hash parameters
	 * @throws Exception the exception
	 */
	public Map<String, String> getHashParameters(String storageID, String fileName, String contentType) throws Exception{

		Map<String, String> hashParameters = new HashMap<String, String>();

		if(storageID==null || storageID.isEmpty())
			throw new Exception("smp url is null or empty");

		hashParameters.put(storageIDParameter, storageID);

		if(fileName!=null && !fileName.isEmpty())
			hashParameters.put(fileNameParameter, fileName);

		if(contentType!=null && !contentType.isEmpty())
			hashParameters.put(contentTypeParameter, contentType);

		return hashParameters;
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
	 * Gets the base uri.
	 *
	 * @return the base uri
	 */
	public String getBaseUri() {
		return baseUri;
	}

	/**
	 * Gets the storage id parameter.
	 *
	 * @return the storageIDParameter
	 */
	public String getStorageIDParameter() {
		return storageIDParameter;
	}

	/**
	 * Gets the file name parameter.
	 *
	 * @return the file name parameter
	 */
	public String getFileNameParameter() {
		return fileNameParameter;
	}

	/**
	 * Gets the content type parameter.
	 *
	 * @return the content type parameter
	 */
	public String getContentTypeParameter() {
		return contentTypeParameter;
	}

	/**
	 * Gets the query.
	 *
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UriResolverReaderParameter [baseUri=");
		builder.append(baseUri);
		builder.append(", storageIDParameter=");
		builder.append(storageIDParameter);
		builder.append(", fileNameParameter=");
		builder.append(fileNameParameter);
		builder.append(", contentTypeParameter=");
		builder.append(contentTypeParameter);
		builder.append(", isAvailable=");
		builder.append(isAvailable);
		builder.append("]");
		return builder.toString();
	}

	/*
	public static void main(String[] args) throws Exception {
		String scope = "/gcube/devsec";
		System.out.println(new UriResolverReaderParameter(scope).toString());
//		UriResolverReaderParameter uriResolver = new UriResolverReaderParameter(scope);
//		String fileName = "Wikipedia_logo_silver.png";
//		String uriRequest = uriResolver.resolveAsUriRequest("smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkU4GKPsiD6PDRVcT4QAqTEy5hSIbr6o4Y", fileName, "image/png", true);
//		System.out.println("uriRequest "+uriRequest);
//		InputStream is = uriResolver.resolve("smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkU4GKPsiD6PDRVcT4QAqTEy5hSIbr6o4Y", fileName, "image/png");
//		File file = new File(fileName);
//		FileOutputStream out = new FileOutputStream(file);
//		IOUtils.copy(is, out);
//		is.close();
//		out.close();
	}*/

}
