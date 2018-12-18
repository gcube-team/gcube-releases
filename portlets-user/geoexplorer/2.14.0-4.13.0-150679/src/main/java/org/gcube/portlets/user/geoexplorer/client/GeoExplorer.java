/**
 *
 */
package org.gcube.portlets.user.geoexplorer.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.client.layerinfo.LayerInfoPanel;
import org.gcube.portlets.user.geoexplorer.client.resources.Resources;
import org.gcube.portlets.user.geoexplorer.client.rpc.GeoExplorerService;
import org.gcube.portlets.user.geoexplorer.client.rpc.GeoExplorerServiceAsync;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author ceras
 *
 */
public class GeoExplorer implements LayersGridHandler {

	public static Resources resources = GWT.create(Resources.class);
	public static GeoExplorerServiceAsync service = (GeoExplorerServiceAsync) GWT.create(GeoExplorerService.class);

	private GeoExplorerParameters parameters;

	private LayerInfoPanel layerInfoPanel;
	private LayoutContainer selectorsPanel;
	private LayersGrid layersGrid;
	private GeoExplorerHandler geoExplorerHandler;

	private boolean flagInitReady = false;
	private boolean flagGridRendered = false;

	private List<LayerItem> defaultLayers = null;
	private List<LayerItem> baseLayers = null;

	private String scope = null;

	/**
	 *
	 */
	public GeoExplorer() {
		this(new GeoExplorerParameters());
	}

