package org.gcube.spatial.data.geoutility.wms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gcube.spatial.data.geoutility.bean.WmsParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class WmsUrlValidator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 25, 2016
 */
public class WmsUrlValidator {

	private HashMap<String, String> mapWmsParameters = new HashMap<String, String>();
	private String wmsRequest;
	private String baseWmsServiceUrl;
	private String wmsParameters;
	private String wmsNoStandardParameters = "";
	private Map<String, String> mapWmsNoStandardParams;
	public static Logger logger = LoggerFactory.getLogger(WmsUrlValidator.class);
	/**
	 * Instantiates a new wms url validator.
	 *
	 * @param wmsRequest the wms request
	 */
	public WmsUrlValidator(String wmsRequest){
		this.wmsRequest = wmsRequest;
		int indexStart = wmsRequest.indexOf("?");
		if(indexStart==-1){
			this.baseWmsServiceUrl = wmsRequest;
			this.wmsParameters = null;
		}else{
			this.baseWmsServiceUrl=wmsRequest.substring(0, indexStart);
			this.baseWmsServiceUrl.trim();
			this.wmsParameters = wmsRequest.substring(indexStart+1, this.wmsRequest.length());
			this.wmsParameters.trim();
		}
	}

	/**
	 * Parses the wms request.
	 *
	 * @param returnEmptyParameter the return empty parameter
	 * @param fillEmptyParameterAsDefaultValue the fill empty parameter as default
	 * @return the wms request uri builded
	 * @throws Exception the exception
	 */
	public String parseWmsRequest(boolean returnEmptyParameter, boolean fillEmptyParameterAsDefaultValue) throws Exception{

		if(wmsParameters==null || wmsParameters.isEmpty()){
			String msg = "IT IS NOT POSSIBLE TO PARSE WMS URL, 'WMS PARAMETERS' not found!";
//			logger.trace(msg);
			throw new Exception(msg);
		}

		for (WmsParameters wmsParam : WmsParameters.values()) {

			if(wmsParam.equals(WmsParameters.BBOX)){
				String value = validateValueOfParameter(WmsParameters.BBOX, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), value);
			}

			if(wmsParam.equals(WmsParameters.FORMAT)){
				String value = validateValueOfParameter(WmsParameters.FORMAT, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), value);
			}

