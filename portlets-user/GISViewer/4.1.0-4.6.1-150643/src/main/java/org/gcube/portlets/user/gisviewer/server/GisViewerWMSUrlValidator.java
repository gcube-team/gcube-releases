package org.gcube.portlets.user.gisviewer.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.spatial.data.geoutility.GeoWmsServiceUtility;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;
import org.gcube.spatial.data.geoutility.util.HttpRequestUtil;
import org.gcube.spatial.data.geoutility.wms.WmsUrlValidator;


/**
 * The Class GisViewerWMSUrlValidator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 28, 2016
 */
public class GisViewerWMSUrlValidator {

	public static final String GEOSERVER = "/geoserver";
	private static final String WMS = "wms";
	private static final String OWS = "ows";
	private HashMap<String, String> parametersValue = new HashMap<String, String>();
	private String wmsRequestURI;
	private String wmsServiceHost;
	private String layerName;

	private String wmsNoStandardParameters = "";
	private Map<String, String> mapWmsNoStandardParams;
	private WmsUrlValidator urlValidator;

	public static Logger logger = Logger.getLogger(GisViewerWMSUrlValidator.class);


	/**
	 * Instantiates a new gis viewer wms url validator.
	 *
	 * @param wmsRequest the full url
	 * @param layerName the layer name
	 * @throws Exception the exception
	 */
	public GisViewerWMSUrlValidator(String wmsRequest, String layerName) throws Exception{

		if(wmsRequest==null || wmsRequest.isEmpty())
			throw new Exception("WMS request is null or empty");

		this.wmsRequestURI = wmsRequest.trim();
		boolean isOwsService = GeoWmsServiceUtility.isOWSSerice(this.wmsRequestURI);
		WebMapServerHost webMapServerHost;

		//IS WMS?
		if(GeoWmsServiceUtility.isWMSService(wmsRequestURI)){
			logger.trace("found "+GeoWmsServiceUtility.SERVICE_WMS+" in wms request: "+wmsRequestURI);
			webMapServerHost = getWebMapServerHost(wmsRequestURI);
		}else
			throw new Exception("WMS service not found for layer: "+layerName);

		//VALIDATION WMS
		String baseWmsService = webMapServerHost.getHost();
		//IS OWS OR WMS?
		this.wmsServiceHost = appendWmsServiceToBaseUrl(wmsRequest.substring(0, wmsRequest.indexOf("?")),isOwsService);
		this.layerName = layerName;

		try {
			//VALIDATE WMS SERVICE FOR WEB MAP SERVER
			if(!HttpRequestUtil.urlExists(this.wmsServiceHost, true)){
				logger.info("baseWmsServiceUrl: "+wmsServiceHost +" is not a geoserver, setting as input base wms server: "+baseWmsService);
				this.wmsServiceHost = baseWmsService;
			}

		} catch (Exception e) {
			logger.error("error on validating geoserver wms service: "+e);
			logger.info("setting baseWmsService as input base wms server: "+baseWmsService);
			this.wmsServiceHost = baseWmsService;
		}

		//VALIDATION FOR THREDDS - FIND LAYER NAME INTO WMS PATH
		if(this.layerName==null || this.layerName.isEmpty()){

			this.layerName = WmsUrlValidator.getValueOfParameter(WmsParameters.LAYERS, wmsRequest);
			if(this.layerName==null || layerName.isEmpty())
				throw new Exception("Layer name is null or empty");

		}

		parametersValue.put(WmsParameters.LAYERS.getParameter(), this.layerName);
	}


	/**
	 * Append wms service to base url.
	 *
	 * @param url the url
	 * @param isOwsServer the is ows server
	 * @return the string
	 */
	public String appendWmsServiceToBaseUrl(String url, boolean isOwsServer){

		if(url.contains("/"+WMS) || url.contains("/"+OWS))
			return url;

		if(url.lastIndexOf("/") != url.length()){
			url+="/";
		}

		if(isOwsServer)
			return url+=OWS;
		else
			return url+=WMS;
	}

