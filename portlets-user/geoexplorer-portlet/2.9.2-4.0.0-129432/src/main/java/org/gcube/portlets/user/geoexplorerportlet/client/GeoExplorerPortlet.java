package org.gcube.portlets.user.geoexplorerportlet.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.gcubegisviewer.client.GCubeGisViewerPanel;
import org.gcube.portlets.user.geoexplorer.client.GeoExplorer;
import org.gcube.portlets.user.geoexplorer.client.GeoExplorerHandler;
import org.gcube.portlets.user.geoexplorer.client.GeoExplorerParameters;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorerportlet.client.resources.Images;
import org.gcube.portlets.user.geoexplorerportlet.client.resources.Resources;
import org.gcube.portlets.user.gisviewer.client.DataPanelOpenListener;
import org.gcube.portlets.user.gisviewer.client.GisViewerParameters;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * The Class GeoExplorerPortlet.
 * @author Ceras
 * updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 25, 2015
 */
public class GeoExplorerPortlet implements EntryPoint{

	protected static final String GEO_EXPLORER_CONTENT = "geoExplorer-content";
	public static Resources resources = GWT.create(Resources.class);

	public static String defaultGroup= //"WMS_biocod2011_11_22_16_20_19_647"; // in geoserver.d4science-ii
	"group45be7606f-4add-4636-ad14-f353163d0bd3"; // in geoserver-dev.d4science-ii
	// private GisViewerPanel gisViewerPanel; // for normal mode
	private GCubeGisViewerPanel gisViewerPanel; // for gcube mode

	private GeoExplorer geoExplorer;
	private ContentPanel gisViewerContentPanel;
	private LayoutContainer geoExplorerContentPanel;
	private ToolBar toolbar;
	private org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem trueMarbleLayer;
	private LayoutContainer resizeBar = new LayoutContainer();
	public static final String defaultWorkspace = "aquamaps";

