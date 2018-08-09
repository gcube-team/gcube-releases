/**
 *
 */
package org.gcube.spatial.data.geoutility.wms;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.gcube.spatial.data.geoutility.bean.WmsServiceBaseUri;
import org.gcube.spatial.data.geoutility.util.HttpRequestUtil;
import org.gcube.spatial.data.geoutility.util.NamespaceContextMap;
import org.gcube.spatial.data.geoutility.util.UrlEncoderUtil;
import org.gcube.spatial.data.geoutility.util.XpathParserUtil;



/**
 * The Class WmsGetStyles.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2016
 */
public class WmsGetStyles {

	private int CONNECTION_TIMEOUT = 1000; //DEFAULT CONNECTION TIMEOUT
	public static Logger logger = Logger.getLogger(WmsGetStyles.class);
	protected HashMap<String, String> mappings;
	protected NamespaceContextMap context;
	private int responseCode;
	private static final String GEOSERVER = "/geoserver";

	/**
	 * Instantiates a new wms get styles.
	 */
	public WmsGetStyles() {

		mappings = new HashMap<String, String>();
//		mappings.put("xmlns", "http://www.opengis.net/sld");
		mappings.put("sld", "http://www.opengis.net/sld");
		mappings.put("ogc", "http://www.opengis.net/ogc");
		mappings.put("gml", "http://www.opengis.net/gml");
		mappings.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
		context = new NamespaceContextMap(mappings);
	}

	/**
	 * Instantiates a new wms get styles.
	 *
	 * @param connectionTimeout the connection timeout sets a specified timeout value, in milliseconds, to be used when opening WMS GetStyles request.
	 */
	public WmsGetStyles(int connectionTimeout) {
		this();
		if(connectionTimeout>0)
			CONNECTION_TIMEOUT = connectionTimeout;
	}

	/**
	 * Gets the styles from wms.
	 *
	 * @param urlConn the url conn
	 * @param layerName the layer name
	 * @return the styles from wms
	 */
	public List<String> getStylesFromWms(String urlConn, String layerName) {

		List<String> styles = new ArrayList<String>();
		String query = "";
		try {

			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("request", "GetStyles");
			parameters.put("layers", layerName);
			parameters.put("service", "wms");
			parameters.put("version", "1.1.1");

			query = UrlEncoderUtil.encodeQuery(parameters);
			WmsServiceBaseUri geosever = foundWmsServiceBaseUri(urlConn);

			if(query==null || query.isEmpty())
				return new ArrayList<String>();

			logger.info("tentative get styles.. with query: " + query);
			styles = openConnectionGetStyles(urlConn, query);

			if(styles==null || styles.isEmpty()){
				if(geosever.getScope()!=null && !geosever.getScope().isEmpty()){
					logger.trace("trying to get styles with scope: "+geosever.getScope());
					String newUrlConn = urlConn.replace("/"+geosever.getScope(), "");
					logger.trace("new tentative get styles as base url without scope");
					styles = openConnectionGetStyles(newUrlConn, query);
				}
			}
			logger.info("WMS GetStyles returning: "+styles.toString());
			return styles;

		}catch (Exception e) {
			logger.error("Error Exception getStylesFromWms from url: " + urlConn + ", query: "+query, e);
			return styles;
		}

	}

	/**
	 * Gets the styles.
	 *
	 * @param httpConnection the http connection
	 * @return a list of styles. The first element of list is default style if exists
	 * @throws IOException Signals that an I/O exception has occurred.
	 */

	private List<String> getStyles(HttpURLConnection httpConnection) throws IOException{

		List<String> listStylesNames = new ArrayList<String>();
		InputStream source = null;
		int code = httpConnection.getResponseCode();
		logger.trace("response code is " + code);

		if (code == 200) {

			source = httpConnection.getInputStream();
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			xPath.setNamespaceContext(context);

			try{

				String xmlGetStyles = IOUtils.toString(source);
				String xpathExpression = "//sld:UserStyle[sld:IsDefault=1]/sld:Name"; //FIND DEFAULT STYLE NAME
				List<String> defaultStylesList  = XpathParserUtil.getTextFromXPathExpression(context, xmlGetStyles, xpathExpression);
				LinkedHashMap<String, String> exclusiveStyles = new LinkedHashMap<String, String>();

				//DEFAULT STYLE IS FOUND
				if(defaultStylesList.size()>0 || !defaultStylesList.get(0).isEmpty()){
					String defaultStyle = defaultStylesList.get(0);
					exclusiveStyles.put(defaultStyle, defaultStyle);
				}

				xpathExpression = "//sld:UserStyle/sld:Name"; //FIND OTHER STYLES NAMES AND ADD INTO LIST
				List<String> allStyles = XpathParserUtil.getTextFromXPathExpression(context, xmlGetStyles, xpathExpression);

				for (String style : allStyles) {
					exclusiveStyles.put(style, style);
				}

				listStylesNames.addAll(exclusiveStyles.keySet());

			}catch (IOException e) {
				logger.warn("IOException occurred when converting stream get styles");
			}

		} else {

			return listStylesNames;
		}

		return listStylesNames;

	}

