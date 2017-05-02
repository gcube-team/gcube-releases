/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server.GcubeCkanDataCatalogServiceImpl;


/**
 * The Class CkanConnectorAccessPoint.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2016
 */
public class CkanConnectorAccessPoint implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 3771903616375335843L;

	public static final String URL_PATH_SEPARATOR = "/";

	public static final String LIST_OF_VRES_PARAMETER = "listOfVres";
	private static final String GCUBE_TOKEN_PARAMETER = "gcube-token";
	private String baseUrl;
	private String pathInfoParameter;
	private String queryStringParameters;
	private String gcubeTokenValue;
	private Map<String, String> mapVresRoles;
	private String serverviceContext;
	private boolean outsideFromPortal = false;
	private String catalogueBaseUrl = null;

	/**
	 * Instantiates a new ckan connector access point.
	 */
	public CkanConnectorAccessPoint() {
	}


	/**
	 * Instantiates a new ckan connector access point.
	 *
	 * @param baseUrl the base url
	 * @param serverContext the server context
	 */
	public CkanConnectorAccessPoint(String baseUrl, String servericeContext){
		this.baseUrl = baseUrl;
		this.catalogueBaseUrl = baseUrl.split("ckan-connector")[0];
		this.serverviceContext = servericeContext;
	}

	/**
	 * Adds the generic parameter.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void addGenericParameter(String key, String value){
		queryStringParameters=queryStringParameters==null?key+"="+value:queryStringParameters+"&"+key+"="+value;
	}


	/**
	 * Adds the query string.
	 *
	 * @param queryString the query string
	 */
	public void addQueryString(String queryString){

		if(queryString==null || queryString.isEmpty())
			return;

		if(queryString.startsWith("&") || queryString.startsWith("?")){
			queryString = queryString.substring(1, queryString.length()); //removes '&' or '?'
		}

		queryStringParameters=queryStringParameters==null?queryString:queryStringParameters+"&"+queryString;
	}

	/**
	 * Adds the gube token.
	 *
	 * @param value the value
	 */
	public void addGubeToken(String value){
		if(!outsideFromPortal){
			this.gcubeTokenValue = value;
			addGenericParameter(GCUBE_TOKEN_PARAMETER, value);
		}
	}

	/**
	 * Adds the list of vr es.
	 *
	 * @param listVREs the list vr es
	 */
	public void addListOfVREs(Map<String, String> listVREs){
		this.mapVresRoles = listVREs;
		String vres = "";
		if(listVREs!=null && listVREs.size()>0){
			Set<Entry<String, String>> set = listVREs.entrySet();
			for (Entry<String, String> entry : set) {
				vres += entry.getKey().toLowerCase() + "|" + entry.getValue() + ",";
			}
			vres = vres.substring(0, vres.length()-1); //remove last "," and to lower case. A CKAN Organization ID must be lower case
		}

		if(vres.length()>0){
			addGenericParameter(LIST_OF_VRES_PARAMETER, vres);
		}
	}

	/**
	 * Adds the path info.
	 *
	 * @param pathInfo the path info
	 */
	public void addPathInfo(String pathInfo){
		pathInfoParameter = pathInfo;
	}


	/**
	 * Builds the uri to contact the CKAN Connector.
	 *
	 * @return the string
	 */
	public String buildURI(){

		String path =  "";
		String query =  "";
		path = checkURLPathSeparator(pathInfoParameter, true, false);
		query = checkNullString(queryStringParameters);
		return getBaseUrlWithContext()+path+"?"+query;
	}

	/**
	 * Gets the base url with context.
	 *
	 * @return the base url with context
	 */
	public String getBaseUrlWithContext() {

		return baseUrl+serverviceContext;
	}


	/**
	 * Gets the path info parameter.
	 *
	 * @return the pathInfoParameter
	 */
	public String getPathInfoParameter() {

		return pathInfoParameter;
	}


	/**
	 * Gets the query string parameters.
	 *
	 * @return the queryStringParameters
	 */
	public String getQueryStringParameters() {

		return queryStringParameters;
	}


	/**
	 * Gets the gcube token value.
	 *
	 * @return the gcubeTokenValue
	 */
	public String getGcubeTokenValue() {

		return gcubeTokenValue;
	}


	/**
	 * Gets the list of v res.
	 *
	 * @return the listOfVRes
	 */
	public Map<String, String> getListOfVRes() {

		return mapVresRoles;
	}

	/**
	 * Check url path separator.
	 *
	 * @param url the url
	 * @param head - checks the {@link GcubeCkanDataCatalogServiceImpl.URL_PATH_SEPARATOR} in head adding if do not exist
	 * @param tail - checks the {@link GcubeCkanDataCatalogServiceImpl.URL_PATH_SEPARATOR} in tail adding if do not exist
	 * @return the string - if null return an empty string otherwise a string with {@link GcubeCkanDataCatalogServiceImpl.URL_PATH_SEPARATOR}
	 */
	public static String checkURLPathSeparator(String url, boolean head, boolean tail){

		if(url!=null && url.length()>0){
			if(head)
				url=url.startsWith(URL_PATH_SEPARATOR)?url:URL_PATH_SEPARATOR+url;
			if(tail)
				url=url.endsWith(URL_PATH_SEPARATOR)?url:url+URL_PATH_SEPARATOR;
		}else
			url = "";

		return url;
	}


	/**
	 * Gets the base url.
	 *
	 * @return the baseUrl
	 */
	public String getBaseUrl() {

		return baseUrl;
	}


	/**
	 * Check null string.
	 *
	 * @param value the value
	 * @return the string if is not null otherwise an empty string
	 */
	public static String checkNullString(String value){

		if(value==null)
			return "";
		else
			return value;
	}




	/**
	 * @return the outsideFromPortal
	 */
	public boolean isOutsideFromPortal() {

		return outsideFromPortal;
	}



	/**
	 * @param outsideFromPortal the outsideFromPortal to set
	 */
	public void setOutsideFromPortal(boolean outsideFromPortal) {

		this.outsideFromPortal = outsideFromPortal;
	}

	/**
	 * 
	 * @return the catalogue url
	 */
	public String getCatalogueBaseUrl() {
		return catalogueBaseUrl;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CkanConnectorAccessPoint [baseUrl=");
		builder.append(baseUrl);
		builder.append(", pathInfoParameter=");
		builder.append(pathInfoParameter);
		builder.append(", queryStringParameters=");
		builder.append(queryStringParameters);
		builder.append(", gcubeTokenValue=");
		builder.append(gcubeTokenValue);
		builder.append(", mapVresRoles=");
		builder.append(mapVresRoles);
		builder.append(", serverviceContext=");
		builder.append(serverviceContext);
		builder.append(", outsideFromPortal=");
		builder.append(outsideFromPortal);
		builder.append(", catalogueBaseUrl=");
		builder.append(catalogueBaseUrl);
		builder.append("]");
		return builder.toString();
	}

}
