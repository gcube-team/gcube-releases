package org.gcube.portlets.user.gisviewer.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.common.geoserverinterface.GeoCaller;
import org.gcube.common.geoserverinterface.GeoCaller.FILTER_TYPE;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;
import org.gcube.common.geoserverinterface.GeoserverCaller;
import org.gcube.common.geoserverinterface.bean.CoverageTypeRest;
import org.gcube.common.geoserverinterface.bean.CswLayersResult;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.bean.GroupRest;
import org.gcube.common.geoserverinterface.bean.LayerCsw;
import org.gcube.common.geoserverinterface.bean.LayerRest;
import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.GisViewerService;
import org.gcube.portlets.user.gisviewer.client.commons.beans.DataResult;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GeoInformationForWMSRequest;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItemsResult;
import org.gcube.portlets.user.gisviewer.client.commons.beans.Property;
import org.gcube.portlets.user.gisviewer.client.commons.beans.Styles;
import org.gcube.portlets.user.gisviewer.client.commons.beans.TransectParameters;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WebFeatureTable;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WmsRequest;
import org.gcube.portlets.user.gisviewer.client.commons.beans.ZAxis;
import org.gcube.portlets.user.gisviewer.client.commons.utils.URLMakers;
import org.gcube.portlets.user.gisviewer.server.datafeature.ClickDataParser;
import org.gcube.portlets.user.gisviewer.server.datafeature.FeatureParser;
import org.gcube.portlets.user.gisviewer.server.datafeature.FeatureTypeParser;
import org.gcube.spatial.data.geoutility.GeoNcWMSMetadataUtility;
import org.gcube.spatial.data.geoutility.bean.LayerStyles;
import org.gcube.spatial.data.geoutility.bean.LayerZAxis;
import org.gcube.spatial.data.geoutility.bean.WmsParameters;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The Class GisViewerServiceImpl.
 * @author Ceras
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 21, 2016
 */
public abstract class GisViewerServiceImpl extends RemoteServiceServlet implements GisViewerService {

	private static final long serialVersionUID = -2481133840394575925L;
	private static Logger logger = Logger.getLogger(GisViewerServiceImpl.class);
	protected static final boolean BASE_LAYER = true;
	protected static final GeoserverMethodResearch researchMethod = GeoserverMethodResearch.MOSTUNLOAD;
	private Map<String, GeoCaller> geoCallersMap = new HashMap<String, GeoCaller>();

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 * @throws Exception the exception
	 */
	protected abstract GisViewerServiceParameters getParameters() throws Exception;

	/**
	 * Gets the base layers to add gis viewer.
	 *
	 * @return the base layers to add gis viewer
	 * @throws Exception the exception
	 */
	protected abstract List<? extends GisViewerBaseLayerInterface> getBaseLayersToAddGisViewer()throws Exception;


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#getGcubeSecurityToken()
	 */
	public abstract String getGcubeSecurityToken();

