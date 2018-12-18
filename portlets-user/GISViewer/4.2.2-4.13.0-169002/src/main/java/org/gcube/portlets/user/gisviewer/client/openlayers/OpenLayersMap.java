package org.gcube.portlets.user.gisviewer.client.openlayers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.GisViewerPanel;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.MapViewInfo;
import org.gcube.portlets.user.gisviewer.client.commons.beans.TransectParameters;
import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.ZIndexBase;
import org.gwtopenmaps.openlayers.client.control.Control;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeature.FeatureAddedListener;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.MouseDefaults;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.MousePositionOptions;
import org.gwtopenmaps.openlayers.client.control.MousePositionOutput;
import org.gwtopenmaps.openlayers.client.control.NavToolbar;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.control.ZoomBox;
import org.gwtopenmaps.openlayers.client.event.MapClickListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandler;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandlerOptions;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.util.JObjectArray;
import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class OpenLayersMap.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 10, 2016
 */
public class OpenLayersMap {

	/**
	 *
	 */
	public static final String Z_AXIS_NCWMS_KEY_PARAMETER = "ELEVATION";
	public static final String Z_AXIS_WMS_KEY_PARAMETER = "ELEVATION";
	protected static final int MAX_ZINDEX = 280;
	private List<LayerItem> layerItems = new ArrayList<LayerItem>();
	private Map map;

	private MapWidget mapWidget;
	private HashMap<String, String> mapping = new HashMap<String, String>();
	private HashMap<LayerItem, Layer> mappingLayerItemsToLayers = new HashMap<LayerItem, Layer>();
	private HashMap<Layer, LayerItem> mappingLayersToLayerItems = new HashMap<Layer, LayerItem>();
	private NavToolbar mouseToolBar;
	private MouseDefaults mouseDefaults;
	//private OverviewMap overViewMap;
	//private MouseClickOnMap mouseClickOnMap;
//	private ToolBarPanelOld toolBarPanel;
	private int numZoomLevels = 0;
	//private String projection = "";
	private Vector vectorLayer;
	private ZoomBox zoomBox;
	private boolean clickData = false;
	private String tableTransect = "";
	private String fieldTransect = "";
	private VectorFeature prevVf;
	public static Bounds defaultBounds = new Bounds(-180, -90, 180, 90);// -180, -90, 180, 90 //-20037508.34,-20037508.34,20037508.34,20037508.34);// TODO -180, -90, 180, 90);
	private List<Layer> baseLayers = new ArrayList<Layer>();
	//private static String defaultProjection = "EPSG:900913";//"EPSG:4326";
	private DrawFeature drawBoxControl;
	private DrawFeature drawLineControl;
	private OpenLayersHandler handler=null;
	private int maxZindex = 0;
	private ToolBarPanel toolBarPanel;
	private List<GisViewerBaseLayerInterface> baseLayersIntefaces;
	private TransectParameters transect;

	//ADDED BY FRANCESCO M. TO FIX # support 832
	/**
	 * The Class ZIndexBaseGisViewer.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 10, 2016
	 */
	public class ZIndexBaseGisViewer extends ZIndexBase{

		/**
		 * Instantiates a new z index base gis viewer.
		 *
		 * @param jsObject the js object
		 */
		protected ZIndexBaseGisViewer(JSObject jsObject) {
			super(jsObject);
		}

		//Ideally this is initialized from OpenLayers, but it seems that
		// would require instantiating OpenLayers.Map to copy default Z_INDEX_BASE
		/**
		 * Instantiates a new z index base gis viewer.
		 */
		public ZIndexBaseGisViewer(){
			this(JSObject.createJSObject());
			setForBaseLayers(20);
			setForOverlays(80);
			setForFeatures(140);
			setForPopups(200);
			setForControls(240);
		}

