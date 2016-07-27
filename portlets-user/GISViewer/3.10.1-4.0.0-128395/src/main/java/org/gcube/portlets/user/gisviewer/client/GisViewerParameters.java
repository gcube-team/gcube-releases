package org.gcube.portlets.user.gisviewer.client;

import java.util.Arrays;
import java.util.List;


public class GisViewerParameters {	

	private String projection = null;
	private String openingGroup = null;
	private List<String> openingLayers = null;
	private GisViewerSaveHandler gisViewerSaveHandler = null;
	private DataPanelOpenListener dataPanelOpenHandler = null;
	private boolean openDataPanelAtStart = Constants.geoWindowDataPanelOpenedAtStart;
	
	//protected static GisViewerParameters instance = new GisViewerParameters();
//	public static GisViewerParameters getInstance() {
//		return instance;
//	}
	
	public GisViewerParameters(){		
	}
	
	
	
	/**
	 * @param projection
	 * @param groupName
	 * @param layers
	 * @param gisViewerSaveHandler
	 */
	public GisViewerParameters(String projection, String openingGroup, List<String> openingLayers, GisViewerSaveHandler gisViewerSaveHandler) {
		this.projection = projection;
		this.openingGroup = openingGroup;
		this.openingLayers = openingLayers;
		this.gisViewerSaveHandler = gisViewerSaveHandler;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public void setOpeningGroup(String groupName) {
		this.openingGroup = groupName;
	}

	public void setOpeningLayers(List<String> layers) {
		this.openingLayers = layers;
	}

	public void setOpeningLayers(String[] layers) {
		this.openingLayers = Arrays.asList(layers);
	}


	public String getProjection() {
		return projection;
	}
	
	public String getOpeningGroup() {
		return this.openingGroup;
	}

	public List<String> getOpeningLayers() {
		return this.openingLayers;
	}
	
	public void setGisViewerSaveHandler(GisViewerSaveHandler gisViewerSaveHandler) {
		this.gisViewerSaveHandler = gisViewerSaveHandler;
	}
	
	public GisViewerSaveHandler getGisViewerSaveHandler() {
		return gisViewerSaveHandler;
	}

	/**
	 * @return the openDataPanelAtStart
	 */
	public boolean isOpenDataPanelAtStart() {
		return openDataPanelAtStart;
	}

	/**
	 * @param openDataPanelAtStart the openDataPanelAtStart to set
	 */
	public void setOpenDataPanelAtStart(boolean openDataPanelAtStart) {
		this.openDataPanelAtStart = openDataPanelAtStart;
	}
	
	/**
	 * @param dataPanelOpenHandler the dataPanelOpenHandler to set
	 */
	public void setDataPanelOpenListener(DataPanelOpenListener dataPanelOpenHandler) {
		this.dataPanelOpenHandler = dataPanelOpenHandler;
	}
	
	/**
	 * @return the dataPanelOpenHandler
	 */
	public DataPanelOpenListener getDataPanelOpenHandler() {
		return dataPanelOpenHandler;
	}
}