	/**
	 * @param geoExplorerParameters
	 */
	public GeoExplorer(final GeoExplorerParameters parameters) {
		this.parameters = parameters;
		this.geoExplorerHandler = parameters.getGeoExplorerHandler();

		service.initGeoParameters(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable arg0) {
				MessageBox.alert("Error", "Sorry an error occurred when initializing the GeoExplorer, try again later", null);

			}

			@Override
			public void onSuccess(String scp) {
				// set up the workspace list
				flagInitReady = true;
				scope = scp;
				layerInfoPanel.updateScope(scope);
				tryStartGrid(parameters.getLayerUUIDs());
				loadDefaultLayers();
				loadGeoMetadataStylesToShow();
			}

		});

		layersGrid = new LayersGrid("Layers", this, service);

		if (parameters.isDisplaySelectorsPanel())
			selectorsPanel = createSelectorsPanel();

		layerInfoPanel = new LayerInfoPanel(".: Summary layer info");

	}

	/**
	 * @return
	 */
	private LayoutContainer createSelectorsPanel() {

        Button b1 = new Button("<b>Open selected layers</b>", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				fireLayerItemsSelected();
			}
		});

		Button b2 = new Button("Deselect all", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				deselectAllLayers();
			}
		});

		LayoutContainer lc = new LayoutContainer();

        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(5));
        lc.setLayout(layout);

        HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));

        lc.add(b1, layoutData);
		lc.add(b2, layoutData);

		return lc;
	}

	/**
	 *
	 */
	public void deselectAllLayers() {
		if (layersGrid!=null)
			layersGrid.deselectAll();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.client.LayersGridListener#clickLayer(org.gcube.portlets.user.geoexplorer.client.beans.LayerItem)
	 */
	@Override
	public void clickLayer(LayerItem layerItem) {
		layerInfoPanel.showLayerDetails(layerItem);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.client.LayersGridListener#layersGridRendered()
	 */
	@Override
	public void layersGridRendered() {
		flagGridRendered = true;
		tryStartGrid(null);
	};

	private void tryStartGrid(List<String> layerUUIDs) {
		flagGridRendered = true;
		if (flagGridRendered && flagInitReady && layersGrid!=null){
			layersGrid.start(layerUUIDs);
			if(layerUUIDs!=null){
				layersGrid.reload();
				//layerUUIDs.clear(); //in oder to avoid reloading of UUID layer for the next search performed by user
			}
		}
	}

	public Viewport getGeoExplorerViewPort() {
		Viewport viewport = new Viewport();
		createLayout(viewport);
		return viewport;
	}

	public LayoutContainer getGeoExplorerLayoutContainer() {
		LayoutContainer lc = new LayoutContainer();
		createLayout(lc);
		return lc;
	}

	/**
	 * @param lc
	 */
	private void createLayout(LayoutContainer lc) {
		lc.setLayout(new BorderLayout());
		lc.setStyleAttribute("background-color", "transparent");
		//LayoutContainer lc = new LayoutContainer(new BorderLayout());

//		// NORD: LAYERS GRID
//		BorderLayoutData northPanelData = new BorderLayoutData(LayoutRegion.NORTH, 100);
//		northPanelData.setCollapsible(true);
//		northPanelData.setFloatable(true);
//		northPanelData.setHideCollapseTool(true);
//		northPanelData.setSplit(true);
//		northPanelData.setMargins(new Margins(0, 0, 5, 0));
//		ContentPanel northPanel = new ContentPanel();
//		northPanel.setBodyStyle(Constants.panelsBodyStyle);
//		lc.add(northPanel, northPanelData);

		// CENTER PANEL: LAYERS GRID
		BorderLayoutData centerPanelData = new BorderLayoutData(LayoutRegion.CENTER);
		centerPanelData.setMargins(new Margins(0));
		lc.add(layersGrid, centerPanelData);

		// EAST PANEL: LAYER INFO
		BorderLayoutData eastPanelData = new BorderLayoutData(LayoutRegion.EAST, 450, 300, 600);
		eastPanelData.setSplit(true);
		eastPanelData.setCollapsible(true);
		eastPanelData.setFloatable(true);
		eastPanelData.setMargins(new Margins(0, 0, 0, 5));
		lc.add(layerInfoPanel, eastPanelData);

		// SOUTH PANEL: SELECTORS PANEL
		if (geoExplorerHandler!=null && parameters.isDisplaySelectorsPanel()) {
			BorderLayoutData southPanelData = new BorderLayoutData(LayoutRegion.SOUTH, 38);
			southPanelData.setSplit(false);
			southPanelData.setCollapsible(false);
			southPanelData.setFloatable(false);
			southPanelData.setMargins(new Margins(5, 0, 0, 0));
			lc.add(selectorsPanel, southPanelData);
		}
	}

	public Window getGeoExplorerWindow() {
		Window w = new Window();
		w.setHeading(Constants.geoWindowTitle);
		w.setSize(parameters.getWindowWidth(), parameters.getWindowHeight());
		w.setMinWidth(parameters.getWindowMinWidth());
		w.setMinHeight(parameters.getWindowMinHeight());
		w.setMaximizable(true);
		w.setLayout(new FitLayout());
		w.add(this.getGeoExplorerLayoutContainer());

		return w;
	}

	public void fireLayerItemsSelected() {
		if (geoExplorerHandler!=null && layersGrid!=null) {
			List<LayerItem> layerItems = layersGrid.getSelectedItems();
			geoExplorerHandler.layerItemsSelected(layerItems);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.client.LayersGridListener#openLayer(org.gcube.portlets.user.geoexplorer.client.beans.LayerItem)
	 */
	@Override
	public void openLayer(LayerItem l) {
		if (geoExplorerHandler!=null && layersGrid!=null) {
			List<LayerItem> layerItems = new ArrayList<LayerItem>();
			layerItems.add(l);
			geoExplorerHandler.layerItemsSelected(layerItems);
		}
	}

	public String getVersion() {
		return Constants.VERSION;
	}


	public void loadDefaultLayers(){

		service.getDefaultLayers(new AsyncCallback<List<LayerItem>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				GWT.log("An error occurred in RPC getDefaultLayersName, "+caught.getCause());

				geoExplorerHandler.loadDefaultLayers(null);

				loadBaseLayers();

			}

			@Override
			public void onSuccess(List<LayerItem> result) {

				if (result!=null && result.size()>0) {
					defaultLayers = result;
					geoExplorerHandler.loadDefaultLayers(result);
				}
				else
					geoExplorerHandler.loadDefaultLayers(null);

				loadBaseLayers();
			}
		});

	}


	public void loadGeoMetadataStylesToShow(){

		try {

			service.getGeoexplorerStyles(false, new AsyncCallback<List<? extends GeoexplorerMetadataStyleInterface>>() {

				@Override
				public void onFailure(Throwable arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(List<? extends GeoexplorerMetadataStyleInterface> styles) {
					layerInfoPanel.updateMetadataStylesToShow(styles);

				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public void loadBaseLayers(){

		service.getBaseLayers(new AsyncCallback<List<LayerItem>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("An error occurred in RPC getBaseLayers, "+caught.getCause());
				geoExplorerHandler.loadBaseLayers(null);

			}

			@Override
			public void onSuccess(List<LayerItem> result) {

				if (result!=null && result.size()>0) {
					baseLayers = result;
					geoExplorerHandler.loadBaseLayers(result);
				}
				else
					geoExplorerHandler.loadBaseLayers(null);
			}
		});

	}

	public String getScope() {
		return scope;
	}

	public void hardRefresh(boolean confirm){
		layersGrid.hardRefresh(confirm);
	}


	/**
	 * @return the defaultLayers
	 */
	public List<LayerItem> getDefaultLayers() {

		return defaultLayers;
	}


	/**
	 * @return the baseLayers
	 */
	public List<LayerItem> getBaseLayers() {

		return baseLayers;
	}


}