			if(wmsParam.equals(WmsParameters.HEIGHT)){
				String value =  validateValueOfParameter( WmsParameters.HEIGHT, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), value);
			}

			if(wmsParam.equals(WmsParameters.CRS)){
				String crs = validateValueOfParameter(WmsParameters.CRS, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), crs);
			}

			if(wmsParam.equals(WmsParameters.WIDTH)){
				String value = validateValueOfParameter(WmsParameters.WIDTH, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), value);
			}

			if(wmsParam.equals(WmsParameters.REQUEST)){
				String value = validateValueOfParameter(WmsParameters.REQUEST, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), value);
			}

			if(wmsParam.equals(WmsParameters.SERVICE)){
				String value = validateValueOfParameter(WmsParameters.SERVICE, wmsParameters,fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), value);
			}

			if(wmsParam.equals(WmsParameters.SRS)){
				String value = validateValueOfParameter(WmsParameters.SRS, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), value);
			}

			if(wmsParam.equals(WmsParameters.STYLES)){
				String styles = validateValueOfParameter(WmsParameters.STYLES, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), styles);
			}

			if(wmsParam.equals(WmsParameters.VERSION)){
				String version = validateValueOfParameter(WmsParameters.VERSION, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), version);
			}

			if(wmsParam.equals(WmsParameters.LAYERS)){
				String layers = validateValueOfParameter(WmsParameters.LAYERS, wmsParameters, fillEmptyParameterAsDefaultValue);
				mapWmsParameters.put(wmsParam.getParameter(), layers);
			}
		}

		String parsedWmsRequest = baseWmsServiceUrl+"?";

		String[] params = wmsParameters.split("&");

		//CREATING MAP TO RETURN WMS PARAMETERS NOT STANDARD
		mapWmsNoStandardParams = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

		for (String param : params) {
			int ei = param.indexOf("=");
			String key = param.substring(0, ei);
			String value = param.substring(ei+1, param.length());
			mapWmsNoStandardParams.put(key, value);
		}

		//CREATE WMS REQUEST
		for (String key : mapWmsParameters.keySet()) {

			String value = mapWmsParameters.get(key);
			if(returnEmptyParameter || !(value==null) && !value.isEmpty()){
				parsedWmsRequest+=key+"="+value;
				parsedWmsRequest+="&";
			}
			/*if(!returnEmptyParameter && (value==null || value.isEmpty())){
			}else{
				fullWmsRequest+=key+"="+value;
				fullWmsRequest+="&";
			}*/

			String exist = mapWmsParameters.get(key);
			if(exist!=null)
				mapWmsNoStandardParams.remove(key); //REMOVE WMS STANDARD PARAMETER FROM MAP

		}

		for (String key : mapWmsNoStandardParams.keySet()) {
			wmsNoStandardParameters+=key+"="+mapWmsNoStandardParams.get(key) + "&";
		}

		if(wmsNoStandardParameters.length()>0)
			wmsNoStandardParameters = wmsNoStandardParameters.substring(0, wmsNoStandardParameters.length()-1); //REMOVE LAST &

		logger.trace("wmsNotStandardParameters: "+wmsNoStandardParameters);

		String fullWmsUrlBuilded;

		if(!wmsNoStandardParameters.isEmpty()){
			fullWmsUrlBuilded = parsedWmsRequest +  wmsNoStandardParameters; //remove last &
			logger.trace("full wms url builded + wms no standard parameters: "+fullWmsUrlBuilded);
		}else{
			fullWmsUrlBuilded = parsedWmsRequest.substring(0, parsedWmsRequest.length()-1); //remove last &
			logger.trace("WmsUrlValidator parseWmsRequest returning, full wms url builded: "+fullWmsUrlBuilded);
		}

		return fullWmsUrlBuilded;
	}


	/**
	 * Gets the map wms parameters.
	 *
	 * @return the map wms parameters
	 */
	public HashMap<String, String> getMapWmsParameters() {

		return mapWmsParameters;
	}

	/**
	 * Gets the wms request.
	 *
	 * @return the wmsRequest
	 */
	public String getWmsRequest() {
		return wmsRequest;
	}

	/**
	 * Gets the base wms service url.
	 *
	 * @return the baseWmsServiceUrl
	 */
	public String getBaseWmsServiceUrl() {
		return baseWmsServiceUrl;
	}

	/**
	 * Gets the wms no standard parameters.
	 *
	 * @return the wms no standard parameters like a  query string (param1=value1&param2=value2&...)
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
		return mapWmsParameters.get(parameter.getParameter());
	}

	/**
	 * Validate value of parameter.
	 *
	 * @param wmsParam the wms param
	 * @param queryStringParameters the value of parameter
	 * @param fillEmptyParameterAsDefaultValue the fill empty parameter as default value
	 * @return the string
	 */
	public static String validateValueOfParameter(WmsParameters wmsParam, String queryStringParameters, boolean fillEmptyParameterAsDefaultValue){

		try{

			logger.trace("validateValueOfParameter wmsParam "+wmsParam+" with queryStringParameters " +queryStringParameters);
			String value = getValueOfParameter(wmsParam, queryStringParameters);
			logger.trace("validateValueOfParameter wmsParam "+wmsParam+" value " +value);

			if(fillEmptyParameterAsDefaultValue && (value==null || value.isEmpty())){
				logger.trace("setting empty value for parameter: "+wmsParam.getParameter() +", as default value: "+wmsParam.getValue());
				value = wmsParam.getValue();
			}
			return value;
		}catch(Exception e){
			//silent
			return null;
		}
	}

	/**
	 * Gets the value of parameter.
	 *
	 * @param wmsParam the wms param
	 * @param wmsRequestParamaters the url wms parameters
	 * @return the value of parameter or null if parameter not exists
	 */
	public static String getValueOfParameter(WmsParameters wmsParam, String wmsRequestParamaters) {
//		logger.trace("finding: "+wmsParam +" into "+url);
		int index = wmsRequestParamaters.toLowerCase().indexOf(wmsParam.getParameter().toLowerCase()+"="); //ADDING CHAR "=" IN TAIL TO BE SECURE  IT IS A PARAMETER
//		logger.trace("start index of "+wmsParam+ " is: "+index);
		String value = "";
		if(index > -1){

			int start = index + wmsParam.getParameter().length()+1; //add +1 for char '='
			String sub = wmsRequestParamaters.substring(start, wmsRequestParamaters.length());
			int indexOfSeparator = sub.indexOf("&");
			int end = indexOfSeparator!=-1?indexOfSeparator:sub.length();
			value = sub.substring(0, end);
		}else
			return null;

//		logger.trace("return value: "+value);
		return value;
	}


	/**
	 * Sets the value of parameter.
	 *
	 * @param wmsParam the wms param
	 * @param wmsRequestParameters the wms url parameters
	 * @param newValue the new value
	 * @param addIfNotExists add the parameter if not exists
	 * @return the string
	 */
	public static String setValueOfParameter(WmsParameters wmsParam, String wmsRequestParameters, String newValue, boolean addIfNotExists){
		String toLowerWmsUrlParameters = wmsRequestParameters.toLowerCase();
		String toLowerWmsParam = wmsParam.getParameter().toLowerCase();

		int index = toLowerWmsUrlParameters.indexOf(toLowerWmsParam+"="); //END WITH CHAR "=" TO BE SECURE  IT IS A PARAMETER
//		logger.trace("start index of "+wmsParam+ " is: "+index);
		if(index > -1){
			int indexStartValue = index + toLowerWmsParam.length()+1; //add +1 for char '='
			int indexOfSeparator = toLowerWmsUrlParameters.indexOf("&", indexStartValue); //GET THE FIRST "&" STARTING FROM INDEX VALUE
//			logger.trace("indexOfSeparator index of "+wmsParam+ " is: "+indexOfSeparator);
			int indexEndValue = indexOfSeparator!=-1?indexOfSeparator:toLowerWmsUrlParameters.length();
//			logger.trace("end: "+indexEndValue);
			return wmsRequestParameters.substring(0, indexStartValue) + newValue +wmsRequestParameters.substring(indexEndValue, wmsRequestParameters.length());
		}else if (addIfNotExists){
			wmsRequestParameters+="&"+wmsParam.getParameter()+"="+newValue;
		}
//		logger.trace("return value: "+value);
		return wmsRequestParameters;
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
	 * @return an empty map if WMS no standard parameters don't exist
	 */
	public Map<String, String> getMapWmsNoStandardParams() {
		return mapWmsNoStandardParams==null?new HashMap<String, String>(1):mapWmsNoStandardParams;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("WmsUrlValidator [mapWmsParameters=");
		builder.append(mapWmsParameters);
		builder.append(", wmsRequest=");
		builder.append(wmsRequest);
		builder.append(", baseWmsServiceUrl=");
		builder.append(baseWmsServiceUrl);
		builder.append(", wmsParameters=");
		builder.append(wmsParameters);
		builder.append(", wmsNotStandardParameters=");
		builder.append(wmsNoStandardParameters);
		builder.append(", mapWmsNoStandardParams=");
		builder.append(mapWmsNoStandardParams);
		builder.append("]");
		return builder.toString();
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
//		String fullPath = "http://www.fao.org/figis/geoserver/species/ows?CRS=EPSG:4326&BBOX=-180,-90,180,90&VERSION=1.1.0&FORMAT=image/png&SERVICE=WMS&HEIGHT=230&LAYERS=&REQUEST=GetMap&STYLES=&SRS=EPSG:4326&WIDTH=676";
//		fullPath = WmsUrlValidator.setValueOfParameter(WmsParameters.LAYERS, fullPath, "123", true);
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
//
	}
}