	/**
	 * Open connection get styles.
	 *
	 * @param urlConn the url conn
	 * @param query the query
	 * @return the list
	 */
	private List<String> openConnectionGetStyles(String urlConn, String query) {

		URL url;
		List<String> styles = new ArrayList<String>();

		try {
			url = new URL(urlConn + "?" + query);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(CONNECTION_TIMEOUT + CONNECTION_TIMEOUT);
			logger.trace("openConnectionGetStyles: " + url);

			// Cast to a HttpURLConnection
			if (connection instanceof HttpURLConnection) {

				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				responseCode= httpConnection.getResponseCode();

				if (responseCode == 200) {
					styles = getStyles(httpConnection);
				}else{
					logger.warn("openConnectionGetStyles error, code = " + responseCode +", returning");
				}

				httpConnection.disconnect();

			} else {
				logger.error("error - not a http request!");
			}

			return styles;
		} catch (SocketTimeoutException e) {
			logger.error("Error SocketTimeoutException with url " + urlConn);
			return styles;
		} catch (MalformedURLException e) {
			logger.error("Error MalformedURLException with url " + urlConn);
			return styles;
		} catch (IOException e) {
			logger.error("Error IOException with url " + urlConn);
			return styles;
		} catch (Exception e) {
			logger.error("Error Exception with url " + urlConn);
			return styles;
		}

	}

	/**
	 * Gets the geoserver base uri.
	 *
	 * @param uri the uri
	 * @return the input uri without the parameters, (the uri substring from start to index of '?' char (if exists)) if geoserver base url not found,
	 * geoserver url otherwise
	 */
	public WmsServiceBaseUri foundWmsServiceBaseUri(String uri){

		WmsServiceBaseUri geoserverBaseUri = new WmsServiceBaseUri();

		if(uri==null)
			return geoserverBaseUri; //uri is empty

		int end = uri.toLowerCase().lastIndexOf("?");

		if(end==-1){
			logger.trace("char ? not found in geoserver uri, return: "+uri);
			return geoserverBaseUri; //uri is empty
		}

		String wmsServiceUri = uri.substring(0, uri.toLowerCase().lastIndexOf("?"));
		int index = wmsServiceUri.lastIndexOf(GEOSERVER);

		if(index>-1){ //FOUND the string GEOSERVER into URL
			logger.trace("found geoserver string: "+GEOSERVER+" in "+wmsServiceUri);

			//THERE IS SCOPE?
			int lastSlash = wmsServiceUri.lastIndexOf("/");
			int includeGeoserverString = index+GEOSERVER.length();
			int endUrl = lastSlash>includeGeoserverString?lastSlash:includeGeoserverString;
			logger.trace("indexs - lastSlash: ["+lastSlash+"],  includeGeoserverString: ["+includeGeoserverString+"], endUrl: ["+endUrl+"]");
			int startScope = includeGeoserverString+1<endUrl?includeGeoserverString+1:endUrl; //INCLUDE SLASH
			String scope = wmsServiceUri.substring(startScope, endUrl);
			logger.trace("geoserver url include scope: "+wmsServiceUri.substring(includeGeoserverString, endUrl));
			geoserverBaseUri.setBaseUrl(wmsServiceUri.substring(0, endUrl));
			geoserverBaseUri.setScope(scope);
			return geoserverBaseUri;
		}
		else{

			logger.trace("the string "+GEOSERVER+" not found in "+wmsServiceUri);
			// GET LAST INDEX OF '/' AND CONCATENATE GEOSERVER
			String urlConn = wmsServiceUri.substring(0, wmsServiceUri.lastIndexOf("/"))+GEOSERVER;
			logger.trace("tentative concatenating string "+GEOSERVER+" at http url "+urlConn);

			try {

				if(HttpRequestUtil.urlExists(urlConn, false)){
					logger.trace("url: "+urlConn+" - open a connection, return "+urlConn);
					geoserverBaseUri.setBaseUrl(urlConn);
					return geoserverBaseUri;
				}
				else
					logger.trace("url: "+urlConn+" - not open a connection");

			} catch (Exception e) {
				logger.error("url connection is wrong at :"+urlConn);
			}

			String uriWithoutParameters = uri.substring(0, end);
			logger.trace("url connection, returned: "+uriWithoutParameters);
			geoserverBaseUri.setBaseUrl(uriWithoutParameters);
			return geoserverBaseUri;
		}
	}

	/**
	 * Gets the response code.
	 *
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WmsGetStyles [mappings=");
		builder.append(mappings);
		builder.append(", context=");
		builder.append(context);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		WmsGetStyles wmsStyles = new WmsGetStyles();
		try {
			List<String> list = wmsStyles.getStylesFromWms("http://www.fao.org/figis/geoserver/area/ows", "FAO_AREAS");
			for (String string : list) {
				System.out.println(string);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