	/**
	 * Method: getFullWmsUrlRequest
	 * Create a correct wms url request
	 * Returns:
	 * {String}.
	 *
	 * @param returnEmptyParameter if true the wms url returned contains also wms parameter with empty value, none otherwise.
	 * and mandatory wms parameters that does not found are filled with empty values
	 * @param fillEmptyParameterAsDefault the fill empty parameter as default
	 * @return a correct wms url request in formatted string like this:
	 *        "wmsserver?key1=value1&key2=value2&key3=value3"
	 */
	public String parseWMSRequest(boolean returnEmptyParameter, boolean fillEmptyParameterAsDefault){

		urlValidator = new org.gcube.spatial.data.geoutility.wms.WmsUrlValidator(wmsRequestURI);
		String fullWmsUrlBuilded;

		try {
			fullWmsUrlBuilded = urlValidator.parseWmsRequest(returnEmptyParameter, fillEmptyParameterAsDefault);
			parametersValue.putAll(urlValidator.getMapWmsParameters());

			String ln = parametersValue.get(WmsParameters.LAYERS);
			logger.debug("Comparing layer name from Wms request: "+ln +", with OnLineResource layerName: "+this.layerName);
			if(ln==null || ln.isEmpty() || ln.compareTo(this.layerName)!=0){
				logger.info("Layer name into wms request is different to OnLineResource layers name, replacing layer name: "+this.layerName);
				parametersValue.put(WmsParameters.LAYERS.getParameter(), this.layerName);
				urlValidator.getMapWmsParameters().put(org.gcube.spatial.data.geoutility.bean.WmsParameters.LAYERS.getParameter(), this.layerName);
				fullWmsUrlBuilded = org.gcube.spatial.data.geoutility.wms.WmsUrlValidator.setValueOfParameter(org.gcube.spatial.data.geoutility.bean.WmsParameters.LAYERS, fullWmsUrlBuilded, this.layerName, true);
			}

//			logger.trace("parametersValue: "+parametersValue);
			mapWmsNoStandardParams = new HashMap<String, String>(urlValidator.getMapWmsNoStandardParams().size());
			mapWmsNoStandardParams.putAll(urlValidator.getMapWmsNoStandardParams());
			wmsNoStandardParameters = urlValidator.getWmsNoStandardParameters();
		}
		catch (Exception e) {
			logger.error("An error occurred during wms uri build, returning uri: "+wmsRequestURI, e);
			fullWmsUrlBuilded = wmsRequestURI;
		}

		logger.trace("GisViewerWMSUrlValidator parseWMSRequest returning full wms url: "+fullWmsUrlBuilded);
		return fullWmsUrlBuilded;
	}



