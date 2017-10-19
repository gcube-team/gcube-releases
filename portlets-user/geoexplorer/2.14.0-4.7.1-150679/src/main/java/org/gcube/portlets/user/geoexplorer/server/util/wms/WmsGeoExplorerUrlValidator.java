package org.gcube.portlets.user.geoexplorer.server.util.wms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.util.HttpRequestUtil;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;
import org.gcube.spatial.data.geoutility.wms.WmsUrlValidator;


/**
 * The Class WmsGeoExplorerUrlValidator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 26, 2016
 */
public class WmsGeoExplorerUrlValidator {

	private static final String WMS = "wms";
	private static final String OWS = "ows";
	private static final String LAYERS = WmsParameters.LAYERS.getParameter();
	private HashMap<String, String> parametersValue = new HashMap<String, String>();
	private String wmsFullUrl;
	private String baseWmsServiceUrl;
	private String layerName;

	private String wmsNoStandardParameters = "";
	private Map<String, String> mapWmsNoStandardParams;
	private WmsUrlValidator urlValidator;

	public static Logger logger = Logger.getLogger(WmsGeoExplorerUrlValidator.class);

	/**
	 * Instantiates a new wms url validator.
	 *
	 * @param baseWmsService the base wms service
	 * @param fullUrl the full url
	 * @param layerName the layer name
	 * @param isOwsServer the is ows server
	 */
	public WmsGeoExplorerUrlValidator(String baseWmsService, String fullUrl, String layerName, boolean isOwsServer){

		this.wmsFullUrl = fullUrl.trim();
		//VALIDATION WMS URL TO GEOSERVER
		this.baseWmsServiceUrl = appendWmsSerivceToBaseUrl(baseWmsService,isOwsServer);

		try {
			//VALIDATE GEOSERVER WMS SERVICE
			if(!HttpRequestUtil.urlExists(this.baseWmsServiceUrl, true)){
				logger.info("baseWmsServiceUrl: "+baseWmsServiceUrl +" is not a geoserver, setting as input base wms server: "+baseWmsService);
//				this.baseWmsServiceUrl = "";
				this.baseWmsServiceUrl = baseWmsService;
			}

		} catch (Exception e) {
			logger.error("error on validating geoserver wms service: "+e);
			logger.info("setting baseWmsServiceUrl as input base wms server: "+baseWmsService);
			this.baseWmsServiceUrl = baseWmsService;
		}

		//VALIDATION FOR THREDDS - FIND LAYER NAME INTO WMS PATH
		if(layerName==null || layerName.isEmpty()){

			int indexOfLayers = wmsFullUrl.toLowerCase().indexOf(LAYERS.toLowerCase());

			if(indexOfLayers>=0){
				String tempLayerName = "";
				tempLayerName = wmsFullUrl.substring(indexOfLayers, wmsFullUrl.length());

				try{
					//FIND THE VALUE
					if(tempLayerName.contains("=")){
						if(tempLayerName.contains("&")) //LAYERS IS AN INTERNAL PARAMETER
							tempLayerName = tempLayerName.substring(tempLayerName.indexOf("=")+1, tempLayerName.indexOf("&"));
						else//LAYERS IS LAST PARAMETER
							tempLayerName = tempLayerName.substring(tempLayerName.indexOf("=")+1, tempLayerName.length());
					}

					this.layerName = tempLayerName;

				}catch (Exception e) {
					logger.error("error on searching layer name  into wms url, the layer name is: "+tempLayerName,e);
					this.layerName = layerName;
				}
			}

		}else{

			//FIX RELEASED 19/09/2013 by FRANCESCO M.
			//IF WMS PARAMETER LAYER NAME IS NOT EQUAL AT LAYER NAME INTO METADATA, WMS PARAMETER IS ASSIGNED AS LAYER NAME
			String value = getValueOfParameter(WmsParameters.LAYERS, this.wmsFullUrl);
			if(value!=null && !value.isEmpty() && layerName.compareToIgnoreCase(value)!=0){
				logger.info("Layer Name into wms request IS NOT EQUAL to layer name into OnlineResource Metadata,  assigning layer name like wms parameter: "+value);
				this.layerName = value;
			}else
				this.layerName = layerName;
		}

		parametersValue.put(WmsParameters.LAYERS.getParameter(), this.layerName);
	}

	/**
	 * Append wms serivce to base url.
	 *
	 * @param url the url
	 * @param isOwsServer the is ows server
	 * @return the string
	 */
	public String appendWmsSerivceToBaseUrl(String url, boolean isOwsServer){

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
	public String getFullWmsUrlRequest(boolean returnEmptyParameter, boolean fillEmptyParameterAsDefault){

		urlValidator = new org.gcube.spatial.data.geoutility.wms.WmsUrlValidator(wmsFullUrl);
		String fullWmsUrlBuilded;

		try {
			fullWmsUrlBuilded = urlValidator.parseWmsRequest(returnEmptyParameter, fillEmptyParameterAsDefault);
			parametersValue.putAll(urlValidator.getMapWmsParameters());

			String ln = parametersValue.get(WmsParameters.LAYERS);
			logger.trace("Compare layers name from Wms request: "+ln +", with OnLineResource layerName: "+this.layerName);
			if(ln==null || ln.isEmpty() || ln.compareTo(this.layerName)!=0){
				logger.info("Layers name into wms request is different to saved layers name, adding layers name: "+this.layerName);
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
			logger.error("An error occurred during wms uri build, returning uri: "+wmsFullUrl, e);
			fullWmsUrlBuilded = wmsFullUrl;
		}

		logger.trace("returning full wms url: "+fullWmsUrlBuilded);
		return fullWmsUrlBuilded;
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
	 * Gets the base wms service url.
	 *
	 * @return the base wms service url
	 */
	public String getBaseWmsServiceUrl() {
		return baseWmsServiceUrl;
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

	}

}