	private Button openBaseLayerButton;
	private Button openDefaultLayersButton;
	protected VerticalPanel mainPanel;
	private List<LayerItem> defaultLayersItem = null;
	private List<LayerItem> baseLayersItem = null;

	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {

		GeoExplorerParameters geoExplorerParameters = new GeoExplorerParameters();
		geoExplorerParameters.setDisplaySelectorsPanel(false);
		geoExplorerParameters.setReferredWorkspace(defaultWorkspace);
		geoExplorerParameters.setGeoExplorerHandler(new GeoExplorerHandler(){
			@Override
			public void layerItemsSelected(List<LayerItem> layerItems) {
				openGisViewerWithLayers(layerItems, false);
			}

			@Override
			public void loadDefaultLayers(List<LayerItem> defLayers) {
				defaultLayersItem = defLayers;
				activeButtonAddDefaultLayer();
			}

			@Override
			public void loadBaseLayers(List<LayerItem> baseLayers) {
				baseLayersItem = baseLayers;
				activeButtonAddBaseLayers();
			}

		});

		GisViewerParameters gisViewerParameters = new GisViewerParameters();
		gisViewerParameters.setProjection("");

		gisViewerParameters.setOpenDataPanelAtStart(false);

		gisViewerParameters.setDataPanelOpenListener(new DataPanelOpenListener() {
			@Override
			public void dataPanelOpen(boolean isOpen, int panelHeight) {
				//CICCIO
//				int gisViewerHeight = gisViewerContentPanel.getHeight()
//						+ (isOpen ? panelHeight-24 : -panelHeight+24);
//				gisViewerContentPanel.setHeight(gisViewerHeight);

				//CHANGED BY FRANCESCO M.
				GWT.log("Data panel Open Listener : "+isOpen);
				int dataPanelHeight = isOpen ? panelHeight+24 : 24;
				int gisViewerHeight;

				if(gisViewerContentPanel.getHeight()>Constants.gisViewerHeight){
					gisViewerHeight = gisViewerContentPanel.getHeight();
				}
				else {
					gisViewerHeight = Constants.gisViewerHeight+dataPanelHeight;
				}

				gisViewerContentPanel.setHeight(gisViewerHeight);

			}
		});

		trueMarbleLayer = createTrueMarbleLayer();
		toolbar = createToolbar();
		geoExplorer = new GeoExplorer(geoExplorerParameters);
		// gisViewerPanel = new GisViewerPanel(gisViewerParameters);  // for normal mode
		gisViewerPanel = new GCubeGisViewerPanel(gisViewerParameters);  // for gcube mode

		mainPanel = createLayout();

		// GEOEXPLORER LAYOUT CONTAINER
		RootPanel.get(GEO_EXPLORER_CONTENT).add(mainPanel);
		gisViewerContentPanel.collapse();
		gisViewerContentPanel.setVisible(true);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				updateSize();
			}
		});

		updateSize();
	}

	/**
	 * Active button add default layer.
	 */
	protected void activeButtonAddDefaultLayer() {

		if(defaultLayersItem!=null && !defaultLayersItem.isEmpty()) {
			openDefaultLayersButton.setVisible(true);
		}

	}

	/**
	 * Active button add base layers.
	 */
	protected void activeButtonAddBaseLayers() {

		if(baseLayersItem!=null && !baseLayersItem.isEmpty()) {
			openBaseLayerButton.setVisible(true);
		}

	}

	/**
	 * Update size.
	 */
	private void updateSize() {
		RootPanel rootPanel = RootPanel.get(GEO_EXPLORER_CONTENT);

		int leftBorder = rootPanel.getAbsoluteLeft();
		int w = Window.getClientWidth() - 2* leftBorder;
		gisViewerContentPanel.setWidth(w-20);
		toolbar.setWidth(w-20);
		geoExplorerContentPanel.setWidth(w-20);
	}

	/**
	 * Creates the true marble layer.
	 *
	 * @return the org.gcube.portlets.user.gisviewer.client.commons.beans. layer item
	 */
	private org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem createTrueMarbleLayer() {
		org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem l = new org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem();
		l.setLayer("aquamaps:TrueMarble.16km.2700x1350");
		l.setName("TrueMarble.16km.2700x1350");
		l.setTitle("True Marble");
		l.setGeoserverUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver");
		return l;
	}

	/**
	 * Creates the toolbar.
	 *
	 * @return the tool bar
	 */
	private ToolBar createToolbar() {
		ToolBar tb = new ToolBar();

		ImageResource logoGeoExplorer = GeoExplorerPortlet.resources.logoGeoExplorer();

		Button openSelectedLayersButton = new Button("Add Selected Layers", Images.iconOpen(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				openSelectedLayers();
			}
		});

		 openDefaultLayersButton = new Button("Add Default Layers", Images.iconLayers(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addDefaultLayers();
			}
		});

		openBaseLayerButton = new Button("Add Base Layers", Images.iconTrueMarble(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {

				addBaseLayers();
//				addTrueMarble();
			}
		});

		Button addWmsLayerButton = new Button("Add External WMS Layer", Images.iconAddWms(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
//				showOpenWmsDialog();

				final DialogBox box = new DialogBox(true);
				box.setText("Add External WMS Layer");
				box.getElement().getStyle().setZIndex(10000);

				WmsForm form = new WmsForm() {

					@Override
					public void closeHandler() {
						box.hide();
					}

					@Override
					public void subtmitHandler(String title, String name, String wmsRequest) {
						gisViewerPanel.addLayerByWms(title, name, wmsRequest, false, false, null);
						showGisViewer();
						box.hide();
					}
				};
				box.setWidget(form);
				box.center();
			}
		});

		Button removeAllLayersButton = new Button("Remove All Layers", Images.iconRemove(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				gisViewerPanel.removeAllLayers();
			}
		});

		openSelectedLayersButton.setScale(ButtonScale.LARGE);
		openDefaultLayersButton.setScale(ButtonScale.LARGE);

		openDefaultLayersButton.setVisible(false); //DEFAULT IS NOT VISIBLE BY DEFAULT
		openBaseLayerButton.setVisible(false); //BASE LAYERS IS NOT VISIBLE BY DEFAULT

		openBaseLayerButton.setScale(ButtonScale.LARGE);
		removeAllLayersButton.setScale(ButtonScale.LARGE);
		addWmsLayerButton.setScale(ButtonScale.LARGE);


		Button settingsRefreshButton = new Button("Reload settings", Images.settingsRefresh(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				geoExplorer.hardRefresh(true);
			}
		});

		settingsRefreshButton.setScale(ButtonScale.LARGE);
		settingsRefreshButton.setTitle("Reload settings and data (hard refresh)");

		LayoutContainer lc = new LayoutContainer();
		Image logo = new Image(logoGeoExplorer);
		logo.getElement().getStyle().setMargin(5.0, Unit.PX);
