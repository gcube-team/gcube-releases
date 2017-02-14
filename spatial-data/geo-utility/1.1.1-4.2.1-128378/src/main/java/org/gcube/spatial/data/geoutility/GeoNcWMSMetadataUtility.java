/**
 *
 */
package org.gcube.spatial.data.geoutility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.spatial.data.geoutility.bean.LayerStyles;
import org.gcube.spatial.data.geoutility.bean.LayerZAxis;
import org.gcube.spatial.data.geoutility.bean.NcWmsLayerMetadata;
import org.gcube.spatial.data.geoutility.bean.NcWmsLayerMetadata.METADATA;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;
import org.gcube.spatial.data.geoutility.wms.NcWmsGetMetadata;
import org.gcube.spatial.data.geoutility.wms.NcWmsGetMetadataRequest;
import org.gcube.spatial.data.geoutility.wms.WmsGetStyles;
import org.gcube.spatial.data.geoutility.wms.WmsUrlValidator;


/**
 * The Class GeoNcWMSMetadataUtility.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2016
 */
public class GeoNcWMSMetadataUtility {

	public static Logger logger = Logger.getLogger(GeoNcWMSMetadataUtility.class);
	public static final String COLORSCALERANGE = "COLORSCALERANGE";
	public static final String COLORSCALERANGE_DEFAULT_VALUE = "auto";
	private String wmsRequest;
	private WmsUrlValidator validator;
	private int connectionTimeout;
	private NcWmsLayerMetadata ncMetadata;
	private LayerZAxis zAxis = null;
	private LayerStyles layerStyles;

	/** The Constant CONNECTION_TIMEOUT. */
	public static final int DEFAULT_CONNECTION_TIMEOUT = 1000; //DEFAULT CONNECTION TIMEOUT


	/**
	 * Instantiates a new geo get styles utility.
	 *
	 * @param wmsRequest the wms request
	 * @throws Exception the exception
	 */
	public GeoNcWMSMetadataUtility(String wmsRequest) throws Exception{
		this(wmsRequest, DEFAULT_CONNECTION_TIMEOUT);
	}


	/**
	 * Instantiates a new geo get styles utility.
	 *
	 * @param wmsRequest the wms request
	 * @param connectionTimeout the connection timeout sets a specified timeout value, in milliseconds, to be used when opening URLConnection.
	 * @throws Exception the exception
	 */
	public GeoNcWMSMetadataUtility(String wmsRequest, int connectionTimeout) throws Exception{
		this.wmsRequest = wmsRequest;
		this.connectionTimeout = connectionTimeout;
		this.validator = new WmsUrlValidator(wmsRequest);
		validator.parseWmsRequest(true, false);
		validateConnectionTimeout();
		loadMetadata();
	}

	/**
	 * Instantiates a new geo get styles utility.
	 *
	 * @param validator the validator
	 * @throws Exception the exception
	 */
	public GeoNcWMSMetadataUtility(WmsUrlValidator validator) throws Exception{
		this(validator, DEFAULT_CONNECTION_TIMEOUT);
	}


	/**
	 * Instantiates a new geo get styles utility.
	 *
	 * @param validator the validator
	 * @param connectionTimeout the connection timeout sets a specified timeout value, in milliseconds, to be used when opening URLConnection.
	 * @throws Exception the exception
	 */
	public GeoNcWMSMetadataUtility(WmsUrlValidator validator, int connectionTimeout) throws Exception{
		this.wmsRequest = validator.getWmsRequest();
		this.validator = validator;
		this.connectionTimeout = connectionTimeout;
		validateConnectionTimeout();
		loadMetadata();
	}

	/**
	 * Validate connection timeout.
	 */
	private void validateConnectionTimeout(){
		if(this.connectionTimeout<=0)
			this.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	}

	/**
	 * Load metadata.
	 *
	 * @throws Exception the exception
	 */
	private void loadMetadata() throws Exception{
	    String uriWMSService = validator.getBaseWmsServiceUrl();
	    String layerName = validator.getValueOfParsedWMSParameter(WmsParameters.LAYERS);
	    this.ncMetadata = NcWmsGetMetadata.getMetadata(uriWMSService, layerName, connectionTimeout, METADATA.values());
	}


