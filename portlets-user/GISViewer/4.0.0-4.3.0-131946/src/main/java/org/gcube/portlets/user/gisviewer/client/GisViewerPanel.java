package org.gcube.portlets.user.gisviewer.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.gisviewer.client.commons.beans.DataResult;
import org.gcube.portlets.user.gisviewer.client.commons.beans.ExportFormat;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GeoInformationForWMSRequest;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GeoserverItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;
import org.gcube.portlets.user.gisviewer.client.commons.beans.MapViewInfo;
import org.gcube.portlets.user.gisviewer.client.commons.beans.TransectParameters;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WebFeatureTable;
import org.gcube.portlets.user.gisviewer.client.commons.beans.WmsRequest;
import org.gcube.portlets.user.gisviewer.client.commons.beans.ZAxis;
import org.gcube.portlets.user.gisviewer.client.commons.utils.ClickDataInfo;
import org.gcube.portlets.user.gisviewer.client.commons.utils.NewBrowserWindow;
import org.gcube.portlets.user.gisviewer.client.commons.utils.URLMakers;
import org.gcube.portlets.user.gisviewer.client.commons.utils.UriParamUtil;
import org.gcube.portlets.user.gisviewer.client.commons.utils.WmsParameters;
import org.gcube.portlets.user.gisviewer.client.commons.utils.WmsUrlValidator;
import org.gcube.portlets.user.gisviewer.client.datafeature.DataPanelHandler;
import org.gcube.portlets.user.gisviewer.client.datafeature.DataResultPanel;
import org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanel;
import org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler;
import org.gcube.portlets.user.gisviewer.client.openlayers.CqlFilterHandler;
import org.gcube.portlets.user.gisviewer.client.openlayers.CqlFilterPanel;
import org.gcube.portlets.user.gisviewer.client.openlayers.OpenLayersHandler;
import org.gcube.portlets.user.gisviewer.client.openlayers.OpenLayersMap;
import org.gcube.portlets.user.gisviewer.client.openlayers.ToolBarPanel;
import org.gcube.portlets.user.gisviewer.client.openlayers.ToolbarHandler;
import org.gcube.portlets.user.gisviewer.client.resources.Images;
import org.gcube.portlets.user.gisviewer.client.resources.Resources;
import org.gcube.portlets.user.gisviewer.client.util.SizedLabel;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


/**
 * The Class GisViewerPanel.
 * @author Ceras
 * @author updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 26, 2016
 */