		/**
		 * Instantiates a new z index base gis viewer.
		 *
		 * @param baseLayerZIndexBase the base layer z index base
		 * @param overlayZIndexBase the overlay z index base
		 * @param featureZIndexBase the feature z index base
		 * @param popupZIndexBase the popup z index base
		 * @param controlZIndexBase the control z index base
		 */
		public ZIndexBaseGisViewer(int baseLayerZIndexBase,
				int overlayZIndexBase,
				int featureZIndexBase,
				int popupZIndexBase,
				int controlZIndexBase){
			this(JSObject.createJSObject());
			setForBaseLayers(baseLayerZIndexBase);
			setForOverlays(overlayZIndexBase);
			setForFeatures(featureZIndexBase);
			setForPopups(popupZIndexBase);
			setForControls(controlZIndexBase);
		}
	}
	// constructor
	/**
	 * Instantiates a new open layers map.
	 *
	 * @param w the w
	 * @param h the h
	 * @param numZoomLevels the num zoom levels
	 * @param title the title
	 * @param handler the handler
	 */
	public OpenLayersMap(String w, String h, int numZoomLevels, // projection, transectUrl removed
			String title, final OpenLayersHandler handler) {

		this.handler = handler;
		this.numZoomLevels = numZoomLevels;
		//this.projection = defaultBounds.;
		// set map options
		MapOptions mapOptions = new MapOptions();
		mapOptions.setControls(new JObjectArray(new JSObject[] {}));
		mapOptions.setNumZoomLevels(numZoomLevels);
		mapOptions.setProjection(Constants.defaultProjection);
		mapOptions.setZIndexBase(new ZIndexBaseGisViewer());

		// let's create map widget and map objects
		mapWidget = new MapWidget(w, h, mapOptions); // map widget
		map = mapWidget.getMap(); // map js-mapped object
		mapWidget.setTitle(title); // set title
		map.setMaxExtent(defaultBounds);
		//map.getJSObject().setProperty("allOverlays", true);
		// set click listener on the map
		map.addMapClickListener(new MapClickListener() {
			public void onClick(MapClickEvent mapClickEvent) {
				if (clickData) {
					double lat = mapClickEvent.getLonLat().lat();
					double lon = mapClickEvent.getLonLat().lon();
					addPoint(lon, lat);

//					int x = mapClickEvent.getJSObject().getProperty("xy")
//							.getPropertyAsInt("x");
//					int y = mapClickEvent.getJSObject().getProperty("xy")
//							.getPropertyAsInt("y");

					int w = (int) map.getSize().getWidth();
					int h = (int) map.getSize().getHeight();
//					handler.clickOnMap(x, y, w, h);

					// ratio -  mapPixelWeight : bboxWeight = 10px : geoRectangleWidth
					// where 10px is the pixel diameter dimension of the clicked point

					double bboxWidth = Math.abs(getExtent().getLowerLeftX() - getExtent().getUpperRightX());
					double geoWidth = bboxWidth / w * 10 / 2;
					double x1 = Math.min(lon+geoWidth, lon-geoWidth);
					double x2 = Math.max(lon+geoWidth, lon-geoWidth);
					double y1 = Math.min(lat+geoWidth, lat-geoWidth);
					double y2 = Math.max(lat+geoWidth, lat-geoWidth);
					Constants.log("("+x1+","+y1+")("+x2+","+y2+")");
					handler.selectBox(x1, y1, x2, y2);
				}
			}
		});

		// set the other controls
		mouseDefaults = new MouseDefaults();
		mouseToolBar = new NavToolbar();
		PanZoomBar pamZoomBar = new PanZoomBar();
		zoomBox = new ZoomBox();
		Element e = mouseDefaults.getJSObject().getPropertyAsDomElement("position");
		e.setPropertyDouble("x", -300);
		mouseDefaults.getJSObject().setProperty("position", e);

		VectorOptions vectoroptions = new VectorOptions();
		vectoroptions.setDisplayInLayerSwitcher(false);
		vectoroptions.setDisplayOutsideMaxExtent(true);
		vectoroptions.setProjection(Constants.defaultProjection);
		vectorLayer = new Vector("transet", vectoroptions);
		map.addLayer(vectorLayer);
		vectorLayer.setZIndex(MAX_ZINDEX);

		String wmsURI = GisViewerPanel.resources.baseLayer().getText();
		String[] params = wmsURI.split("\\?");

		if(params.length<2){
			GWT.log("ERROR: No base layer found!");
		}

		String baseLayerURI = params[0];
		String[] parameter = params[1].split("&");
		String baseLayerName = "";

		for (String string : parameter) {
			if(string.startsWith("layers=")){
				baseLayerName = string.substring(7);
				break;
			}
		}

		if(baseLayerURI==null || baseLayerURI.isEmpty() || baseLayerName.isEmpty()){
			GWT.log("ERROR: No base layer found!");
		}

		addBaseLayerToOpenLayerMap("True Marble", baseLayerName, baseLayerURI);
		addControl(mouseDefaults);
		addControl(pamZoomBar);
		addControl(getMousePosition());
		addControl(zoomBox);
		addControl(new LayerSwitcher());
		addTransectControl();
		addBoxControl();
	}