	/**
	 * Load styles for a layer (WMS or ncWMS). This method puts styles for a layer into List {@link LayerStyles#getGeoStyles()}
	 * and override the value of {@link NcWmsGetMetadataRequest#COLORSCALERANGE} for ncWMS styles and put it into Map {@link LayerStyles#getMapNcWmsStyles()}
	 *
	 * @return the styles
	 */
	public LayerStyles loadStyles(){

	    String uriWMSService = validator.getBaseWmsServiceUrl();
	    String layerName = validator.getValueOfParsedWMSParameter(WmsParameters.LAYERS);
	    String versionWms = validator.getValueOfParsedWMSParameter(WmsParameters.VERSION);
	    String crs = validator.getValueOfParsedWMSParameter(WmsParameters.CRS);
	    layerStyles = new LayerStyles();
		Map<String, String> mapNcWmsStyles = null;
		List<String> styles;
	    boolean isNcWms = false;
		try {

			//OVERRIDE ncWMS COLORSCALERANGE='auto'
			if(validator.getMapWmsNoStandardParams()!=null){
				mapNcWmsStyles = new HashMap<String, String>(validator.getMapWmsNoStandardParams().size());
//				mapNcWmsStyles.putAll(validator.getMapWmsNotStandardParams());

				if(validator.getMapWmsNoStandardParams().containsKey(NcWmsGetMetadataRequest.COLORSCALERANGE) || validator.getMapWmsNoStandardParams().containsKey(NcWmsGetMetadataRequest.COLORSCALERANGE.toLowerCase())){
					isNcWms = true;
					logger.debug("Ovveriding "+NcWmsGetMetadataRequest.COLORSCALERANGE +"?");
					String value = validator.getMapWmsNoStandardParams().get(NcWmsGetMetadataRequest.COLORSCALERANGE);

					if(value.compareToIgnoreCase(NcWmsGetMetadataRequest.COLORSCALERANGE_DEFAULT_VALUE)==0){ //COLORSCALERANGE == 'auto'?
						logger.trace(NcWmsGetMetadataRequest.COLORSCALERANGE +" found like 'auto'..");
						String[] minmax = NcWmsGetMetadataRequest.getColorScaleRange(uriWMSService,
							versionWms,
							validator.getValueOfParsedWMSParameter(WmsParameters.BBOX),
							layerName,
							crs,
							validator.getValueOfParsedWMSParameter(WmsParameters.WIDTH),
							validator.getValueOfParsedWMSParameter(WmsParameters.HEIGHT),connectionTimeout);

						if(minmax!=null && minmax.length==2 && minmax[0]!=null && minmax[1]!=null){
							String valueMinMax =  minmax[0]+","+minmax[1];
							mapNcWmsStyles.put(NcWmsGetMetadataRequest.COLORSCALERANGE, valueMinMax);
							logger.debug("Overrided "+NcWmsGetMetadataRequest.COLORSCALERANGE +" min,max value: "+valueMinMax);
						}
					}else{
						logger.debug(NcWmsGetMetadataRequest.COLORSCALERANGE +" is not \'auto', skipping override");
					}
				}else{ //NcWmsGetColorScaleRange.COLORSCALERANGE not found
					logger.debug(NcWmsGetMetadataRequest.COLORSCALERANGE +" not found");

					String[] minmax = NcWmsGetMetadataRequest.getColorScaleRange(uriWMSService,
							versionWms,
							validator.getValueOfParsedWMSParameter(WmsParameters.BBOX),
							layerName,
							crs,
							validator.getValueOfParsedWMSParameter(WmsParameters.WIDTH),
							validator.getValueOfParsedWMSParameter(WmsParameters.HEIGHT),connectionTimeout);

						if(minmax!=null && minmax.length==2 && minmax[0]!=null && minmax[1]!=null){
							isNcWms = true;
							logger.debug(NcWmsGetMetadataRequest.GET_METADATA +" works, adding "+NcWmsGetMetadataRequest.COLORSCALERANGE + " with minmax");
							String valueMinMax =  minmax[0]+","+minmax[1];
							mapNcWmsStyles.put(NcWmsGetMetadataRequest.COLORSCALERANGE, valueMinMax);
							logger.debug("Added "+NcWmsGetMetadataRequest.COLORSCALERANGE +" min,max value: "+valueMinMax);
						}else{
							logger.debug(NcWmsGetMetadataRequest.GET_METADATA +" failed, skip "+NcWmsGetMetadataRequest.COLORSCALERANGE +" management, is not raster?");
						}
				}
			}
		}catch (Exception e) {
			logger.error("Exception during ncWMS get style: "+e);
		}

		logger.trace("versionWms found: "+versionWms);
		logger.trace("crs found: "+crs);

		String stylesRead = validator.getValueOfParsedWMSParameter(WmsParameters.STYLES);
		logger.trace("styles found: "+stylesRead);


		//VALIDATION STYLES
		if(stylesRead==null || stylesRead.isEmpty()){

			logger.trace("styles are empty or null - Trying to get styles by 'WMS Get Style'");
			WmsGetStyles wmsGetStyles = new WmsGetStyles(connectionTimeout);
			styles = wmsGetStyles.getStylesFromWms(uriWMSService, layerName);

			if(wmsGetStyles.getResponseCode()==200){
				logger.debug("WMS Get Style found: "+styles);
			}else{
				logger.debug("Wms GetStyles not found, Trying to get styles by 'NcWmsGetMetadata'");
				isNcWms = true;
				try{

					if(ncMetadata==null){
						METADATA[] meta = new METADATA[3];
						meta[0] = METADATA.SUPPORTEDSTYLES;
						meta[1] = METADATA.DEFAULTPALETTE;
						meta[2] = METADATA.PALETTES;
						this.ncMetadata = NcWmsGetMetadata.getMetadata(uriWMSService, layerName, connectionTimeout, meta);
					}

					if(ncMetadata!=null && ncMetadata.getResponseCode()==200){
						//STYLES
						if(ncMetadata.getSupportedStyles().size()>0){
							styles.add(ncMetadata.getSupportedStyles().get(0)+"/"+ncMetadata.getDefaultPalette()); //DEFAULT STYLE
							logger.debug("added ncWms default style: "+ncMetadata.getSupportedStyles().get(0)+"/"+ncMetadata.getDefaultPalette());

							for (String s : ncMetadata.getSupportedStyles()) {
								for (String p : ncMetadata.getPalettes()) {
									String as = s+"/"+p;
									if(!styles.contains(as)){
										styles.add(as);
										logger.trace("added ncWMS style: "+as);
									}
								}
							}
						}
					}
				}catch(Exception e){
					logger.error(e);
					styles = validator.getStylesAsList();
				}
			}
		}else{
			styles = validator.getStylesAsList();
		}
		logger.trace("returning style: "+styles);
		layerStyles.setGeoStyles(styles);
		layerStyles.setMapNcWmsStyles(mapNcWmsStyles);
		layerStyles.setNcWms(isNcWms);
		return layerStyles;
//		this.geoStyles = styles;
	}


