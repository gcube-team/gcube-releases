/**
 *
 */
package org.gcube.portlets.user.gisviewer.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;
import org.gcube.portlets.user.gisviewer.server.baselayer.BaseLayerPropertyReader;
import org.gcube.portlets.user.gisviewer.server.exception.PropertyFileNotFoundException;
import org.gcube.portlets.user.gisviewer.server.gisconfiguration.GisConfigurationPropertyReader;


/**
 * The Class DefaultGisViewerServiceImpl.
 *
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * updated By Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class DefaultGisViewerServiceImpl extends GisViewerServiceImpl {

	private static final long serialVersionUID = 7965911406156513171L;
	private static Logger logger = Logger.getLogger(DefaultGisViewerServiceImpl.class);
	protected GisViewerServiceParameters parameters;


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.server.GisViewerServiceImpl#getParameters()
	 */
	@Override
	protected GisViewerServiceParameters getParameters() {

		GisConfigurationPropertyReader gisConf = null;
		try {
			gisConf = new GisConfigurationPropertyReader(true);
			if (parameters == null) parameters = new GisViewerServiceParameters(gisConf.getGeoServerUrl(), gisConf.getGeoServerUser(), gisConf.getGeoServerPwd(), gisConf.getGeoNetworkUrl(), gisConf.getGeoNetworkUser(), gisConf.getGeoNetworkPwd(), gisConf.getTransectUrl(), gisConf.getDataMinerUrl(), gisConf.getScope());
		}catch (PropertyFileNotFoundException e){
			logger.error("Property file: "+GisConfigurationPropertyReader.GIS_CONFIGURATION_FILE +", not found using static configurations");
			try {
				gisConf = new GisConfigurationPropertyReader(false);
			} catch (PropertyFileNotFoundException e1) {
				//SILENT
			}
			try {
				parameters = new GisViewerServiceParameters(gisConf.getGeoServerUrl(), gisConf.getGeoServerUser(), StringEncrypter.getEncrypter().decrypt(gisConf.getGeoServerPwd()), gisConf.getGeoNetworkUrl(), gisConf.getGeoNetworkUser(), StringEncrypter.getEncrypter().decrypt(gisConf.getGeoNetworkPwd()), gisConf.getTransectUrl(), gisConf.getDataMinerUrl(), gisConf.getScope());
			} catch (Exception e1) {
				logger.warn("Error on decripting static pwd");
			}
		}

		return parameters;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.server.GisViewerServiceImpl#getBaseLayersToAddGisViewer()
	 */
	@Override
	protected List<? extends GisViewerBaseLayerInterface> getBaseLayersToAddGisViewer() throws Exception {

		BaseLayerPropertyReader bl = null;
		try{
			logger.info("Instancing BaseLayerPropertyReader");
			bl = new BaseLayerPropertyReader();
			logger.info("Read base layer: "+bl);
		}catch(Exception e){
			logger.error("Error on reading BaseLayerPropertyReader! Returning hardcode wms base layer");
			bl = null;
		}

		final String layerName = bl!=null && bl.getLayerName()!=null?bl.getLayerName():"aquamaps:TrueMarble.16km.2700x1350";
		final String layerTitle = bl!=null && bl.getLayerTitle()!=null?bl.getLayerTitle():"True Marble";
		final String layerWmsUrl = bl!=null && bl.getLayerWmsUrl()!=null?bl.getLayerWmsUrl():"http://geoserver1.d4science.org/geoserver/aquamaps/wms";

		GisViewerBaseLayerInterface trueMarble = new GisViewerBaseLayerInterface() {

			@Override
			public boolean isDisplay() {
				return true;
			}

			@Override
			public String getWmsServiceBaseURL() {
				return layerWmsUrl;
			}

			@Override
			public String getTitle() {
				return layerTitle;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return layerName;
			}
		};

		List<GisViewerBaseLayerInterface> baseLayer = new ArrayList<GisViewerBaseLayerInterface>(1);
		baseLayer.add(trueMarble);

		return baseLayer;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerService#getGcubeSecurityToken()
	 */
	@Override
	public String getGcubeSecurityToken() {
		return null;
	}

}