	/**
	 * Adds the transect control.
	 */
	private void addTransectControl() {
		FeatureAddedListener listener = new FeatureAddedListener() {
			public void onFeatureAdded(VectorFeature vf) {

				GWT.log("Transect "+transect);
				if (transect.getUrl()==null)
					return;
				org.gwtopenmaps.openlayers.client.geometry.LineString line = org.gwtopenmaps.openlayers.client.geometry.LineString
						.narrowToLineString(vf.getGeometry().getJSObject());

				Double x1;
				Double y1;
				Double x2;
				Double y2;

				try {
					double[][] coordinate = line.getCoordinateArray();

					x1 = coordinate[0][0];
					y1 = coordinate[0][1];
					x2 = coordinate[1][0];
					y2 = coordinate[1][1];
					String url = transect.getUrl() + "?"
							+ "x1=" + x1 + "&" + "y1=" + y1 + "&" + "x2=" + x2
							+ "&" + "y2=" + y2 + "&SRID=4326"
							+ "&maxelements=1000" + "&minimumgap=-1"
							+ "&tablename=" + getTableTransect()
							+ "&biodiversityfield=" + getFieldTransect()+""
							+ "&scope="+transect.getScope();

					Window.open(url, "_blank", "");

				} catch (Exception e) {
				}
				vf.destroy();
			}

		};

		DrawFeatureOptions drawoptions = new DrawFeatureOptions();
		drawoptions.onFeatureAdded(listener);
		LineHandler handler = new LineHandler();
		drawLineControl = new DrawFeature(vectorLayer, handler, drawoptions);
		addControl(drawLineControl);
	}


	/**
	 * Adds the box control.
	 */
	private void addBoxControl() {
		FeatureAddedListener listener = new FeatureAddedListener() {
			@Override
			public void onFeatureAdded(VectorFeature vf) {
				if (prevVf!=null)
					vectorLayer.removeFeature(prevVf);
				prevVf = vf;
				vectorLayer.setZIndex(MAX_ZINDEX);

				Geometry geo = vf.getGeometry();
                Bounds bounds = geo.getBounds();
                double x1 = bounds.getLowerLeftX();
                double y1 = bounds.getLowerLeftY();
                double x2 = bounds.getUpperRightX();
                double y2 = bounds.getUpperRightY();
				handler.selectBox(x1, y1, x2, y2);
			}
		};

		/*
		DrawFeatureOptions options = new DrawFeatureOptions();
		options.onFeatureAdded(listener);
		RegularPolygonHandlerOptions handlerOptions = new RegularPolygonHandlerOptions();
		handlerOptions.setSides(4);
		handlerOptions.setIrregular(true);
		options.setHandlerOptions(handlerOptions);
		drawBoxControl = new DrawFeature(vectorLayer, new RegularPolygonHandler(), options);
		this.map.addControl(drawBoxControl);
		*/

		//CHANGED BY FRANCESCO
		DrawFeatureOptions options = new DrawFeatureOptions();
		options.onFeatureAdded(listener);
		final RegularPolygonHandlerOptions boxHandlerOptions = new RegularPolygonHandlerOptions();
	    boxHandlerOptions.setIrregular(true);
	    boxHandlerOptions.setSides(4);
	    RegularPolygonHandler boxHandler = new RegularPolygonHandler();
	    drawBoxControl = new DrawFeature(vectorLayer, boxHandler, options);
	    ((RegularPolygonHandler) drawBoxControl.getHandler()).setOptions(boxHandlerOptions);
		this.map.addControl(drawBoxControl);
	}

	/**
	 * Gets the mouse position.
	 *
	 * @return the mouse position
	 */
	private Control getMousePosition() {
		// set text mouse position (latitude and longitude)
		MousePositionOutput mpOut = new MousePositionOutput() {
			@Override
			public String format(LonLat lonLat, Map map) {
				String out = "<table style=\"background-color:#4B79B4 !important;\">";
				out += "<tr><td>";
				out += "<font color=\"#FFFFFF\" align=\"left\"><b>Latitude</b></font></td>";
				out += "<td style=\"background-color:#D9D9DF !important;\"><font style=\"padding: 5px;\" color=\"#9694A2\"><b>";
				out += Util.getFormattedLonLat(lonLat.lat(), "", "");
				// out += lonLat.lat();
				out += "</b></font></td>";
				out += "<td>";
				out += "<font color=\"#FFFFFF\" align=\"left\"><b>Longitude</b></font></td>";
				out += "<td style=\"background-color:#D9D9DF !important;\"><font style=\"padding: 5px;\" color=\"#9694A2\"><b>";
				out += Util.getFormattedLonLat(lonLat.lon(), "lon", "");
				// out += lonLat.lon();
				out += "</b></font></td></tr>";
				out += "</table>";

				return out;
			}

		};
		MousePositionOptions mpOptions = new MousePositionOptions();
		mpOptions.setFormatOutput(mpOut);
		MousePosition mousePosition = new MousePosition(mpOptions);
		return mousePosition;
	}