	/**
	 * Load z-axis values for a ncWMS layer and put it into {@link #getZAxis()}
	 * @return
	 *
	 * @return the z axis
	 * @throws Exception
	 */
	public LayerZAxis loadZAxis() throws Exception{

		if(ncMetadata==null){
			String uriWMSService = validator.getBaseWmsServiceUrl();
			String layerName = validator.getValueOfParsedWMSParameter(WmsParameters.LAYERS);
			METADATA[] meta = new METADATA[1];
			meta[0] = METADATA.Z_AXIS;
			this.ncMetadata = NcWmsGetMetadata.getMetadata(uriWMSService, layerName, connectionTimeout, meta);
		}

		if(this.ncMetadata==null)
			this.zAxis = null;
		else
			this.zAxis = this.ncMetadata.getZAxis();

		return this.zAxis;
	}


	/**
	 * @return the zAxis
	 */
	public LayerZAxis getZAxis() {

		return zAxis;
	}

	/**
	 * @return the layerStyles
	 */
	public LayerStyles getLayerStyles() {

		return layerStyles;
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
	 * Gets the map wms no standard parameters.
	 *
	 * @return the map wms no standard parameters
	 */
	public Map<String, String> getMapWmsNoStandardParameters(){
		return validator.getMapWmsNoStandardParams();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GeoNcWMSMetadataUtility [wmsRequest=");
		builder.append(wmsRequest);
		builder.append(", validator=");
		builder.append(validator);
		builder.append(", connectionTimeout=");
		builder.append(connectionTimeout);
		builder.append(", ncMetadata=");
		builder.append(ncMetadata);
		builder.append(", zAxis=");
		builder.append(zAxis);
		builder.append(", layerStyles=");
		builder.append(layerStyles);
		builder.append("]");
		return builder.toString();
	}
}