public class GisViewerPanel extends LayoutContainer
implements ToolbarHandler, DataPanelHandler, LayersPanelHandler, CqlFilterHandler, OpenLayersHandler {

	private static final String ALERT_HARD_SPATIAL_QUERY = "The spatial query can take long time. Do you want to continue to download it?";
	private GisViewerLayout mainPanel;
	private LayersPanel layersPanel;
	private OpenLayersMap openLayersMap;
	private ToolBarPanel toolBarPanel;
	private GisViewerServiceAsync gisViewerServiceAsync;
	private GisViewerSaveHandler gisViewerSaveHandler;
	private GisViewerParameters parameters;
	private String projection;
	private CqlFilterPanel cqlFilterPanel = new CqlFilterPanel(this);

	private ClickDataInfo lastClickDataInfo = null;

	//ADDED BY FRANCESCO
	/**
	 * The Enum LayerType.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Sep 28, 2015
	 */
	public enum LayerType {RASTER_BASELAYER, FEATURE_TYPE};
	//SHOW INTRO
	private boolean first = true;
	public static Resources resources = GWT.create(Resources.class);

	/**
	 * The Enum Operation.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Sep 28, 2015
	 */
	public enum Operation {OPEN, SAVE};

	/**
	 * Instantiates a new gis viewer panel.
	 */
	public GisViewerPanel() {
		this(new GisViewerParameters());
	}


	/**
	 * Instantiates a new gis viewer panel.
	 *
	 * @param parameters the parameters
	 */
	public GisViewerPanel(GisViewerParameters parameters) {
		super();
		this.parameters = parameters;
		this.gisViewerServiceAsync = GisViewer.service;
		this.gisViewerSaveHandler = parameters.getGisViewerSaveHandler();
		this.projection = parameters.getProjection();

		init();
	}

	/**
	 * Instantiates a new gis viewer panel.
	 *
	 * @param parameters the parameters
	 * @param gisViewerSaveHandler the gis viewer save handler
	 */
	public GisViewerPanel(GisViewerParameters parameters, GisViewerSaveHandler gisViewerSaveHandler) {
		super();
		this.parameters = parameters;
		this.parameters.setGisViewerSaveHandler(gisViewerSaveHandler);
		this.gisViewerServiceAsync = GisViewer.service;
		this.gisViewerSaveHandler = parameters.getGisViewerSaveHandler();
		this.projection = parameters.getProjection();

		init();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		// create an empty layers panel
		layersPanel = new LayersPanel(this);
		createOpenLayersMap();

		// initialize the layout
		this.setLayout(new FitLayout());
		mainPanel = new GisViewerLayout(parameters.isOpenDataPanelAtStart(), this) {
			/* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.widget.LayoutContainer#onRender(com.google.gwt.user.client.Element, int)
			 */
			@Override
			protected void onRender(Element parent, int index) {
				super.onRender(parent, index);
				mainPanel.setLayersPanel(layersPanel);
				mainPanel.setOpenLayers(openLayersMap);
			}
		};

		this.add(mainPanel);
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.LayoutContainer#onRender(com.google.gwt.user.client.Element, int)
	 */
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

		// monitoring of the datapanel open/close event
		if (parameters.getDataPanelOpenHandler()!=null) {
			mainPanel.startPanelOpenMonitoring();
		}

		getTransectUrl();
	};

	/**
	 * Wait message.
	 *
	 * @param show the show
	 */
	private void waitMessage(boolean show) {
		if (show) {
			this.mask(Constants.MessageLoadingLayersData);
		}
		else {
			this.unmask();
		}
	}

	/**
	 * Gets the transect url.
	 *
	 * @return the transect url
	 */
	protected void getTransectUrl() {

		GWT.log("Calling getTransectParameters");
		gisViewerServiceAsync.getTransectParameters(new AsyncCallback<TransectParameters>() {

			@Override
			public void onFailure(Throwable caught) {
//				MessageBox.alert("Error retrieving the transect url", caught.getMessage(), null);
				GWT.log("Error transect url fail, "+caught);
			}

			@Override
			public void onSuccess(TransectParameters result) {
				GWT.log("Setting transect url "+result);
				openLayersMap.setTransect(result);
			}
		});
	}

	/**
	 * Adds the layer by wms.
	 *
	 * @param layerTitle the layer title
	 * @param layerName the layer name
	 * @param wmsRequest the wms request
	 * @param isBase the is base
	 * @param displayInLayerSwitcher the display in layer switcher
	 * @param UUID the uuid
	 *
	 * {use @link {@link GisViewerPanel#addLayerByWmsRequest(String, String, String, boolean, boolean, String, boolean)}}
	 */
	@Deprecated
	public void addLayerByWms(String layerTitle, String layerName, String wmsRequest, boolean isBase, boolean displayInLayerSwitcher, String UUID) {

		if(wmsRequest.contains("?")){ //IS FULL REQUEST
			GWT.log("WmsRequest string has '?', parsing parameters..");
			WmsUrlValidator  wmsUrlValidator = new WmsUrlValidator(wmsRequest);

			try {
				wmsUrlValidator.parseWmsRequest(false, true);
				HashMap<String, String> mapWmsNotStandard = new HashMap<String, String>(wmsUrlValidator.getMapWmsNotStandardParams().size());
				mapWmsNotStandard.putAll(wmsUrlValidator.getMapWmsNotStandardParams());
				LayerType featureType = isBase?LayerType.RASTER_BASELAYER:LayerType.FEATURE_TYPE;
				addLayerByWms(featureType, layerTitle, layerName, wmsUrlValidator.getBaseWmsServiceUrl(), true, isBase, displayInLayerSwitcher, (ArrayList<String>) wmsUrlValidator.getStylesAsList(), wmsRequest, false, mapWmsNotStandard, mapWmsNotStandard.size()>0, UUID, null);
			} catch (Exception e) {
				e.printStackTrace();
				Window.alert("Sorry an error occurred during layer parsing, check your wms request");
			}

		}else{ //IS ONLY GEOSERVER REQUEST
			GWT.log("WmsRequest string has not '?', no parameter available");
			addLayerByWms(LayerType.FEATURE_TYPE, layerTitle, layerName, wmsRequest, true, false, false, null, "", false, null, false, UUID, null);
		}
	}

	/**
	 * Adds the layer by wms request.
	 *
	 * @param layerTitle the layer title
	 * @param layerName the layer name
	 * @param wmsRequest the wms request
	 * @param isBase the is base
	 * @param displayInLayerSwitcher the display in layer switcher
	 * @param UUID the uuid
	 * @param onTop the on top
	 */
	public void addLayerByWmsRequest(final String layerTitle, final String layerName, final String wmsRequest, final boolean isBase,final boolean displayInLayerSwitcher, final String UUID, final boolean onTop) {

		final LayoutContainer westPanel = (LayoutContainer) layersPanel.getParent();

		if(layersPanel.getLayerItems().size()==0)
			westPanel.mask("Adding..."+layerName, "x-mask-loading");
		else
			layersPanel.mask("Adding..."+layerName, "x-mask-loading");

		final LayerType featureType = isBase?LayerType.RASTER_BASELAYER:LayerType.FEATURE_TYPE;
//		Info.display("Adding Layer", layerName);
		GisViewer.service.parseWmsRequest(wmsRequest, layerName, new AsyncCallback<GeoInformationForWMSRequest>() {

			@Override
			public void onFailure(Throwable caught) {
				if(westPanel.isMasked())
					westPanel.unmask();
				if(layersPanel.isMasked())
					layersPanel.unmask();
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(GeoInformationForWMSRequest result) {
				if(westPanel.isMasked())
					westPanel.unmask();
				if(layersPanel.isMasked())
					layersPanel.unmask();

				GWT.log("Add Layer By WMS: "+result.getMapWMSNoStandard() + ", and isNCWMS? "+result.isNcWMS());
				addLayerByWms(featureType, layerTitle, layerName, result.getBaseWmsServiceHost(), true, isBase, displayInLayerSwitcher, (ArrayList<String>) result.getStyles().getGeoStyles(), result.getWmsRequest(), false, result.getMapWMSNoStandard(), result.isNcWMS(), UUID, result.getZAxis());
			}
		});
	}

	/**
	 * Adds the layers on map.
	 *
	 * @param layerItems the layer items
	 * @param toTop the to top
	 */
	private void addLayersOnMap(List<LayerItem> layerItems, boolean toTop) {
		for (LayerItem layerItem : layerItems) {
			layerItem.setBaseLayer(false);
		}
		openLayersMap.addLayerItems(layerItems, toTop);
		layersPanel.addLayerItems(layerItems, toTop);
		layersPanel.updateLayersOrder();
	}

	/**
	 * Removes the all layers.
	 */
	public void removeAllLayers() {
		if (openLayersMap!=null && openLayersMap.getLayerItems()!=null && openLayersMap.getLayerItems().size()>0) {
			List<LayerItem> layers = new ArrayList<LayerItem>();
			layers.addAll(openLayersMap.getLayerItems());
			for (LayerItem l: layers) {
				removeLayer(l);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#removeLayer(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem)
	 */
	@Override
	public void removeLayer(LayerItem layerItem) {
		openLayersMap.removeLayer(layerItem);
		layersPanel.removeLayer(layerItem);
		updateLayersOrder();
	}

	/**
	 * Creates the open layers map.
	 */
	private void createOpenLayersMap() { // TODO update for pluggable layers
		openLayersMap = new OpenLayersMap(Constants.omWidth + "px",
				Constants.omHeight + "px", Constants.numZoomLevels, "GisViewer ver. "+Constants.VERSION, this);

		toolBarPanel = new ToolBarPanel(this, openLayersMap);
//		toolBarPanel.setStyleName("x-toolbar");
		openLayersMap.addToolBar(toolBarPanel);
	}

	/**
	 * Ger urls for click data.
	 *
	 * @param clickDataInfo the click data info
	 * @return the list
	 */
	private List<String> gerUrlsForClickData(ClickDataInfo clickDataInfo) {
		List<String> urls = new ArrayList<String>();

		if (clickDataInfo==null) {
			return urls;
		}

		// get visible layers
		List<LayerItem> visibleLayerItems = layersPanel.getVisibleLayers();

		if (clickDataInfo.isPoint()) {
			// group layers by geoserver
			List<GeoserverItem> geoserverItems = groupLayersByGeoserver(visibleLayerItems, true);

			// create a list of url request for data (for each geoserver)
			for (GeoserverItem geoserverItem : geoserverItems) {
				String urlRequest = URLMakers.getURL(clickDataInfo, openLayersMap.getProjection(), geoserverItem.getLayerItems());
				urls.add(geoserverItem.getUrl() + urlRequest);
			}
		} else if (clickDataInfo.isBox()) {
			for (LayerItem layerItem : visibleLayerItems){

				//ADDED BY FRANCESCO M.
				int limit = clickDataInfo.getLimit()==Integer.MAX_VALUE?0:1;

				urls.add(URLMakers.getWfsFeatureUrl(layerItem, clickDataInfo.getBbox(), limit, Constants.CSV));
			}
		}

		return urls;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.datafeature.DataPanelHandler#showDataPanel()
	 */
	@Override
	public void showDataPanel() {
		if (lastClickDataInfo==null) {
			return;
		}

		if (lastClickDataInfo.isPoint()) {
			List<String> urls = gerUrlsForClickData(lastClickDataInfo);
			mainPanel.setDataPanelWait(true);

			gisViewerServiceAsync.getDataResult(urls,new AsyncCallback<List<DataResult>>() {
				@Override
				public void onSuccess(List<DataResult> result) {
					DataResultPanel dpanel = new DataResultPanel();
					dpanel.setDataResult(result);
					mainPanel.setDataPanelContent(dpanel);
					mainPanel.setDataPanelWait(false);
				}
				@Override
				public void onFailure(Throwable caught) {
					mainPanel.setDataPanelWait(false);
					mainPanel.setDataPanelMessage("Data Error");
				}
			});
		} else if (lastClickDataInfo.isBox()) {
			String bbox = lastClickDataInfo.getBbox();
			mainPanel.setDataPanelWait(true);

			int maxWFSFeature = lastClickDataInfo.getLimit()==Integer.MAX_VALUE?Constants.MAX_WFS_FEATURES:lastClickDataInfo.getLimit();
			GWT.log("Max WFS Feature is "+maxWFSFeature);
			GWT.log("Zoom level"+openLayersMap.getMap().getZoom());

			gisViewerServiceAsync.getDataResult(layersPanel.getVisibleLayers(), bbox, maxWFSFeature, openLayersMap.getMap().getZoom(), new AsyncCallback<List<WebFeatureTable>>() {
				@Override
				public void onSuccess(List<WebFeatureTable> result) {
					DataResultPanel dpanel = new DataResultPanel();
					dpanel.setDataResultFromWfs(result);
					mainPanel.setDataPanelContent(dpanel);
					mainPanel.setDataPanelWait(false);
				}
				@Override
				public void onFailure(Throwable caught) {
					mainPanel.setDataPanelWait(false);
					mainPanel.setDataPanelMessage("Data Error");
				}
			});
		}
	}

	/**
	 * Show legend popup.
	 *
	 * @param layerItem the layer item
	 * @param left the left
	 * @param top the top
	 */
	private void showLegendPopup(final LayerItem layerItem, final int left, final int top) {
		final Dialog dialog = new Dialog(){
			@Override
			protected void onAfterLayout() {
				super.onAfterLayout();
				if (this.getHeight()>Constants.legendDialogMaxHeight) {
					this.setAutoHeight(false);
					this.setHeight(Constants.legendDialogMaxHeight);
				}
			}
		};

		dialog.setHeading(new SizedLabel("Legend for: "+layerItem.getName(), 12,30).getText());
		dialog.setTitle("Legend for: "+layerItem.getName());
		dialog.setBodyStyleName("pad-text");
		dialog.setScrollMode(Scroll.AUTO);
		dialog.setHideOnButtonClick(true);
		dialog.setBodyStyle("background-color:#FFFFFF");
		dialog.setWidth(Constants.legendDialogWidth);
		dialog.setAutoWidth(true);
		dialog.setAutoHeight(true);
		dialog.setPosition(left, top);
		dialog.setActive(false);

		final HorizontalPanel hpLegend = new HorizontalPanel();
		final Image loading = Images.loading().createImage();
		hpLegend.add(loading);

//		dialog.add(hpLoading);

		String url = layerItem.getGeoserverWmsUrl()
				+ "?service=WMS&"
				+ "version="+WmsUrlValidator.getValueOfParameter(WmsParameters.VERSION, layerItem.getServerWmsRequest())+"&"
				+ "request=GetLegendGraphic&"
				+ "layer="+layerItem.getLayer();

		if(!layerItem.isNcWms()){

					url+= "&format=image/png"
					+ "&STYLE="+(layerItem.getStyle()!=null && !layerItem.getStyle().isEmpty()?layerItem.getStyle():"")
					+ "&LEGEND_OPTIONS=forceRule:True;dx:0.2;dy:0.2;mx:0.2;my:0.2;fontStyle:bold;"
					+ "borderColor:000000;border:true;fontColor:000000;fontSize:14";
		}else{
			String style = layerItem.getStyle()!=null && !layerItem.getStyle().isEmpty()?layerItem.getStyle():"";
			int isNcWmsStyle = style.indexOf("/");
			if(isNcWmsStyle!=-1){
				style = style.substring(isNcWmsStyle+1, style.length());
			}
			url+= "&palette="+style;
			if(layerItem.getWmsNotStandardParams()!=null) {
				for (String key : layerItem.getWmsNotStandardParams().keySet()) { //ADDING COLORSCALERANGE?
					if(key.compareToIgnoreCase(Constants.COLORSCALERANGE)==0){
						url+= "&"+key+"="+layerItem.getWmsNotStandardParams().get(key);
						break;
					}
				}
			}
		}

		GWT.log(url);
		final FlexTable flexTable = new FlexTable();
		flexTable.setStyleName("tablelegend");
		flexTable.setWidget(0, 0, new Label("Style"));
		flexTable.setWidget(0, 1, new Label(layerItem.getStyle()));
		flexTable.setWidget(1, 0, new Label("Legend"));
		final Image legend = new Image(url);
		legend.addLoadHandler(new LoadHandler(){
			@Override
			public void onLoad(LoadEvent event) {
				GWT.log("LoadEvent ");
				hpLegend.remove(loading);
				int h = legend.getHeight();
				if (h>Constants.legendDialogMaxHeight-13) {
					dialog.setAutoHeight(true);
					dialog.setHeight(Constants.legendDialogMaxHeight);
				}
			}
		});

		legend.addErrorHandler(new ErrorHandler() {
			@Override
			public void onError(ErrorEvent event) {
				GWT.log("ErrorEvent ");
				flexTable.setWidget(1, 1, Images.noLegendAvailable().createImage());
			}
		});

		hpLegend.add(legend);
		flexTable.setWidget(1, 1, hpLegend);
		dialog.add(flexTable);
		dialog.show();
	}

	/**
	 * Update open layers size.
	 */
	public void updateOpenLayersSize() {
		if (openLayersMap!=null) {
			openLayersMap.updateSize();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.openlayers.ToolbarHandler#openBrowserMapImage(java.lang.String, boolean)
	 */
	@Override
	public void openBrowserMapImage(ExportFormat outputFormat, boolean isWMS) {
		String servlet = GWT.getModuleBaseURL() + "MapGenerator?";
		Map<String,String> mapParameters = getMapImageRequestParameters(outputFormat, isWMS, true);
		String parameters="";
		for (String param : mapParameters.keySet()) {
			parameters+=param+"="+mapParameters.get(param)+"&";
		}
		String url = servlet+parameters;
		Window.open(url, "_blank", "");
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.openlayers.ToolbarHandler#isSaveSupported()
	 */
	@Override
	public boolean isSaveSupported() {
		return gisViewerSaveHandler!=null;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.openlayers.ToolbarHandler#saveMapImage(java.lang.String, boolean)
	 */
	//Updated by Francesco M.
	@Override
	public void saveMapImage(ExportFormat outputFormat, boolean isWMS) {
		String fileName = "MapImage_"+DateTimeFormat.getFormat("yyyy-MM-dd").format(new Date());
		Map<String, String> parameters = getMapImageRequestParameters(outputFormat, isWMS, false);
		gisViewerSaveHandler.saveMapImage(fileName, outputFormat.getFormat(), parameters, XDOM.getTopZIndex()+Constants.OFFSET_ZINDEX);
	}

	/**
	 * Gets the map image request parameters.
	 *
	 * @param outputFormat the output format
	 * @param isWMS the is wms
	 * @param encodeUrl the encode url
	 * @return the map image request parameters
	 */
	private Map<String, String> getMapImageRequestParameters(ExportFormat outputFormat, boolean isWMS, boolean encodeUrl) {

		boolean firstGs=true, firstLayer=true;

		if (openLayersMap==null || layersPanel==null) {
			return null;
		}

		// get visible layers
		List<LayerItem> visibleLayers = new ArrayList<LayerItem>();
		visibleLayers.add(openLayersMap.getActiveBaseLayer());
		visibleLayers.addAll(layersPanel.getVisibleLayers());
		List<GeoserverItem> geoserverItems = groupLayersByGeoserver(visibleLayers, false);
		String strGeoservers="", strOpacities="", strLayers="", strStyles="", strCqlFilters="", strGeoserverRefs="";
		String strCrs = "", strWmsServerVersions = "", strFormats = "", strSrs="", strWmsNonStandardParameters = "", srtElevation="";

		int gsIndex=0;
		for (GeoserverItem geoserverItem: geoserverItems) {
			String geoserverUrl = encodeUrl? URLMakers.encodeUrl(geoserverItem.getUrl()):geoserverItem.getUrl();
			strGeoservers += (firstGs ? "" : ";") + geoserverUrl;

			for (LayerItem layerItem: geoserverItem.getLayerItems()) {

				String cqlFilter = layerItem.getCqlFilter();
				strLayers += (firstLayer ? "" : ";") + layerItem.getLayer();
				strStyles += (firstLayer ? "" : ";") + (layerItem.getStyle()==null ? "null" : layerItem.getStyle());
				strOpacities += (firstLayer ? "" : ";") + layerItem.getOpacity();
				strCqlFilters += (firstLayer ? "" : ";") + (cqlFilter.equals("") ? "null" : cqlFilter);
				strGeoserverRefs += (firstLayer ? "" : ";") + gsIndex;
//				GWT.log("GeoserverWmsUrl "+layerItem.getServerWmsRequest());
				strCrs += (firstLayer ? "" : ";") +UriParamUtil.getValueOfParameter("crs", layerItem.getServerWmsRequest());
				strWmsServerVersions += (firstLayer ? "" : ";") +UriParamUtil.getValueOfParameter("version", layerItem.getServerWmsRequest());
				strSrs += (firstLayer ? "" : ";") +UriParamUtil.getValueOfParameter("srs", layerItem.getServerWmsRequest());
				strFormats += (firstLayer ? "" : ";") +UriParamUtil.getValueOfParameter("format", layerItem.getServerWmsRequest());

				//TODO ADD WMS NON-STANDARD PARAMETERS
				String nonWmsParams = "";

//				GWT.log("WmsNotStandardParams "+layerItem.getWmsNotStandardParams());
				if(layerItem.getWmsNotStandardParams()!=null && layerItem.getWmsNotStandardParams().size()>0){
					for (String key : layerItem.getWmsNotStandardParams().keySet()) {
						nonWmsParams+=key+"="+layerItem.getWmsNotStandardParams().get(key)+"&";
					}
					nonWmsParams = nonWmsParams.substring(0, nonWmsParams.length()-1); //remove last char
				}
				else {
					nonWmsParams = "null";
				}

				GWT.log("nonWmsParams "+nonWmsParams);
				strWmsNonStandardParameters += (firstLayer ? "" : ";") +nonWmsParams;

				GWT.log("layerItem.getZAxisSelected() "+layerItem.getZAxisSelected());
				srtElevation += (firstLayer ? "" : ";") + (layerItem.getZAxisSelected()==null ? "null" : layerItem.getZAxisSelected());

				if (firstLayer) {
					firstLayer = false;
				}
			}
			gsIndex++;
			if (firstGs) {
				firstGs = false;
			}
		}

		MapViewInfo mapViewInfo = openLayersMap.getMapViewInfo();
		Map<String, String> mapParameters = new HashMap<String, String>();

		//TODO CHANGED OUTPUT FORMAT
		mapParameters.put("outputFormat", outputFormat.getFormat());
		mapParameters.put("bbox", mapViewInfo.getLowerLeftX()
				+ "," + mapViewInfo.getLowerLeftY()
				+ "," + mapViewInfo.getUpperRightX()
				+ "," + mapViewInfo.getUpperRightY());
		mapParameters.put("width", mapViewInfo.getWidth()+"");
		mapParameters.put("height", mapViewInfo.getHeight()+"");
		mapParameters.put("geoservers", strGeoservers);
		mapParameters.put("layers", strLayers);
		mapParameters.put("styles", strStyles);
		mapParameters.put("opacities", strOpacities);
		mapParameters.put("cqlfilters", strCqlFilters);
		mapParameters.put("gsrefs", strGeoserverRefs);
		mapParameters.put("crs", strCrs);
		mapParameters.put("wmsServerVersions", strWmsServerVersions);
		mapParameters.put("srs", strSrs);
		mapParameters.put("formats", strFormats);
		mapParameters.put("wmsNonStandardParameters", strWmsNonStandardParameters);
		mapParameters.put("elevations", srtElevation);

		GWT.log("map parameters return "+mapParameters);

		return mapParameters;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.datafeature.DataPanelHandler#exportData(boolean)
	 */
	@Override
	public void exportData(boolean force) {
		if (force) {
			List<String> urls = gerUrlsForClickData(lastClickDataInfo);
			for (String url : urls) {
				System.out.println("WFS: "+url);
				Window.open(url, "_blank", "");
			}
		} else {
			if (lastClickDataInfo!=null && lastClickDataInfo.isHardQuery()) {
				MessageBox.confirm("Warning"
					, ALERT_HARD_SPATIAL_QUERY
					,new Listener<MessageBoxEvent>(){
						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getText().contentEquals("Yes")) {
								exportData(true);
							}
						}
					}
				);
			}
			else {
				exportData(true);
			}
		}
	}

	/**
	 * Gets the layer image url.
	 *
	 * @param layerItem the layer item
	 * @param format the format
	 * @param isWMS the is wms
	 * @param operation the operation
	 * @return the layer image url
	 */
	private String getLayerImageUrl(final LayerItem layerItem, final ExportFormat format, boolean isWMS, final Operation operation) {
		String url ="";

		NewBrowserWindow window = null;
		if(operation.equals(Operation.OPEN)) {
			window = NewBrowserWindow.open("", "_blank", "");
		}

		if (isWMS) {
			String cqlFilter = layerItem.getCqlFilter();
			MapViewInfo mapViewInfo = openLayersMap.getMapViewInfo();
			String styles="";

			if(layerItem.getStyle()!=null && !layerItem.getStyle().isEmpty()){
//				styles = "&STYLES="+layerItem.getStyle();
				styles = layerItem.getStyle();
			}

			String bbox = mapViewInfo.getLowerLeftX()
					+ "," + mapViewInfo.getLowerLeftY()
					+ "," + mapViewInfo.getUpperRightX()
					+ "," + mapViewInfo.getUpperRightY();

			String wmsVersion = UriParamUtil.getValueOfParameter("version", layerItem.getServerWmsRequest());
			String srs = UriParamUtil.getValueOfParameter("srs", layerItem.getServerWmsRequest());
			String crs = UriParamUtil.getValueOfParameter("crs", layerItem.getServerWmsRequest());

			String cqlFilterParam = cqlFilter.equals("") ? "" : "&CQL_FILTER="+cqlFilter;

			String wmsNonStandardParameters = "";
			if(layerItem.getWmsNotStandardParams()!=null && layerItem.getWmsNotStandardParams().size()>0){
				for (String key : layerItem.getWmsNotStandardParams().keySet()) {
					wmsNonStandardParameters+=key+"="+layerItem.getWmsNotStandardParams().get(key)+"&";
				}
				wmsNonStandardParameters = wmsNonStandardParameters.substring(0, wmsNonStandardParameters.length()-1); //remove last char
			}

			WmsRequest request = new WmsRequest(bbox, mapViewInfo.getWidth()+"", mapViewInfo.getHeight()+"", layerItem.getGeoserverUrl()+url, wmsVersion, layerItem.getLayer(), styles, cqlFilterParam, srs, crs, format.getFormat(), Boolean.toString(format.isSupportTransparency()), wmsNonStandardParameters, layerItem.getZAxisSelected());

			final NewBrowserWindow newWin = window;
			GisViewer.service.parseWmsRequest(request, new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(String wmsRequest) {
					if(operation.equals(Operation.OPEN)){
						if(newWin!=null) {
							newWin.setUrl(wmsRequest);
						}
						else{
							NewBrowserWindow.open(wmsRequest, "_blank", "");
						}
					}else if(operation.equals(Operation.SAVE)){
						gisViewerSaveHandler.saveLayerImage(layerItem.getName(), format.getFormat(), wmsRequest, XDOM.getTopZIndex()+Constants.OFFSET_ZINDEX);
					}

				}
			});

		} else {

			url = "/ows?service=WFS&version=1.0.0&request=GetFeature&typeName="
					+ layerItem.getLayer() + "&outputFormat=" + format;
		}

		String fullImageUrl = layerItem.getGeoserverUrl()+url;
//		System.out.println("returning wms image request: "+fullImageUrl);
//		Window.alert(fullImageUrl);
		return fullImageUrl;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#openBrowserLayerImage(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem, java.lang.String, boolean)
	 */
	@Override
	public void openBrowserLayerImage(LayerItem layerItem, ExportFormat format, boolean isWMS) {
		String url = getLayerImageUrl(layerItem, format, isWMS, Operation.OPEN);
		Constants.log("open layer: " + url);
//		Window.open(url, "_blank", "");
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#saveLayerImage(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem, java.lang.String, boolean)
	 */
	@Override
	public void saveLayerImage(LayerItem layerItem, ExportFormat format, boolean isWMS) {
		String url = getLayerImageUrl(layerItem, format, isWMS, Operation.SAVE);
		Constants.log("open layer: " + url);
//		gisViewerSaveHandler.saveLayerImage(layerItem.getName(), format, url);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#showLegend(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem, int, int)
	 */
	@Override
	public void showLegend(LayerItem layerItem, int left, int top) {
		showLegendPopup(layerItem, left, top);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#setNewStyle(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem, java.lang.String)
	 */
	@Override
	public void applyStyleForLayer(LayerItem layerItem, String style) {
		openLayersMap.setNewStyle(layerItem, style);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#setOpacityLayer(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem, double)
	 */
	@Override
	public void setOpacityLayer(LayerItem layerItem, double value) {
		openLayersMap.setOpacity(layerItem, value);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#showLayer(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem)
	 */
	@Override
	public void showLayer(LayerItem layerItem) {
		openLayersMap.setVisibility(layerItem, true);
		layersPanel.setLayerVisible(layerItem, true);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#hideLayer(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem)
	 */
	@Override
	public void hideLayer(LayerItem layerItem) {
		openLayersMap.setVisibility(layerItem, false);
		layersPanel.setLayerVisible(layerItem, false);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#showFilterQuery(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem, int, int)
	 */
	@Override
	public boolean showFilterQuery(final LayerItem layerItem, int left, int top) {
		cqlFilterPanel.open(layerItem, left, top);
		return true;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#removeFilterQuery(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem)
	 */
	@Override
	public void removeFilterQuery(LayerItem layerItem) {
		removeCqlFilter(layerItem);
	}

	/**
	 * Group layers by geoserver.
	 *
	 * @param layerItems the layer items
	 * @param forData the for data
	 * @return the list
	 */
	private List<GeoserverItem> groupLayersByGeoserver(List<LayerItem> layerItems, boolean forData) {
		List<GeoserverItem> geoserverItems = new ArrayList<GeoserverItem>();
		boolean found;

		//CHANGED BY FRANCESCO M. 17/09/2014
		for (LayerItem layerItem : layerItems) {
			found = false;
			GWT.log("groupLayersByGeoserver: "+layerItem);
			for (GeoserverItem geoserverItem : geoserverItems) {
				if (layerItem.getGeoserverWmsUrl().equals(geoserverItem.getUrl())) {
					geoserverItem.addLayerItem(layerItem);
					found = true;
					break;
				}
			}

			if (!found) {
				GeoserverItem geoserverItem = forData ?
					new GeoserverItem(layerItem.getGeoserverUrl()) :
					new GeoserverItem(layerItem.getGeoserverWmsUrl());
				geoserverItem.addLayerItem(layerItem);
				geoserverItems.add(geoserverItem);
			}

		}

		GWT.log("geoserverItems: "+geoserverItems);
		return geoserverItems;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#updateLayersOrder()
	 */
	@Override
	public void updateLayersOrder() {
		this.openLayersMap.updateLayersOrder();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.openlayers.CqlFilterHandler#setCQLFilter(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem, java.lang.String)
	 */
	@Override
	public void setCQLFilter(LayerItem layerItem, String filter) {
		if (filter==null || filter.equals("")) {
			removeCqlFilter(layerItem);
		}
		else {
			openLayersMap.setCqlFilter(layerItem, filter);
			layersPanel.setCqlTip(layerItem, true);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.openlayers.CqlFilterHandler#removeCqlFilter(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem)
	 */
	@Override
	public void removeCqlFilter(LayerItem layerItem) {
		if (layerItem.getCqlFilter()!=null || layerItem.getCqlFilter().equals("")) {
			openLayersMap.removeCqlFilter(layerItem);
			layersPanel.setCqlTip(layerItem, false);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#activateTransect(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem, java.lang.String, java.lang.String)
	 */
	@Override
	public void activateTransect(LayerItem layerItem, String table, String field) {
		toolBarPanel.setAllUp();
		toolBarPanel.setTransectPanelVisible(layerItem, true);
		layersPanel.setTransectTip(layerItem, true);
		openLayersMap.activateTransectDraw(true);
		openLayersMap.activateZoomIn(false);
		openLayersMap.activatePan(false);
		openLayersMap.activateClickData(false);
		openLayersMap.setTableTransect(table);
		openLayersMap.setFieldTransect(field);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.openlayers.ToolbarHandler#deactivateTransect(org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem)
	 */
	@Override
	public void deactivateTransect(LayerItem layerItem) {
		layersPanel.setTransectTip(layerItem, false);
		toolBarPanel.setTransectPanelVisible(layerItem, false);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.datafeature.DataPanelHandler#panelOpen(boolean)
	 */
	@Override
	public void dataPanelOpen(boolean isOpen, int panelHeight) {
		DataPanelOpenListener handler = parameters.getDataPanelOpenHandler();
		if (handler!=null) {
			handler.dataPanelOpen(isOpen, panelHeight);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.openlayers.OpenLayersHandler#selectBox(java.lang.String)
	 */
	@Override
	public void selectBox(double x1, double y1, double x2, double y2) {
		if (toolBarPanel!=null) {// && toolBarPanel.isClickBox()) {
			// create a new click data information
			lastClickDataInfo = new ClickDataInfo(x1, y1, x2, y2);

			if(toolBarPanel.isPointDataTogglePressed()) {
				lastClickDataInfo.setLimit(1);
			}

			// create a list of url request for data (for each geoserver)
			showDataPanel();
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanelHandler#zAxisUpdated(java.lang.Integer)
	 */
	@Override
	public void zAxisValueChanged(LayerItem layerItem, Double value) {
		openLayersMap.setZAxisValue(layerItem, value.doubleValue());
	}


	/**
	 * Adds the layer by wms.
	 * Extension of addLayerByWms, with input parameter is external layer
	 * @author Francesco Mangiacrapa
	 *
	 * @param layerType the layer type
	 * @param layerTitle the layer title
	 * @param layerName the layer name
	 * @param wmsServiceBaseUrl the wms service base url
	 * @param isExternal the is external
	 * @param isBase the is base
	 * @param displayInLayerSwitcher the display in layer switcher
	 * @param styles the styles
	 * @param serverWmsRequest the server wms request
	 * @param onTop the on top
	 * @param wmsNotStandardParams the wms not standard params
	 * @param isNcWms the is nc wms
	 * @param UUID the uuid
	 */
	private void addLayerByWms(LayerType layerType, String layerTitle, String layerName, String wmsServiceBaseUrl, boolean isExternal, boolean isBase, boolean displayInLayerSwitcher, ArrayList<String> styles, String serverWmsRequest, boolean onTop, HashMap<String, String> wmsNotStandardParams, boolean isNcWms, String UUID, ZAxis zAxis) {

//		GWT.log("Add addLayerByWms 1");
		LayerItem l = new LayerItem();
		l.setBaseLayer(isBase);
		l.setTitle(layerTitle);
		l.setName(layerName);
		l.setLayer(layerName);
		l.setUrl(wmsServiceBaseUrl);
		l.setGeoserverUrl(wmsServiceBaseUrl);
		l.setGeoserverWmsUrl(wmsServiceBaseUrl);
		l.setExternal(isExternal);
		l.setOpacity(1d);
		l.setBuffer(2);
		l.setServerWmsRequest(serverWmsRequest);
		l.setWmsNotStandardParams(wmsNotStandardParams);
		l.setNcWms(isNcWms);
		l.setUUID(UUID);
		l.setZAxis(zAxis);

		switch (layerType) {

		//TODO IMPLEMENT THIS CASE
			case RASTER_BASELAYER:

//				l.setHasLegend(false);
				l.setBaseLayer(true);
				l.setTrasparent(false);
				l.setClickData(false);
				break;

			case FEATURE_TYPE:

				//CASE FEATURE TYPE
				l.setBaseLayer(false);
				l.setClickData(true);
				l.setTrasparent(true);
				break;

		}

		GWT.log("styles "+styles);

		if(styles!=null && styles.size()>0){
			l.setHasLegend(true);
			l.setDefaultStyle(styles.get(0));
			l.setStyle(styles.get(0));
			l.setStyles(styles);
		}else{
			String style = WmsUrlValidator.getValueOfParameter(WmsParameters.STYLES, serverWmsRequest);
			if(style!=null){ //CASE OF STYLE ="";
				//TENTATIVE TO GET LEGEND
				l.setHasLegend(true);
			}
		}

		List<LayerItem> layerItems = new ArrayList<LayerItem>();
		layerItems.add(l);

		openLayersMap.addLayerItemByWms(l, displayInLayerSwitcher);
		layersPanel.addLayerItems(layerItems, onTop);
		layersPanel.updateLayersOrder();
	}

	/**
	 * by Francesco M.
	 *
	 * @param layers the layers
	 */
	public void addBaseLayersToOLM(List<? extends GisViewerBaseLayerInterface> layers){
		openLayersMap.addBaseLayersToOpenLayerMap((List<GisViewerBaseLayerInterface>) layers);
	}

	/**
	 * Show intro.
	 */
	public void showIntro() {
		if (first) {
			final String str = "first_time";
			if (Cookies.getCookie(Constants.COOKIE_NAME) == null) {

				final GisViewerIntro intro = new GisViewerIntro();

				intro.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						if (intro.getCheckNotShow().getValue()) {
							Cookies.setCookie(Constants.COOKIE_NAME, str);
						}

						intro.hide();
					}
				});

				intro.show();
			}
			first = false;
		}
	}

	/**
	 * Gets the open layers map.
	 *
	 * @return the openLayersMap
	 */
	public OpenLayersMap getOpenLayersMap() {
		return openLayersMap;
	}

	/**
	 * Gets the tool bar panel.
	 *
	 * @return the toolBarPanel
	 */
	public ToolBarPanel getToolBarPanel() {

		return toolBarPanel;
	}
}