	/**
	 * Activate zoom out.
	 */
	public void activateZoomOut() {
		mouseToolBar.getJSObject().setProperty("mode", "zoomout");
	}

	/**
	 * Adds the layer items.
	 *
	 * @param layerItems the layer items
	 * @param toTop the to top
	 */
	public void addLayerItems(List<LayerItem> layerItems, boolean toTop) {
		if (toTop)
			for (LayerItem layerItem : layerItems)
				this.layerItems.add(0, layerItem);
		else
			for (LayerItem layerItem : layerItems)
				this.layerItems.add(layerItem);

		LayerItemsHandler lh = new LayerItemsHandler(layerItems,
				mappingLayerItemsToLayers, mappingLayersToLayerItems);
		List<Layer> ls = lh.getLayers();
		Layer[] lsArray = new Layer[ls.size()];
		ls.toArray(lsArray);
		this.addLayers(lsArray);
	}

	/**
	 * Adds the layers.
	 *
	 * @param layers the layers
	 */
	private void addLayers(Layer[] layers) {

		for (Layer l : layers) {
			l.setIsBaseLayer(false);
			l.getJSObject().setProperty("animationEnabled", true);
			l.getJSObject().setProperty("displayInLayerSwitcher", false);

			if (l.getName().contentEquals("Google Satellite"))
				mapping.put("Google Satellite", l.getId());
			else {
				String layerName = l.getJSObject().getProperty("params").getPropertyAsString("LAYERS");
				mapping.put(layerName, l.getId());
			}
		}

		map.addLayers(layers);
		//vectorLayer.setZIndex(MAX_ZINDEX);

		// OverviewMapOptions overOptions = new OverviewMapOptions();
		// Layer[] ls = new Layer[1];
		// ls[0] = layers[0];
		//
		// overOptions.setLayers(ls);
		//
		// if (Constants.isOverViewMapVisible) {
		// overViewMap = new OverviewMap();
		// addControl(overViewMap);
		// }
	}

	/**
	 * Adds the tool bar.
	 *
	 * @param toolBarPanel the tool bar panel
	 */
	public void addToolBar(ToolBarPanel toolBarPanel) {
		this.toolBarPanel = toolBarPanel;
	}

	/**
	 * Adds the control.
	 *
	 * @param control the control
	 */
	private void addControl(Control control) {
		map.addControl(control);
	}

	/**
	 * Gets the layer items.
	 *
	 * @return the layer items
	 */
	public List<LayerItem> getLayerItems() {
		return layerItems;
	}

	/**
	 * Gets the dock panel.
	 *
	 * @return the dock panel
	 */
	public Widget getDockPanel() {
		// int zoom=map.getZoomForExtent(map.getMaxExtent(), true);
		// let's center the map to somewhere and set zoom level to 5
		LonLat center = new LonLat(0, 0);
		map.setCenter(center, Constants.openLayersMapDefaultZoom);
		// map.setCenter(center, zoom);

		// eventually add the map widget to div:map
		final DockPanel dockPanel = new DockPanel();

		if (toolBarPanel != null)
			dockPanel.add(toolBarPanel, DockPanel.NORTH);

		dockPanel.add(mapWidget, DockPanel.CENTER);

		return dockPanel;

	}

	/**
	 * Gets the content panel.
	 *
	 * @return the content panel
	 */
	public ContentPanel getContentPanel() {
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.add(mapWidget);
		panel.setTopComponent(toolBarPanel);

		// int zoom=map.getZoomForExtent(map.getMaxExtent(), true);
		// let's center the map to somewhere and set zoom level to 5
		LonLat center = new LonLat(0, 0);
		map.setCenter(center, Constants.openLayersMapDefaultZoom);
		// map.setCenter(center, zoom);

		return panel;
	}

	/**
	 * Sets the opacity.
	 *
	 * @param layerItem the layer item
	 * @param opacity the opacity
	 */
	public void setOpacity(LayerItem layerItem, double opacity) {
		Layer layer = mappingLayerItemsToLayers.get(layerItem);
		if (layer != null)
			layer.setOpacity(new Float(opacity).floatValue());
	}

	/**
	 * Sets the z axis value.
	 *
	 * @param layerItem the layer item
	 * @param intValue the int value
	 */
	public void setZAxisValue(LayerItem layerItem, double intValue) {
		Layer layer = mappingLayerItemsToLayers.get(layerItem);
		if (layer != null){
			String keyParam = layerItem.isNcWms()?Z_AXIS_NCWMS_KEY_PARAMETER:Z_AXIS_WMS_KEY_PARAMETER;
			layer.getJSObject().getProperty("params").setProperty(keyParam, intValue);
			layer.redraw();
		}
	}

