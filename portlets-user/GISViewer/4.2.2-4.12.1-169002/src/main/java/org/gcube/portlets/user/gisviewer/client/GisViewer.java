package org.gcube.portlets.user.gisviewer.client;

import java.util.List;

import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;
import org.gcube.portlets.user.gisviewer.client.resources.Resources;

import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;


/**
 * The Class GisViewer.
 * @author Ceras
 * updated by
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 26, 2016
 */
public class GisViewer extends Window {

	public GisViewerPanel gg;
	public static Resources resources = GWT.create(Resources.class);
	public static GisViewerServiceAsync service = (GisViewerServiceAsync) GWT.create(GisViewerService.class);


	/**
	 * Instantiates a new gis viewer.
	 */
	public GisViewer() {
		this(new GisViewerParameters());
	}

	/**
	 * Instantiates a new gis viewer.
	 *
	 * @param parameters the parameters
	 */
	public GisViewer(GisViewerParameters parameters) {
		super();
		this.setHeading(Constants.geoWindowTitle);
		this.setSize(Constants.geoWindowWidth, Constants.geoWindowHeight);
		this.setMinWidth(Constants.geoWindowMinWidth);
		this.setMinHeight(Constants.geoWindowMinHeight);
		this.setMaximizable(true);
		this.setLayout(new FitLayout());

		if (Constants.geoWindowShadow) {
			this.setStyleAttribute("padding", "10px");
		}

		gg = new GisViewerPanel(parameters);
		this.add(gg);

		this.addListener(Events.Move, new Listener<BoxComponentEvent>(){
			@Override
			public void handleEvent(BoxComponentEvent be) {
				if (gg!=null) {
					gg.updateOpenLayersSize();
				}
			}
		});
	}

	/**
	 * Add base layer to Open Layer Map.
	 *
	 * @param layers the layers
	 *
	 */
	public void addBaseLayersToOLM(List<? extends GisViewerBaseLayerInterface> layers) {
		gg.addBaseLayersToOLM(layers);
	}

	/**
	 * Removes the all layers.
	 */
	public void removeAllLayers() {
		gg.removeAllLayers();
	}


	/**
	 * Adds the layer by wms.
	 *
	 * @param layerTitle the layer title
	 * @param layerName the layer name
	 * @param wmsRequest the wms request
	 * @param isBase the is base
	 * @param UUID the uuid
	 */
	public void addLayerByWms(String layerTitle, String layerName, String wmsRequest, boolean isBase, String UUID) {

		boolean displayInLayerSwitcher = false;
		if(isBase)
			displayInLayerSwitcher = true;

		gg.addLayerByWmsRequest(layerTitle, layerName, wmsRequest, isBase, displayInLayerSwitcher, UUID, true);
	}


	/**
	 * Adds the layer by wms.
	 *
	 * @param layerTitle the layer title
	 * @param layerName the layer name
	 * @param wmsRequest the wms request
	 */
	public void addLayerByWms(String layerTitle, String layerName, String wmsRequest) {
		gg.addLayerByWmsRequest(layerTitle, layerName, wmsRequest, false, false, null, true);
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.Window#show()
	 */
	@Override
	public void show() {
		super.show();
		gg.showIntro();
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return Constants.VERSION;
	}
}
