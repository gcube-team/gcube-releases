
package org.gcube.portlets.user.gisviewerapp.server;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GeoInformationForWMSRequest;
import org.gcube.portlets.user.gisviewer.server.DefaultGisViewerServiceImpl;
import org.gcube.portlets.user.gisviewerapp.client.rpc.GisViewerAppService;
import org.gcube.portlets.user.gisviewerapp.shared.GeoInformation;
import org.gcube.portlets.user.gisviewerapp.shared.GeoStyles;
import org.gcube.spatial.data.geoutility.GeoNcWMSMetadataUtility;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;
import org.gcube.spatial.data.geoutility.wms.WmsUrlValidator;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The Class GisViewerAppServiceImpl.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 17, 2017
 */
@SuppressWarnings("serial")
public class GisViewerAppServiceImpl extends RemoteServiceServlet implements GisViewerAppService {

	private static Logger logger =Logger.getLogger(GisViewerAppServiceImpl.class);

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewerapp.client.rpc.GisViewerAppService#getStylesForWmsRequest(java.lang.String)
	 */
	/**
	 * Gets the styles for wms request.
	 *
	 * @param wmsRequest the wms request
	 * @return the styles for wms request
	 */
	@Deprecated
	public GeoStyles getStylesForWmsRequest(String wmsRequest) {

		try {
			logger.info("Tentative get styles for wmsRequest: "+wmsRequest);
			GeoNcWMSMetadataUtility geoGetStyles = new GeoNcWMSMetadataUtility(wmsRequest);

//			GeoStyles geo = new GeoStyles();
//			geo.setStyles(geoGetStyles.getGeoStyles());
//			geo.setNcWMS(geoGetStyles.isNcWms());
//			geo.setMapNcWmsStyles(geoGetStyles.getMapNcWmsStyles());
			return null;
		}
		catch (Exception e) {
			logger.error("An error occurred during get styles, returning an empty GeoStyles: ",e);
			return new GeoStyles();
		}
	}

	/**
	 * Gets the parameters for wms request.
	 *
	 * @param wmsRequest the wms request
	 * @param displayName the display name
	 * @return the parameters for wms request
	 * @throws Exception the exception
	 */
	@Deprecated
	public GeoInformation getParametersForWmsRequest(String wmsRequest, String displayName) throws Exception {

		try {
			logger.info("Tentative to get GeoInformation object for wmsRequest: "+wmsRequest);

			WmsUrlValidator urlValidator = new WmsUrlValidator(wmsRequest);
			String displayLayerName;
			String title;
			urlValidator.parseWmsRequest(true,false);
			String layerName = urlValidator.getValueOfParsedWMSParameter(WmsParameters.LAYERS);

			if(layerName==null || layerName.isEmpty()){
				Window.alert("Bad wms request LAYER parameter not found!");
				throw new LayerNameNotFound("Layer name not found!");
			}else{
				displayLayerName = layerName;
				title = layerName;
				if(displayName!=null && !displayName.isEmpty()) {
					displayLayerName = displayName;
				}
			}

			GeoInformationForWMSRequest geoInformation = DefaultGisViewerServiceImpl.loadGeoInfoForWmsRequest(wmsRequest, layerName);

			GeoInformation geoInfo = new GeoInformation();
			geoInfo.setTitle(title);
			geoInfo.setDisplayName(displayLayerName);
			geoInfo.setLayerName(layerName);
			geoInfo.setParametersMap(urlValidator.getMapWmsParameters());

			/*HashMap<String, String> mapWmsNotStandard = new HashMap<String, String>(urlValidator.getMapWmsNoStandardParams().size());
			mapWmsNotStandard.putAll(urlValidator.getMapWmsNoStandardParams());
			logger.trace("wms no standard params: "+mapWmsNotStandard);
			*/

//			GeoStyles geo = getStylesForWmsRequest(wmsRequest);
//			logger.trace("overriding wms no standard styles: "+geo.getMapNcWmsStyles());

			GeoStyles geoStyles = new GeoStyles();
			geoStyles.setStyles(geoInformation.getStyles().getGeoStyles());
			geoStyles.setNcWMS(geoInformation.isNcWMS());
			geoStyles.setMapNcWmsStyles(geoInformation.getStyles().getMapNcWmsStyles());
//			mapWmsNotStandard.putAll(geoInformation.getMapWMSNoStandard());
//			logger.trace("final wms no standard params: "+mapWmsNotStandard);
//			geoInfo.setGeoStyle(geoInformation.getStyles());
			geoInfo.setNoWmsParameters(geoInformation.getMapWMSNoStandard());

			logger.info("returning: "+geoInfo);
//			 RequestDispatcher dispatcher=getServletContext().getRequestDispatcher(DefaultGisViewerServiceImpl.class.getSimpleName());
//			 dispatcher.forward( request, response );


			return geoInfo;
		}catch (LayerNameNotFound e1){
			throw new Exception("Layer name not found!");
		}catch (Exception e) {
			logger.error("An error occurred during GetInformation build, returning an empty GetInformation: ",e);
			throw new Exception("An error occurred during wms service request, try again later");
		}
	}
}