	/**
	 * Sets the visibility.
	 *
	 * @param layerItem the layer item
	 * @param visible the visible
	 */
	public void setVisibility(LayerItem layerItem, boolean visible) {
		Layer layer = mappingLayerItemsToLayers.get(layerItem);
		if (layer != null) {
			layer.setIsVisible(visible);
			layer.redraw();
		}
	}

	/**
	 * Reproject.
	 *
	 * @param proj the proj
	 */
	public void reproject(String proj) {

		Constants.log("change " + proj);

		MapOptions mapOptions = new MapOptions();
		mapOptions.setControls(new JObjectArray(new JSObject[] {}));
		mapOptions.setNumZoomLevels(this.numZoomLevels);
		mapOptions.setProjection(proj);

		mapWidget.getMap().setOptions(mapOptions);
	}

	/**
	 * Zoom to max extent.
	 */
	public void zoomToMaxExtent() {
		this.map.zoomToMaxExtent();
	}

	/**
	 * Gets the projection.
	 *
	 * @return the projection
	 */
	public String getProjection() {
		return this.map.getProjection();
	}

	/**
	 * Gets the max extent.
	 *
	 * @return the max extent
	 */
	public Bounds getMaxExtent() {
		return this.map.getMaxExtent();
	}

	/**
	 * Gets the extent.
	 *
	 * @return the extent
	 */
	public Bounds getExtent() {
		return this.map.getExtent();
	}

	/**
	 * Activate transect draw.
	 *
	 * @param isActivate the is activate
	 */
	public void activateTransectDraw(boolean isActivate) {
		if (isActivate)
			drawLineControl.activate();
		else
			drawLineControl.deactivate();
	}

	/**
	 * Activate click data.
	 *
	 * @param isActivate the is activate
	 */
	public void activateClickData(boolean isActivate) {
		this.clickData = isActivate;
	}

	/**
	 * Activate draw box control.
	 *
	 * @param isActivate the is activate
	 */
	public void activateDrawBoxControl(boolean isActivate) {
		if (isActivate)
			this.drawBoxControl.activate();
		else
			this.drawBoxControl.deactivate();
		vectorLayer.setZIndex(MAX_ZINDEX);
	}

	/**
	 * Activate zoom in.
	 *
	 * @param isActivate the is activate
	 */
	public void activateZoomIn(boolean isActivate) {
		if (isActivate)
			zoomBox.activate();
		else
			zoomBox.deactivate();
	}

	/**
	 * Activate pan.
	 *
	 * @param isActivate the is activate
	 */
	public void activatePan(boolean isActivate) {
		if (isActivate)
			mouseDefaults.activate();
		else
			mouseDefaults.deactivate();
	}

	// added by gianpaolo coro in 13-07-11 for returning to cache when
	// CQL_FILTERING is finished
	/*
	 * //String name = this.getLayer(layer).getName(); JSObject layerjs =
	 * this.map.getLayerByName(name).getJSObject(); String url =
	 * layerjs.getPropertyAsString("url");
	 * System.out.println("Switching from Previous URL: "+url); url =
	 * url.replace("geoserver/wms", "geoserver/gwc/service/wms");
	 * layerjs.setProperty("url", url); System.out.println("To New URL: "+url);
	 * //this.map.getLayerByName(name).redraw(true); layer.redraw();
	 */

	/**
	 * Removes the cql filter.
	 *
	 * @param layerItem the layer item
	 */
	public void removeCqlFilter(LayerItem layerItem) {
		Layer layer = mappingLayerItemsToLayers.get(layerItem);

		if (layer!=null) {
			JSObject layerJs = layer.getJSObject();
			layerJs.getProperty("params").setProperty("CQL_FILTER", "include");
			layerItem.setCqlFilter("");

			// switch to the default layer's request url
			String url = layerItem.getUrl();
			layerJs.setProperty("url", url);
			layer.redraw();
		}
	}

	/**
	 * Sets the cql filter.
	 *
	 * @param layerItem the layer item
	 * @param filter the filter
	 */
	public void setCqlFilter(LayerItem layerItem, String filter) {
		Layer layer = mappingLayerItemsToLayers.get(layerItem);

		if (layer != null) {
			Constants.log("LAYER : " + layerItem.getName() + " FILTER : " + filter);
			JSObject layerJs = layer.getJSObject();
			layerItem.setCqlFilter(filter);

			if (filter.equals("")) {
				layerJs.getProperty("params").setProperty("CQL_FILTER", "include");
			} else {
				layerJs.getProperty("params").setProperty("CQL_FILTER", filter);

				// added by gianpaolo coro: In the case of filtering don't use
				// the GWC but directly the wms service
				// this will be used till the next refresh
				String wmsUrl = layerItem.getGeoserverWmsUrl();
				layerJs.setProperty("url", wmsUrl);
			}
			layer.redraw();
		}
	}

