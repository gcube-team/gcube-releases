package org.gcube.portlets.user.gisviewer.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.resources.Resources;

import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

/**
 * The Class GisViewer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 27, 2015
 */
public class GisViewer extends Window {

	public GisViewerPanel gg;
	private boolean first = true;
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
	 * Adds the layers by names.
	 *
	 * @param layerNames the layer names
	 */
	public void addLayersByNames(String ... layerNames) {
		gg.addLayersByLayerNames(Arrays.asList(layerNames));
	}

	/**
	 * Adds the layers by names.
	 *
	 * @param layerNames the layer names
	 */
	public void addLayersByNames(List<String> layerNames) {
		gg.addLayersByLayerNames(layerNames);
	}

	/**
	 * Adds the layer by name.
	 *
	 * @param layerName the layer name
	 */
	public void addLayerByName(String layerName) {
		List<String> layerNames = new ArrayList<String>();
		layerNames.add(layerName);
		gg.addLayersByLayerNames(layerNames);
	}


	/**
	 * Adds the layers by layer items.
	 *
	 * @param layerItems the layer items
	 */
	public void addLayersByLayerItems(List<LayerItem> layerItems) {
		gg.addLayersByLayerItems(layerItems);
	}

	/**
	 * Adds the layer by layer item.
	 *
	 * @param layerItem the layer item
	 */
	public void addLayerByLayerItem(LayerItem layerItem) {
		gg.addLayerByLayerItem(layerItem);
	}

	/**
	 * Adds the group.
	 *
	 * @param groupName the group name
	 */
	public void addGroup(String groupName) {
		gg.addGroupByGroupName(groupName);
	}

	/**
	 * Adds the layers by layer items to top.
	 *
	 * @param layerItems the layer items
	 */
	public void addLayersByLayerItemsToTop(List<LayerItem> layerItems) {
		gg.addLayersByLayerItemsToTop(layerItems);
	}

	/**
	 * Adds the layer by layer item to top.
	 *
	 * @param layerItem the layer item
	 */
	public void addLayerByLayerItemToTop(LayerItem layerItem) {
		gg.addLayerByLayerItemToTop(layerItem);
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
	 * @param title the title
	 * @param layerName the layer name
	 * @param wmsRequest the wms request
	 * @param isBase the is base
	 * @param displayInLayerSwitcher the display in layer switcher
	 * @param isNcWMS the is nc wms
	 * @param UUID the uuid
	 */
	public void addLayerByWms(String title, String layerName, String wmsRequest, boolean isBase, boolean displayInLayerSwitcher, boolean isNcWMS, String UUID) {
		gg.addLayerByWms(title, layerName, wmsRequest, isBase, displayInLayerSwitcher, UUID);
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