	/**
	 * Gets the web map server host.
	 *
	 * @param wmsRequest the wms request
	 * @return the web map server host, (geoserver URI or the wmsRequest substring from start to index of '?' char (if exists))
	 */
	public WebMapServerHost getWebMapServerHost(String wmsRequest){

		WebMapServerHost geoserverBaseUri = new WebMapServerHost();

		if(wmsRequest==null)
			return geoserverBaseUri; //uri is empty


		int end = wmsRequest.toLowerCase().lastIndexOf("?");

		if(end==-1){
			logger.trace("char ? not found in geoserver uri, return: "+wmsRequest);
			return geoserverBaseUri; //uri is empty
		}

		String geoserverUrl = wmsRequest.substring(0, wmsRequest.toLowerCase().lastIndexOf("?"));
		int index = geoserverUrl.lastIndexOf(GEOSERVER);

		if(index>-1){ //FOUND the string GEOSERVER into URL
			logger.trace("found geoserver string: "+GEOSERVER+" in "+geoserverUrl);

			//THERE IS SCOPE?
			int lastSlash = geoserverUrl.lastIndexOf("/");
			int includeGeoserverString = index+GEOSERVER.length();
			int endUrl = lastSlash>includeGeoserverString?lastSlash:includeGeoserverString;
			logger.trace("indexs - lastSlash: ["+lastSlash+"],  includeGeoserverString: ["+includeGeoserverString+"], endUrl: ["+endUrl+"]");
			int startScope = includeGeoserverString+1<endUrl?includeGeoserverString+1:endUrl; //INCLUDE SLASH
			String scope = geoserverUrl.substring(startScope, endUrl);
			logger.trace("geoserver url include scope: "+geoserverUrl.substring(includeGeoserverString, endUrl));
			geoserverBaseUri.setHost(geoserverUrl.substring(0, endUrl));
			geoserverBaseUri.setScope(scope);

			return geoserverBaseUri;

		}else{
			logger.trace("the string 'geoserver' not found in "+geoserverUrl);
			// GET LAST INDEX OF '/' AND CONCATENATE GEOSERVER
			String urlConn = geoserverUrl.substring(0, geoserverUrl.lastIndexOf("/"))+GEOSERVER;
			logger.trace("tentative concatenating string 'geoserver' at http url "+urlConn);

			try {

				if(HttpRequestUtil.urlExists(urlConn, false)){
					logger.trace("url: "+urlConn+" - open a connection, return "+urlConn);
					geoserverBaseUri.setHost(urlConn);
					return geoserverBaseUri;
				}
				else
					logger.trace("url: "+urlConn+" - not open a connection");

			} catch (Exception e) {
				logger.error("url connection is wrong at :"+urlConn);
			}

			String uriWithoutParameters = wmsRequest.substring(0, end);
			logger.trace("url connection, returned: "+uriWithoutParameters);
			geoserverBaseUri.setHost(uriWithoutParameters);
			return geoserverBaseUri;
		}
	}


	/**
	 * Gets the wms service host.
	 *
	 * @return the wms service host
	 */
	public String getWmsServiceHost() {

		return wmsServiceHost;
	}

	/**
	 * Gets the url validator.
	 *
	 * @return the urlValidator
	 */
	public org.gcube.spatial.data.geoutility.wms.WmsUrlValidator getUrlValidator() {

		return urlValidator;
	}

	/**
	 * Gets the wms no standard parameters.
	 *
	 * @return the wms no standard parameters
	 */
	public String getWmsNoStandardParameters() {

		return wmsNoStandardParameters;
	}


	/**
	 * Gets the value of parsed wms parameter.
	 *
	 * @param parameter the parameter
	 * @return the value of parsed wms parameter parsed from wms request.
	 */
	public String getValueOfParsedWMSParameter(WmsParameters parameter){

		return parametersValue.get(parameter.getParameter());
	}


	/**
	 * Gets the value of parameter.
	 *
	 * @param wmsParam the wms param
	 * @param wmsUrlParameters the wms url parameters
	 * @return the value of parameter
	 */
	public static String getValueOfParameter(WmsParameters wmsParam, String wmsUrlParameters){

		return WmsUrlValidator.getValueOfParameter(wmsParam, wmsUrlParameters);
	}


	/**
	 * Sets the value of parameter.
	 *
	 * @param wmsParam the wms param
	 * @param wmsUrlParameters the wms url parameters
	 * @param newValue the new value
	 * @param addIfNotExists the add if not exists
	 * @return the string
	 */
	public static String setValueOfParameter(WmsParameters wmsParam, String wmsUrlParameters, String newValue, boolean addIfNotExists){

		return WmsUrlValidator.setValueOfParameter(wmsParam, wmsUrlParameters, newValue, addIfNotExists);
	}

	/**
	 * Gets the layer name.
	 *
	 * @return the layer name
	 */
	public String getLayerName() {
		return layerName;
	}



	/**
	 * Gets the styles as list.
	 *
	 * @return the styles as list
	 */
	public List<String> getStylesAsList() {

		List<String> listStyles = new ArrayList<String>();
		String styles = getValueOfParsedWMSParameter(WmsParameters.STYLES);

		if(styles!=null && !styles.isEmpty()){

			String[] arrayStyle = styles.split(",");
			for (String style : arrayStyle) {
				if(style!=null && !style.isEmpty())
					listStyles.add(style);
			}
		}
		return listStyles;
	}