	/**
	 * Sets the new style.
	 *
	 * @param layerItem the layer item
	 * @param newStyle the new style
	 */
	public void setNewStyle(LayerItem layerItem, String newStyle) {
		Layer layer = mappingLayerItemsToLayers.get(layerItem);
		layer.getJSObject().getProperty("params").setProperty("STYLES", newStyle);
		layer.redraw();
	}

	/**
	 * Sets the table transect.
	 *
	 * @param layerTransect the new table transect
	 */
	public void setTableTransect(String layerTransect) {
		this.tableTransect = layerTransect;
	}

	/**
	 * Gets the table transect.
	 *
	 * @return the table transect
	 */
	public String getTableTransect() {
		return tableTransect;
	}

	/**
	 * Sets the field transect.
	 *
	 * @param fieldTransect the new field transect
	 */
	public void setFieldTransect(String fieldTransect) {
		this.fieldTransect = fieldTransect;
	}

	/**
	 * Gets the field transect.
	 *
	 * @return the field transect
	 */
	public String getFieldTransect() {
		return fieldTransect;
	}

	/**
	 * Change size.
	 *
	 * @param w the w
	 * @param h the h
	 */
	public void changeSize(String w, String h) {
		// if together are positives
		if (Integer.parseInt(w) > 0 && Integer.parseInt(h) > 0) {
			mapWidget.setWidth(w);
			mapWidget.setHeight(h);
			this.updateSize();
		}
		// mapWidget.setSize(w, h);
	}

	/**
	 * Update size.
	 */
	public void updateSize() {
		map.updateSize();
		Constants.info("Update size", "(" + getMapWidth() + ","	+ getMapHeight() + ")");
	}

	/**
	 * Gets the map width.
	 *
	 * @return the map width
	 */
	public int getMapWidth() {
		return (int) this.map.getSize().getWidth();
	}

	/**
	 * Gets the map height.
	 *
	 * @return the map height
	 */
	public int getMapHeight() {
		return (int) this.map.getSize().getHeight();
	}

	/**
	 * Gets the map widget.
	 *
	 * @return the map widget
	 */
	public MapWidget getMapWidget() {
		return mapWidget;
	}


	/**
	 * Adds the point.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void addPoint(double x, double y) {
		// remove any previuos point
		if (this.prevVf != null)
			vectorLayer.removeFeature(prevVf);

		// add new point
		VectorFeature vf = new VectorFeature(new Point(x, y));
		vectorLayer.addFeature(vf);
		vectorLayer.setZIndex(MAX_ZINDEX);

		// store the point
		this.prevVf = vf;
	}

	/**
	 * Gets the map view info.
	 *
	 * @return the map view info
	 */
	public MapViewInfo getMapViewInfo() {
		double lowerLeftX = getExtent().getLowerLeftX() < getMaxExtent()
				.getLowerLeftX() ? getMaxExtent().getLowerLeftX()
				: getExtent().getLowerLeftX();
		double lowerLeftY = getExtent().getLowerLeftY() < getMaxExtent()
				.getLowerLeftY() ? getMaxExtent().getLowerLeftY()
				: getExtent().getLowerLeftY();
		double upperRightX = getExtent().getUpperRightX() > getMaxExtent()
				.getUpperRightX() ? getMaxExtent().getUpperRightX()
				: getExtent().getUpperRightX();
		double upperRightY = getExtent().getUpperRightY() > getMaxExtent()
				.getUpperRightY() ? getMaxExtent().getUpperRightY()
				: getExtent().getUpperRightY();
		return new MapViewInfo(lowerLeftX, lowerLeftY, upperRightX,
				upperRightY, getMapWidth(), getMapHeight());
	}