//		logo.getElement().getStyle().setMarginLeft(5, Unit.PX);
//		logo.getElement().getStyle().setMarginRight(5, Unit.PX);
		lc.add(logo);
		tb.add(lc);
		tb.add(new SeparatorToolItem());
		tb.add(new SeparatorToolItem());
		tb.add(openSelectedLayersButton);

		tb.add(openDefaultLayersButton);
		tb.add(openBaseLayerButton);
		tb.add(removeAllLayersButton);
		tb.add(addWmsLayerButton);

		tb.add(new SeparatorToolItem());
		tb.add(new SeparatorToolItem());
		tb.add(settingsRefreshButton);

		return tb;
	}

	/**
	 * Open selected layers.
	 */
	private void openSelectedLayers() {
		geoExplorer.fireLayerItemsSelected();
		geoExplorer.deselectAllLayers();
	}

	/**
	 * Adds the true marble.
	 */
	protected void addTrueMarble() {

		gisViewerPanel.addLayerByLayerItemToTop(trueMarbleLayer);
		showGisViewer();

	}

	/**
	 * Added by Francesco M.
	 */
	protected void addBaseLayers() {
		openGisViewerWithLayers(baseLayersItem, true);
		showGisViewer();
	}

	/**
	 * Adds the default layers.
	 */
	protected void addDefaultLayers() {
		GWT.log("defaultLayersItem: "+defaultLayersItem);
		openGisViewerWithLayers(defaultLayersItem, true);
	}

	/**
	 * Open gis viewer with layers.
	 *
	 * @param layerItems the layer items
	 * @param onTop the on top
	 */
	private void openGisViewerWithLayers(List<LayerItem> layerItems, boolean onTop) {
		List<LayerItem> listGeoserverWrongLayer = new ArrayList<LayerItem>();

		//ADDED BY FRANCESCO
		if (layerItems!=null && layerItems.size()>0) {

			for (LayerItem layerItem : layerItems) {

				if(layerItem.getWmsServiceUrl()==null || layerItem.getWmsServiceUrl().isEmpty()){
					listGeoserverWrongLayer.add(layerItem);
				}else{

					GWT.log("Adding layer item to map: "+layerItem);
					/*ArrayList<String> listStyles = new ArrayList<String>();

					//EXTRACT THIS FUNCTION
					if(layerItem.getStyles()!=null){
//						listStyles = new ArrayList<String>(layerItem.getStyles());
						listStyles.addAll(layerItem.getStyles());
					}*/
					//TODO IMPLEMENT THIS CASE
	//				GisViewerPanel.LayerType.RASTER
//					gisViewerPanel.addLayerByWms(GisViewerPanel.LayerType.FEATURETYPE, layerItem.getTitle(), layerItem.getName(), layerItem.getWmsServiceUrl(), !layerItem.isInternalLayer(), false, false, listStyles, onTop);
//					gisViewerPanel.addLayerByWms(GisViewerPanel.LayerType.FEATURE_TYPE, layerItem.getTitle(), layerItem.getName(), layerItem.getWmsServiceUrl(), !layerItem.isInternalLayer(), false, false, listStyles, layerItem.getWMSRequest(), onTop, layerItem.getMapWmsNotStandardParameters(), layerItem.isNcWMS(), layerItem.getUuid());

//					gisViewerPanel.addLayerByWmsRequest(layerItem.getTitle(), layerItem.getName(), layerItem.getWmsServiceUrl(), false, false, false, layerItem.getWMSRequest(), onTop, layerItem.getUuid());

					gisViewerPanel.addLayerByWmsRequest(layerItem.getTitle(), layerItem.getName(), layerItem.getWMSRequest(), false, false, layerItem.getUuid(), onTop);
				}
			}

			showGisViewer();

			if(listGeoserverWrongLayer.size()>0){

				String msg = listGeoserverWrongLayer.size()>1?"Those layers ":"This layer ";
				for (LayerItem layerItem : listGeoserverWrongLayer) {
					msg+= layerItem.getTitle() +", ";
				}

				msg+= "can not be displayed with GisViewer, the geoserver base url is not available";
				MessageBox.alert("Alert", msg, null);
			}
		}

	}


	/**
	 * Transform layer items for gis viewer.
	 *
	 * @param layerItems the layer items
	 * @return the list
	 */
	@SuppressWarnings("unused")
	private List<org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem> transformLayerItemsForGisViewer(List<LayerItem> layerItems) {

		List<org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem> gVLayerItems =
				new ArrayList<org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem>();

		for (LayerItem layerItem : layerItems) {
			org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem l =
					new org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem();
			l.setLayer(layerItem.getLayer());
			l.setName(layerItem.getName());
			l.setTitle(layerItem.getTitle());
			l.setGeoserverUrl(layerItem.getGeoserverUrl());

			gVLayerItems.add(l);
		}

		return gVLayerItems;
	}

	/**
	 * Show gis viewer.
	 */
	private void showGisViewer() {
		if (gisViewerContentPanel!=null) {
			gisViewerContentPanel.expand();
			Window.scrollTo(0, 0);
		}
	}

	/**
	 * Creates the layout.
	 *
	 * @return the vertical panel
	 */
	private VerticalPanel createLayout() {
		VerticalPanel vp = new VerticalPanel();
		vp.setStyleAttribute("background-color", "transparent");
		vp.setTableWidth("100%");

		gisViewerContentPanel = new ContentPanel() {

			@Override
			protected void onExpand() {
				super.onExpand();
				if (gisViewerPanel!=null) {
					gisViewerPanel.updateOpenLayersSize();
				}
				resizeBar.show();
			}

			@Override
			protected void onCollapse() {
				super.onCollapse();
				resizeBar.hide();
			}
		};
		gisViewerContentPanel.setLayout(new FitLayout());
		gisViewerContentPanel.setHeading("GisViewer");

		gisViewerContentPanel.setCollapsible(true);
		gisViewerContentPanel.add(gisViewerPanel);
		gisViewerContentPanel.setVisible(false);

		geoExplorerContentPanel = geoExplorer.getGeoExplorerLayoutContainer();

		gisViewerContentPanel.setHeight(Constants.gisViewerHeight);
		geoExplorerContentPanel.setHeight(Constants.geoExplorerHeight);

		@SuppressWarnings("unused")
		Resizable r = new Resizable(gisViewerContentPanel, "s");

		resizeBar.addStyleName("resizeBar");
		resizeBar.hide();

		vp.add(gisViewerContentPanel);
		vp.add(resizeBar);
		vp.add(toolbar);
		vp.add(geoExplorerContentPanel);
		vp.add(new Html("<div style='height:30px'></div>"));

		return vp;
	}
}