	/**
	 * Gets the map wms no standard params.
	 *
	 * @return the map wms no standard params
	 */
	public Map<String, String> getMapWmsNoStandardParams() {
		return mapWmsNoStandardParams;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

//		String baseGeoserverUrl = "http://repoigg.services.iit.cnr.it:8080/geoserver/IGG/ows";
//		String baseGeoserverUrl = "http://www.fao.org/figis/geoserver/species";
//		String fullPath = "http://www.fao.org/figis/geoserver/species?SERVICE=WMS&BBOX=-176.0,-90.0,180.0,90&styles=Species_prob, puppa&layers=layerName&FORMAT=image/gif";
//		String fullPath = "http://repoigg.services.iit.cnr.it:8080/geoserver/IGG/ows?service=wms&version=1.1.0&request=GetMap&layers==IGG:area_temp_1000&width=676&height=330&srs=EPSG:4326&crs=EPSG:4326&format=application/openlayers&bbox=-85.5,-180.0,90.0,180.0";
//		String baseGeoserverUrl = "http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc";
//		String fullPath = "http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc?service=wms&version=1.3.0&request=GetMap&layers=analyzed_field&bbox=-85.0,-180.0,85.0,180.0&styles=&width=640&height=480&srs=EPSG:4326&CRS=EPSG:4326&format=image/png&COLORSCALERANGE=auto";
//		WmsUrlValidator validator = new WmsUrlValidator(baseGeoserverUrl, fullPath , "", false);
//		logger.trace("base wms service url: "+validator.getBaseWmsServiceUrl());
//		logger.trace("layer name: "+validator.getLayerName());
//		logger.trace("full wms url: "+validator.getFullWmsUrlRequest(false, true));
//		logger.trace("style: "+validator.getStyles());
//		logger.trace("not standard parameter: "+validator.getWmsNotStandardParameters());
//		String[] arrayStyle = validator.getStyles().split(",");
//
//		if(arrayStyle!=null && arrayStyle.length>0){
//
//			for (String style : arrayStyle) {
//				if(style!=null && !style.isEmpty())
//
//					System.out.println("Style: "+style.trim());
//			}
//		}
//
//		String fullPath = "http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc?service=wms&version=1.3.0&request=GetMap&layers=analyzed_field&bbox=-85.0,-180.0,85.0,180.0&styles=&width=640&height=480&srs=EPSG:4326&CRS=EPSG:4326&format=image/png&COLORSCALERANGE=auto";
//
//		WmsGeoExplorerUrlValidator validator = new WmsGeoExplorerUrlValidator("http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc", fullPath , "", false);
//		validator.getFullWmsUrlRequest(false,true);
//
//		System.out.println(validator.getWmsNoStandardParameters());
//		System.out.println(validator.getMapWmsNoStandardParams());

//		fullPath = WmsUrlValidator.setValueOfParameter(WmsParameters.STYLES, fullPath, "123", true);
//

//		MapPreviewGenerator map = new MapPreviewGenerator();
//		fullPath = map.buildWmsRequestMapPreview(fullPath, "-85.0,-180.0,85.0,180.0");
//		System.out.println(fullPath);

		String wmsRequest = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/wms?CRS=EPSG:4326&BBOX=-85.5,-180.0,90.0,180.0&VERSION=1.1.0&FORMAT=application/openlayers&SERVICE=wms&HEIGHT=330&LAYERS=aquamaps:lsoleasolea20130716162322254cest&REQUEST=GetMap&STYLES=Species_prob&SRS=EPSG:4326&WIDTH=676";
//		String wmsRequest = "http://thredds-d-d4s.d4science.org/thredds/wms/public/netcdf/test20.nc?service=wms&version=1.3.0&request=GetMap&layers=analyzed_field&styles=&width=640&height=480&srs=EPSG:4326&CRS=EPSG:4326&format=image/png&COLORSCALERANGE=auto&bbox=-85.0,-180.0,85.0,180.0";
		WmsUrlValidator wms;
		try {
			wms = new WmsUrlValidator(wmsRequest);
			System.out.println("Returned wms: "+wms.toString());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