	/**
	 * Update layers order.
	 */
	public void updateLayersOrder() {
		maxZindex = 0;
		for (Layer layer : baseLayers) {
			if (Integer.parseInt((String)layer.getZIndex())>maxZindex){

				//ADDED BY FRANCESCO M.
				int zindex = Integer.parseInt((String)layer.getZIndex());
				if(zindex>=MAX_ZINDEX){
					zindex-=-50;
					GWT.log("zindex to base layer is >=MAX_ZINDEX, recalculate as: "+zindex);
				}

				maxZindex = zindex;
				layer.setZIndex(maxZindex);
				GWT.log("base layer: "+layer.getName()+" have maxZindex is: "+zindex);
			}
		}

		for (LayerItem layerItem : layerItems) {
			Layer layer = mappingLayerItemsToLayers.get(layerItem);

			//ADDED BY FRANCESCO M.
			int newOrder = maxZindex + (int)layerItem.getOrder();

			if(newOrder>=MAX_ZINDEX){
				newOrder-=layerItems.size()-10;
				GWT.log("newOrder>=MAX_ZINDEX is true, recalculate newOrder: "+newOrder);
			}
			GWT.log("layer: "+layerItem.getName()+" setting ZIndex: "+newOrder);
			layer.setZIndex(newOrder);
		}
	}

	/**
	 * Removes the layer.
	 *
	 * @param layerItem the layer item
	 */
	public void removeLayer(LayerItem layerItem) {
		Layer layer = mappingLayerItemsToLayers.get(layerItem);
		map.removeLayer(layer);
		layerItems.remove(layerItem);
		mappingLayerItemsToLayers.remove(layerItem);
		mappingLayersToLayerItems.remove(layer);
	}


	/**
	 * Reset base layer to open layer map.
	 */
	public void resetBaseLayerToOpenLayerMap(){
		baseLayersIntefaces.clear();
	}


	/**
	 * Adds the base layers to open layer map.
	 *
	 * @param layers the layers
	 */
	public void addBaseLayersToOpenLayerMap(List<GisViewerBaseLayerInterface> layers) {

		if(baseLayersIntefaces==null){
			baseLayersIntefaces = new ArrayList<GisViewerBaseLayerInterface>();
		}

		baseLayersIntefaces.addAll(layers);

		for (GisViewerBaseLayerInterface baseLayerInterface : layers) {
			if(baseLayerInterface.isDisplay())
				addBaseLayerToOpenLayerMap(baseLayerInterface.getTitle(), baseLayerInterface.getName(), baseLayerInterface.getWmsServiceBaseURL());
		}


//		options = new GoogleV3Options();
//		options.setIsBaseLayer(true);
//		options.setDisplayInLayerSwitcher(true);
//		options.setType(GoogleV3MapType.G_TERRAIN_MAP);
//		googleLayer = new GoogleV3("Google Terrain", options);
//		map.addLayer(googleLayer);
//
		// reproject
//		MapOptions mapOptions = new MapOptions();
//		mapOptions.setControls(new JObjectArray(new JSObject[] {}));
//		mapOptions.setNumZoomLevels(this.numZoomLevels);
//		mapOptions.setProjection("EPSG:900913");
//		mapOptions.setMaxExtent(new Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34));
//		mapOptions.setMaxResolutionToAuto();
//		mapWidget.getMap().setOptions(mapOptions);


//		addBaseLayer("Blue Marble", "bluemarble_1", "http://iceds.ge.ucl.ac.uk/cgi-bin/icedswms");
//		addBaseLayerToOpenLayerMap("Global Surface Relief", "etopo1_ice:z", "http://oos.soest.hawaii.edu/erddap/wms/etopo1_ice/request");
//		addBaseLayerToOpenLayerMap("Shaded Relief", "shaded_relief", "http://maps.ngdc.noaa.gov/soap/etopo1/MapServer/WMSServer");
//		addBaseLayerToOpenLayerMap("Dem", "dem", "http://maps.ngdc.noaa.gov/soap/etopo1/MapServer/WMSServer");
//		addBaseLayerToOpenLayerMap("Etopo180", "etopo180:altitude", "http://coastwatch.pfeg.noaa.gov/erddap/wms/erdBAssta5day/request");
//		addBaseLayerToOpenLayerMap("Groundwater Map", "bgr_groundwater_whymap", "http://www.bgr.de/Service/groundwater/whymap/");
//		addBaseLayerToOpenLayerMap("LandMask", "LandMask", "http://oos.soest.hawaii.edu/erddap/wms/etopo1_ice/request");
//		addBaseLayerToOpenLayerMap("Coastlines", "Coastlines", "http://oos.soest.hawaii.edu/erddap/wms/etopo1_ice/request");
////		addBaseLayer("test2", "NWW3_Global_Best:Tdir", "http://oos.soest.hawaii.edu/erddap/wms/NWW3_Global_Best/request");
//		addBaseLayerToOpenLayerMap("GEBCO", "GEBCO_08_Grid", "http://www.gebco.net/data_and_products/web_map_service/mapserv/request");
//		addBaseLayerToOpenLayerMap("test2", "", ""); // do not anything

		//var extent = new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34); //-2200000,-712631,3072800,3840000);

//		map = new OpenLayers.Map( 'map', {
//			allOverlays: true,
//			maxExtent: extent,
//			projection: '900913' //'EPSG:42304'
//		});

	}

