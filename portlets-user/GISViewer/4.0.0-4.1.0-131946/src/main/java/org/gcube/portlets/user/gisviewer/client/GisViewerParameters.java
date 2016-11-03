package org.gcube.portlets.user.gisviewer.client;


/**
 * The Class GisViewerParameters.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 26, 2016
 */
public class GisViewerParameters {

	private String projection = null;
	private GisViewerSaveHandler gisViewerSaveHandler = null;
	private DataPanelOpenListener dataPanelOpenHandler = null;
	private boolean openDataPanelAtStart = Constants.geoWindowDataPanelOpenedAtStart;

	/**
	 * Instantiates a new gis viewer parameters.
	 */
	public GisViewerParameters(){
	}

	/**
	 * Instantiates a new gis viewer parameters.
	 *
	 * @param projection the projection
	 * @param gisViewerSaveHandler the gis viewer save handler
	 */
	public GisViewerParameters(String projection, GisViewerSaveHandler gisViewerSaveHandler) {
		this.projection = projection;
		this.gisViewerSaveHandler = gisViewerSaveHandler;
	}

	/**
	 * Sets the projection.
	 *
	 * @param projection the new projection
	 */
	public void setProjection(String projection) {
		this.projection = projection;
	}

	/**
	 * Gets the projection.
	 *
	 * @return the projection
	 */
	public String getProjection() {
		return projection;
	}

	/**
	 * Sets the gis viewer save handler.
	 *
	 * @param gisViewerSaveHandler the new gis viewer save handler
	 */
	public void setGisViewerSaveHandler(GisViewerSaveHandler gisViewerSaveHandler) {
		this.gisViewerSaveHandler = gisViewerSaveHandler;
	}

	/**
	 * Gets the gis viewer save handler.
	 *
	 * @return the gis viewer save handler
	 */
	public GisViewerSaveHandler getGisViewerSaveHandler() {
		return gisViewerSaveHandler;
	}

	/**
	 * Checks if is open data panel at start.
	 *
	 * @return the openDataPanelAtStart
	 */
	public boolean isOpenDataPanelAtStart() {
		return openDataPanelAtStart;
	}

	/**
	 * Sets the open data panel at start.
	 *
	 * @param openDataPanelAtStart the openDataPanelAtStart to set
	 */
	public void setOpenDataPanelAtStart(boolean openDataPanelAtStart) {
		this.openDataPanelAtStart = openDataPanelAtStart;
	}

	/**
	 * Sets the data panel open listener.
	 *
	 * @param dataPanelOpenHandler the dataPanelOpenHandler to set
	 */
	public void setDataPanelOpenListener(DataPanelOpenListener dataPanelOpenHandler) {
		this.dataPanelOpenHandler = dataPanelOpenHandler;
	}

	/**
	 * Gets the data panel open handler.
	 *
	 * @return the dataPanelOpenHandler
	 */
	public DataPanelOpenListener getDataPanelOpenHandler() {
		return dataPanelOpenHandler;
	}
}