	/**
	 * Gets the geo caller.
	 *
	 * @return the geo caller
	 * @throws Exception the exception
	 */
	protected GeoCaller getGeoCaller() throws Exception {
		GisViewerServiceParameters parameters = getParameters();
		String geoserverUrl = parameters.getGeoServerUrl();
		String geonetworkUrl = parameters.getGeoNetworkUrl();
		String gnUser = parameters.getGeoNetworkUser();
		String gnPwd = parameters.getGeoNetworkPwd();
		String gsUser = parameters.getGeoServerUser();
		String gsPwd = parameters.getGeoServerPwd();

		GeoCaller geoCaller = geoCallersMap.get(geonetworkUrl);

		if (geoCaller!=null) {
			logger.debug("-----------GEOCALLER FOUND IN MAP");
			return geoCaller;
		} else {
			logger.debug("-----------GEOCALLER NOT FOUND IN MAP, ADDED...");
			try {
				geoCaller = new GeoCaller(geonetworkUrl, gnUser, gnPwd, geoserverUrl, gsUser, gsPwd, researchMethod);
				geoCallersMap.put(geonetworkUrl, geoCaller);
				return geoCaller;
			} catch (Exception e) {
				throw new Exception("Error initializing the GeoCaller", e);
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#getDataResult(java.util.List)
	 */
	@Override
	public List<DataResult> getDataResult(List<String> urls) {
		List<DataResult> result = new ArrayList<DataResult>();

		for (String url : urls) {
			List<DataResult> oneGeoserverResult = ClickDataParser.getDataResult(url); // TODO adjust url adding wms
			result.addAll(oneGeoserverResult);
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#parseWmsRequest(java.lang.String, java.lang.String)
	 */
	@Override
	public GeoInformationForWMSRequest parseWmsRequest(String wmsRequest, String layerName) throws Exception{
		return loadGeoInfoForWmsRequest(wmsRequest, layerName);
	}

	/**
	 * Load geo info for wms request.
	 *
	 * @param wmsRequest the wms request
	 * @param layerName the layer name
	 * @return the gis viewer wms valid parameters
	 * @throws Exception the exception
	 */
	public static GeoInformationForWMSRequest loadGeoInfoForWmsRequest(String wmsRequest, String layerName) throws Exception{
		try {
			GisViewerWMSUrlValidator validator = new GisViewerWMSUrlValidator(wmsRequest, layerName);
			String wmsServiceHost = validator.getWmsServiceHost();
			String validWMSRequest = validator.parseWMSRequest(true, true);
			layerName = validator.getLayerName();
			String versionWms = validator.getValueOfParsedWMSParameter(WmsParameters.VERSION);
			String crs = validator.getValueOfParsedWMSParameter(WmsParameters.CRS);
	//
			HashMap<String, String> mapWmsNotStandard = new HashMap<String, String>();

			if(validator.getMapWmsNoStandardParams()!=null){
				mapWmsNotStandard.putAll(validator.getMapWmsNoStandardParams());
			}
	//
			GeoNcWMSMetadataUtility geoGS = new GeoNcWMSMetadataUtility(validWMSRequest, 4000);
			//STYLES
			LayerStyles layerStyle  = geoGS.loadStyles();
			Map<String,String> mapNcWmsStyles = layerStyle.getMapNcWmsStyles()==null?new HashMap<String, String>(1):layerStyle.getMapNcWmsStyles();
			mapWmsNotStandard.putAll(mapNcWmsStyles);
			//MAP STYLES INTO GWT-SERIALIZABLE OBJECT
			Styles styles = new Styles(layerStyle.getGeoStyles(), layerStyle.getMapNcWmsStyles(), layerStyle.isNcWms());
			//ZAxis
			LayerZAxis layerZAxis = geoGS.loadZAxis();
			//MAP ZAXIS INTO GWT-SERIALIZABLE OBJECT
			ZAxis zAxis = layerZAxis!=null?new ZAxis(layerZAxis.getUnits(), layerZAxis.isPositive(), layerZAxis.getValues()):null;

			return new GeoInformationForWMSRequest(wmsServiceHost, validWMSRequest, layerName, versionWms, crs, mapWmsNotStandard, styles, zAxis);
		}
		catch (Exception e) {
			String msg = "An error occurred during wms request validation for layer: "+layerName;
			logger.error(msg,e);
			throw new Exception(msg);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#getGroupsInfo(java.lang.String)
	 */
	@Override
	public LayerItemsResult getGroupsInfo(String groupName) {
		// search of geoserver that contains the group
		String gsUrl=null;
		try {
			GeoCaller geoCaller = getGeoCaller();
			gsUrl = geoCaller.getGeoServerForGroup(groupName);
			if (gsUrl==null)
				return null;

			//FIXME manage the exception
			GisViewerServiceParameters parameters = getParameters();
			String gsUser = parameters.getGeoServerUser();
			String gsPwd = parameters.getGeoServerPwd();

			// connecting to the geoserver found
			GeoserverCaller geoserverCaller = new GeoserverCaller(gsUrl, gsUser, gsPwd);
			GroupRest groupRest = geoserverCaller.getLayerGroup(groupName);

			return getLayersInfo(groupRest.getLayers());

		} catch (Exception e) {
			logger.error("gisViewer EXCEPTION: Error in getGroupInfo. Message=\""+e.getMessage()+"\"");
			logger.error("geoserver_url="+gsUrl);
			logger.error("Stacktrace: ",e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#getLayersInfo(java.util.List)
	 */
	@Override
	public LayerItemsResult getLayersInfo(List<String> layersName) {
		LayerItemsResult layerItemsResult = new LayerItemsResult();

		Map<String, GeoserverCaller> geoserverCallers = new HashMap<String, GeoserverCaller>();
		int countVisible=0, n=layersName.size();

		for (String layerName: layersName) {
			try {
				GeoCaller geoCaller = getGeoCaller();

				CswLayersResult cswLayerResult = geoCaller.getLayersFromCsw(null, 1, 10, false, true, FILTER_TYPE.ANY_TEXT, layerName);
				if (cswLayerResult.getLayers().size()==0)
					layerItemsResult.addStatusMessage("- Layer \""+layerName+"\" not found.");
				else {
					LayerCsw layerCsw = null;
					for (LayerCsw l: cswLayerResult.getLayers()) {
						String[] split = l.getName().split(":");
						if (l.getName().contentEquals(layerName) || split.length>1 && layerName.contentEquals(split[1])) {
							layerCsw = l;
							break;
						}
					}
					if (layerCsw==null)
						layerCsw = cswLayerResult.getLayers().get(0);
					String gsUrl = URLMakers.getGeoserverUrlFromWmsUrl(layerCsw.getGeoserverUrl());
//					String gsGwcUrl = URLMakers.getGeoserverGwcUrl(gsUrl);
					String gsWmsUrl = URLMakers.getGeoserverWmsUrl(gsUrl);

					GeoserverCaller geoserverCaller;
					if ((geoserverCaller = geoserverCallers.get(gsUrl))==null) {
						GisViewerServiceParameters parameters = getParameters();
						String gsUser = parameters.getGeoServerUser();
						String gsPwd = parameters.getGeoServerPwd();
						geoserverCaller = new GeoserverCaller(gsUrl, gsUser, gsPwd);
						geoserverCallers.put(gsUrl, geoserverCaller);
					}

					LayerItem layerItem = new LayerItem();
					layerItem.setName(layerName);
					layerItem.setTitle(layerCsw.getTitle());
					layerItem.setLayer(layerCsw.getName());
					layerItem.setVisible(countVisible++>n-3);
					layerItem.setUrl(gsWmsUrl);
					layerItem.setGeoserverUrl(gsUrl);
					layerItem.setGeoserverWmsUrl(gsWmsUrl);

					LayerRest layerRest = geoserverCaller.getLayer(layerName);
					// set the dataStore
					layerItem.setDataStore(layerRest.getDatastore());
					layerItem.setDefaultStyle(layerRest.getDefaultStyle());
					layerItem.setStyle(layerRest.getDefaultStyle());

					layerItem.setBuffer(2);
					if (layerRest.getType().contentEquals("RASTER")) {

						CoverageTypeRest coverageTypeRest = geoserverCaller.getCoverageType(
								layerRest.getWorkspace(),
								layerRest.getCoveragestore(),
								layerName);

						layerItem.setMaxExtent(coverageTypeRest.getLatLonBoundingBox().getMinx(),
								coverageTypeRest.getLatLonBoundingBox().getMiny(),
								coverageTypeRest.getLatLonBoundingBox().getMaxx(),
								coverageTypeRest.getLatLonBoundingBox().getMaxy(),
								coverageTypeRest.getLatLonBoundingBox().getCrs());
						layerItem.setHasLegend(false);
						layerItem.setBaseLayer(BASE_LAYER);
						layerItem.setTrasparent(false);
						layerItem.setClickData(false);
					} else {
						FeatureTypeRest featureTypeRest = geoserverCaller.getFeatureType(layerRest.getWorkspace(),
								layerRest.getDatastore(),
								layerName);

						layerItem.setMaxExtent(featureTypeRest.getLatLonBoundingBox().getMinx(),
								featureTypeRest.getLatLonBoundingBox().getMiny(),
								featureTypeRest.getLatLonBoundingBox().getMaxx(),
								featureTypeRest.getLatLonBoundingBox().getMaxy(),
								featureTypeRest.getLatLonBoundingBox().getCrs());

						layerItem.setBaseLayer(false);
						layerItem.setClickData(true);
						layerItem.setStyles(layerRest.getStyles());
						layerItem.setHasLegend(true);
						layerItem.setTrasparent(true);
					}

					// set layer properties info
					List<Property> properties = FeatureTypeParser.getProperties(gsUrl, layerItem.getLayer());
					layerItem.setProperties(properties);
					// set opacity
					setDefaultOpacity(layerItem);
					layerItemsResult.addLayerItem(layerItem);

				}
			} catch (Exception e) {
				e.printStackTrace();
				layerItemsResult.addStatusMessage("- Layer \""+layerName+"\" not found.");
			}
		}

		boolean isBaseLayer = false;
		for (LayerItem l: layerItemsResult.getLayerItems()) {
			if (!isBaseLayer) isBaseLayer = l.isBaseLayer();
		}
		if (!isBaseLayer && layerItemsResult.getLayerItemsSize() > 0) {
			layerItemsResult.getLayerItems().get(0).setBaseLayer(BASE_LAYER);
		}

		return layerItemsResult;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#getBaseLayersToGisViewer()
	 */
	@Override
	public List<? extends GisViewerBaseLayerInterface> getBaseLayersToGisViewer() {

		try {

			return getBaseLayersToAddGisViewer();
		} catch (Exception e) {
			e.printStackTrace();
			return new DefaultGisViewerServiceImpl().getBaseLayersToGisViewer();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#getLayersInfoByLayerItems(java.util.List)
	 */
	@Override
	public LayerItemsResult getLayersInfoByLayerItems(List<LayerItem> layerItems) {
		LayerItemsResult layerItemsResult = new LayerItemsResult();

		int countVisible=0;
		for (LayerItem layerItem: layerItems) {
			try {
				putLayerInfoInLayerItem(layerItem);
				layerItem.setVisible(countVisible++<2);
				layerItemsResult.addLayerItem(layerItem);
				setDefaultOpacity(layerItem);
			} catch (Exception e) {
				e.printStackTrace();
				layerItemsResult.addStatusMessage("- Layer \""+layerItem.getName()+"\" not found.");
			}
		}

		// set eventually base layer
		boolean isBaseLayer = false;
		for (LayerItem l: layerItemsResult.getLayerItems())
			if (!isBaseLayer) isBaseLayer = l.isBaseLayer();

		if (!isBaseLayer && layerItemsResult.getLayerItemsSize() > 0) {
			layerItemsResult.getLayerItems().get(0).setBaseLayer(BASE_LAYER);
		}

		return layerItemsResult;
	}


	/**
	 * Put layer info in layer item.
	 *
	 * @param layerItem the layer item
	 * @throws Exception the exception
	 */
	private void putLayerInfoInLayerItem(LayerItem layerItem) throws Exception {
		String gsUrl = layerItem.getGeoserverUrl();
		String layerName = layerItem.getName();

		if (gsUrl==null)
			throw new Exception();

		GeoserverCaller geoserverCaller;
		GisViewerServiceParameters parameters = getParameters();
		String gsUser = parameters.getGeoServerUser();
		String gsPwd = parameters.getGeoServerPwd();
		geoserverCaller = new GeoserverCaller(gsUrl, gsUser, gsPwd);
//		String gsGwcUrl = URLMakers.getGeoserverGwcUrl(gsUrl);
		String gsWmsUrl = URLMakers.getGeoserverWmsUrl(gsUrl);
//		layerItem.setUrl(gsGwcUrl);
		layerItem.setUrl(gsWmsUrl);
		layerItem.setGeoserverWmsUrl(gsWmsUrl);

		LayerRest layerRest = geoserverCaller.getLayer(layerName);
		// set the dataStore
		layerItem.setDataStore(layerRest.getDatastore());
		layerItem.setDefaultStyle(layerRest.getDefaultStyle());
		layerItem.setStyle(layerRest.getDefaultStyle());
		layerItem.setBuffer(2);

//		layerItem.setLayer(ServerGeoExtCostants.workSpace+":" + layerName);
		if (layerRest.getType().contentEquals("RASTER")) {
			CoverageTypeRest coverageTypeRest = geoserverCaller.getCoverageType(
					layerRest.getWorkspace(),
					layerRest.getCoveragestore(),
					layerName);

			layerItem.setMaxExtent(coverageTypeRest.getLatLonBoundingBox().getMinx(),
					coverageTypeRest.getLatLonBoundingBox().getMiny(),
					coverageTypeRest.getLatLonBoundingBox().getMaxx(),
					coverageTypeRest.getLatLonBoundingBox().getMaxy(),
					coverageTypeRest.getLatLonBoundingBox().getCrs());
			layerItem.setHasLegend(false);
			layerItem.setBaseLayer(BASE_LAYER);
			layerItem.setTrasparent(false);
			layerItem.setClickData(false);
		} else {
			FeatureTypeRest featureTypeRest = geoserverCaller.getFeatureType(layerRest.getWorkspace(),
					layerRest.getDatastore(),
					layerName);

			layerItem.setMaxExtent(featureTypeRest.getLatLonBoundingBox().getMinx(),
					featureTypeRest.getLatLonBoundingBox().getMiny(),
					featureTypeRest.getLatLonBoundingBox().getMaxx(),
					featureTypeRest.getLatLonBoundingBox().getMaxy(),
					featureTypeRest.getLatLonBoundingBox().getCrs());

			layerItem.setBaseLayer(false);
			layerItem.setClickData(true);
			layerItem.setStyles(layerRest.getStyles());
			layerItem.setHasLegend(true);

			layerItem.setTrasparent(true);
		}

		// set layer properties info
		List<Property> properties = FeatureTypeParser.getProperties(gsUrl, layerItem.getLayer());
		layerItem.setProperties(properties);
	}

	/**
	 * Gets the layer info by layer name.
	 *
	 * @param layerName the layer name
	 * @param geoserverCallers the geoserver callers
	 * @return the layer info by layer name
	 * @throws Exception the exception
	 */
	private LayerItem getLayerInfoByLayerName(String layerName, Map<String, GeoserverCaller> geoserverCallers) throws Exception {
		GeoCaller geoCaller = getGeoCaller();

		CswLayersResult cswLayerResult = geoCaller.getLayersFromCsw(null, 1, 1, false, true, FILTER_TYPE.ANY_TEXT, layerName);
		if (cswLayerResult.getLayers().size()==0)
			throw new Exception("CswLayerResult is empty.");
		else {
			LayerCsw layerCsw = cswLayerResult.getLayers().get(0);
			String gsUrl = URLMakers.getGeoserverUrlFromWmsUrl(layerCsw.getGeoserverUrl());
//			String gsGwcUrl = URLMakers.getGeoserverGwcUrl(gsUrl);
			String gsWmsUrl = URLMakers.getGeoserverWmsUrl(gsUrl);

			GeoserverCaller geoserverCaller;
			if ((geoserverCaller = geoserverCallers.get(gsUrl))==null) {
				GisViewerServiceParameters parameters = getParameters();
				String gsUser = parameters.getGeoServerUser();
				String gsPwd = parameters.getGeoServerPwd();
				geoserverCaller = new GeoserverCaller(gsUrl, gsUser, gsPwd);
				geoserverCallers.put(gsUrl, geoserverCaller);
			}

			LayerItem layerItem = new LayerItem();
			layerItem.setName(layerName);
			layerItem.setTitle(layerCsw.getTitle());
			layerItem.setLayer(layerCsw.getName());
//			layerItem.setUrl(gsGwcUrl);
			layerItem.setUrl(gsWmsUrl);
			layerItem.setGeoserverUrl(gsUrl);
			layerItem.setGeoserverWmsUrl(gsWmsUrl);

			LayerRest layerRest = geoserverCaller.getLayer(layerName);
			// set the dataStore
			layerItem.setDataStore(layerRest.getDatastore());
			layerItem.setDefaultStyle(layerRest.getDefaultStyle());
			layerItem.setStyle(layerRest.getDefaultStyle());
			layerItem.setBuffer(2);

			if (layerRest.getType().contentEquals("RASTER")) {

				CoverageTypeRest coverageTypeRest = geoserverCaller.getCoverageType(
						layerRest.getWorkspace(),
						layerRest.getCoveragestore(),
						layerName);

				layerItem.setMaxExtent(coverageTypeRest.getLatLonBoundingBox().getMinx(),
						coverageTypeRest.getLatLonBoundingBox().getMiny(),
						coverageTypeRest.getLatLonBoundingBox().getMaxx(),
						coverageTypeRest.getLatLonBoundingBox().getMaxy(),
						coverageTypeRest.getLatLonBoundingBox().getCrs());
				layerItem.setHasLegend(false);
				layerItem.setBaseLayer(BASE_LAYER);
				layerItem.setTrasparent(false);
				layerItem.setClickData(false);
			} else {
				FeatureTypeRest featureTypeRest = geoserverCaller.getFeatureType(layerRest.getWorkspace(),
						layerRest.getDatastore(),
						layerName);

				layerItem.setMaxExtent(featureTypeRest.getLatLonBoundingBox().getMinx(),
						featureTypeRest.getLatLonBoundingBox().getMiny(),
						featureTypeRest.getLatLonBoundingBox().getMaxx(),
						featureTypeRest.getLatLonBoundingBox().getMaxy(),
						featureTypeRest.getLatLonBoundingBox().getCrs());

				layerItem.setBaseLayer(false);
				layerItem.setClickData(true);
				layerItem.setStyles(layerRest.getStyles());
				layerItem.setHasLegend(true);

				layerItem.setTrasparent(true);
			}

			// set layer properties info
			List<Property> properties = FeatureTypeParser.getProperties(gsUrl, layerItem.getLayer());
			layerItem.setProperties(properties);
			setDefaultOpacity(layerItem);

			return layerItem;
		}
	}

	/**
	 * Sets the default opacity.
	 *
	 * @param layerItem the new default opacity
	 */
	private void setDefaultOpacity(LayerItem layerItem) {
		if (layerItem.getOpacity()<0) {
			layerItem.setOpacity(Constants.defaultOpacityLayers);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TransectParameters getTransectParameters() {
		try {
			GisViewerServiceParameters prm = getParameters();
			TransectParameters tp = new TransectParameters(prm.getTransectUrl(), prm.getScope());
			logger.info("getTransectParameters returning: "+tp);
			return tp;
		} catch (Exception e) {
			logger.error("Error on getting transect parameters",e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#getDataResult(java.util.List, java.lang.String, int)
	 */
	@Override
	public List<WebFeatureTable> getDataResult(List<LayerItem> layerItems, String bbox, int maxWFSFeature, int zoomLevel) {
		String dataMinerURL = "";
		try {
			GisViewerServiceParameters parameters = getParameters();
			dataMinerURL = parameters.getDataMinerUrl();
		}
		catch (Exception e) {
			logger.error("Error on retrieving DataMiner URL from parameters, returning empty list of features");
			return new ArrayList<WebFeatureTable>();
		}
		List<WebFeatureTable> result = FeatureParser.getDataResults(layerItems, bbox, maxWFSFeature, getGcubeSecurityToken(), dataMinerURL, zoomLevel);
		return result;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#getListProperty(java.lang.String, org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem)
	 */
	@Override
	public List<Property> getListProperty(String geoserverUrl, LayerItem layer){
		return FeatureTypeParser.getProperties(geoserverUrl, layer.getLayer());
	}

	/**
	 * Parses the wms request.
	 *
	 * @param request the request
	 * @return a WMS request
	 * @throws Exception the exception
	 */
	@Override
	public String parseWmsRequest(WmsRequest request) throws Exception{
		try {
			return MapGeneratorUtils.createWmsRequest(request);
		} catch (Exception e) {
			logger.error("Error on creating wms request",e);
			throw new Exception("Sorry, an error occurred when creating wms request, try again later");
		}
	}
}