	/**
	 * Sets the transect.
	 *
	 * @param transect the new transect
	 */
	public void setTransect(TransectParameters transect) {
		this.transect = transect;
	}

	/**
	 * Deactivate draw box control.
	 */
	public void deactivateDrawBoxControl() {
		this.drawBoxControl.deactivate();
		vectorLayer.setZIndex(MAX_ZINDEX);
	}

	/**
	 * Activate draw box control.
	 */
	public void activateDrawBoxControl() {
		this.drawBoxControl.activate();
		vectorLayer.setZIndex(MAX_ZINDEX);
	}

	/**
	 * Adds the base layer to open layer map.
	 *
	 * @param title the title
	 * @param layerName the layer name
	 * @param geoserverBaseUrl the geoserver base url
	 */
	private void addBaseLayerToOpenLayerMap(String title, String layerName, String geoserverBaseUrl) {
		if (geoserverBaseUrl==null || geoserverBaseUrl.equals("") || layerName==null || layerName.equals(""))
			return;
		LayerItem l = new LayerItem(true);
		l.setTitle(title);
		l.setName(layerName);
		l.setLayer(layerName);
		l.setGeoserverWmsUrl(geoserverBaseUrl);
		l.setBaseLayer(true);
		l.setOpacity(1d);
		addLayerItemByWms(l, true);
	}

	/**
	 * Adds the layer item by wms.
	 *
	 * @param layerItem the layer item
	 * @param displayInLayerSwitcher the display in layer switcher
	 */
	public void addLayerItemByWms(LayerItem layerItem, boolean displayInLayerSwitcher) {
		WMSOptions wmsOptions = new WMSOptions();
		wmsOptions.setIsBaseLayer(layerItem.isBaseLayer());
		wmsOptions.setWrapDateLine(true); // default
//		wmsOptions.setTransitionEffect(TransitionEffect.RESIZE);

		WMSParams wmsParams = new WMSParams();
		wmsParams.setLayers(layerItem.getLayer());
		wmsParams.setIsTransparent(!layerItem.isBaseLayer());
		wmsParams.setFormat("image/png");


		if(layerItem.getWmsNotStandardParams()!=null){ //LAYER CANNOT BE TILED
			for (String key : layerItem.getWmsNotStandardParams().keySet()) {
				String value = layerItem.getWmsNotStandardParams().get(key);
				wmsParams.setParameter(key, value);
//				{"min":-55.33762,"max":2474.535}
//				wmsParams.setParameter(key, "-42.581142,1829.7272");
//				wmsParams.setParameter(key, "-55.33762,2474.535");
				GWT.log("Added non-standard pair to wms request: "+key+"/"+value);
			}
//			wmsOptions.setUntiled();
//			wmsOptions.setSingleTile(true);
//			wmsParams.setParameter("version", "1.3.0");
//			wmsParams.setParameter("CRS", "EPSG:4326");
//			wmsParams.setParameter("SRS", "EPSG:4326");
//			wmsParams.setParameter("BBOX", "-415.283203125,-109.951171875,415.283203125,109.951171875");
		}else //LAYER CAB BE TILED
			wmsOptions.setBuffer(2); // from serviceimpl


		WMS layer = new WMS(layerItem.getTitle(), layerItem.getGeoserverWmsUrl(), wmsParams, wmsOptions);

		layer.setDisplayInLayerSwitcher(displayInLayerSwitcher);
		mappingLayerItemsToLayers.put(layerItem, layer);
		mappingLayersToLayerItems.put(layer, layerItem);
		map.addLayer(layer);

		if (layerItem.isBaseLayer())
			baseLayers.add(layer);
		else
			layerItems.add(layerItem);
	}

	/**
	 * Gets the active base layer.
	 *
	 * @return the active base layer
	 */
	public LayerItem getActiveBaseLayer() {
		for (Layer l : baseLayers)
			if (l.isVisible())
				return mappingLayersToLayerItems.get(l);
		return null;
	}

	/**
	 * Zoom out.
	 */
	public void zoomOut() {
		int z = map.getZoom();
		Constants.log("ACTUAL:" + z);
		map.zoomTo(z-1);
	}

	/**
	 * Removes the data feature.
	 */
	public void removeDataFeature() {
		if (prevVf!=null) {
			vectorLayer.removeFeature(prevVf);
			prevVf = null;
		}
	}

		/**
		 * Gets the map.
		 *
		 * @return the map
		 */
	public Map getMap() {
		return map;
	}

}
