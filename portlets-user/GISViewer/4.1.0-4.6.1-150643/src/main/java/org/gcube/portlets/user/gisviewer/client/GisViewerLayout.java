package org.gcube.portlets.user.gisviewer.client;

import org.gcube.portlets.user.gisviewer.client.datafeature.DataPanelHandler;
import org.gcube.portlets.user.gisviewer.client.datafeature.DataResultPanel;
import org.gcube.portlets.user.gisviewer.client.layerspanel.LayersPanel;
import org.gcube.portlets.user.gisviewer.client.openlayers.OpenLayersMap;
import org.gcube.portlets.user.gisviewer.client.resources.Images;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BorderLayoutEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;

public class GisViewerLayout extends LayoutContainer {

	private static final String MSG_DATAPANEL_EMPTY = "No data found in this area.";
	private static final String MSG_NO_DATA_LOADED = "No data loaded.";
	private static final String REFRESH_DATA_BUTTON_TOOLTIP = "Refresh data from last point selected";
	private static final String EXPORT_DATA_BUTTON_TOOLTIP = "Export data into a table";
	private static final String HEADING_DATA = ".: Data <SMALL>(visualization limited to "+Constants.MAX_WFS_FEATURES+" rows)</SMALL>";
	private OpenLayersMap openLayersMap;
	//private BorderLayoutData northPanelData;
	private BorderLayoutData westPanelData;
	private BorderLayoutData centerPanelData;
	private BorderLayoutData southPanelData;
	private ContentPanel westPanel;
	private ContentPanel northPanel;
	private ContentPanel southPanel;
	private ContentPanel centerPanel;
	private boolean isOpenlayersStarted=false;
	private BorderLayout layout;	
	private Button refreshDataButton;
	private Button exportDataButton;
	private DataPanelHandler handler;
	private boolean openDataPanel;
	protected boolean started=false;
	
//	private boolean isOpenDataPanel = false;
	
	public GisViewerLayout(boolean openDataPanel, DataPanelHandler dataPanelHandler) {
		super();
		
		this.openDataPanel = openDataPanel;
//		this.isOpenDataPanel = openDataPanel;
		
		this.handler = dataPanelHandler;
		
		// settaggio layout
		layout = new BorderLayout();

		layout.addListener(Events.Expand, new Listener<BorderLayoutEvent>(){
			@Override
			public void handleEvent(BorderLayoutEvent be) {
				GWT.log("Expand "+started);
				GWT.log("Region "+(be.getRegion()==LayoutRegion.SOUTH));
				if (started && be.getRegion()==LayoutRegion.SOUTH)
					handler.dataPanelOpen(true, southPanel.getHeight());
			}
		});
		
		layout.addListener(Events.BeforeCollapse, new Listener<BorderLayoutEvent>(){
			@Override
			public void handleEvent(BorderLayoutEvent be) {
				GWT.log("BeforeCollapse "+started);
				GWT.log("BeforeCollapse "+(be.getRegion()==LayoutRegion.SOUTH));
				if (started && be.getRegion()==LayoutRegion.SOUTH)
					handler.dataPanelOpen(false, southPanel.getHeight());
			}
		});
		
		setLayout(layout);
		
		/*
		northPanelData = new BorderLayoutData(LayoutRegion.NORTH, 100);
		northPanelData.setCollapsible(true);
		northPanelData.setFloatable(true);
		northPanelData.setHideCollapseTool(true);
		northPanelData.setSplit(false);
		northPanelData.setCollapsible(true);
		northPanelData.setMargins(new Margins(0, 0, 5, 0));
		*/
		
		westPanelData = new BorderLayoutData(LayoutRegion.WEST, 300);
		westPanelData.setMaxSize(300);
		westPanelData.setSplit(true);
		westPanelData.setCollapsible(true);
		westPanelData.setFloatable(true);
		westPanelData.setMargins(new Margins(0, 5, 0, 0));

		// center.setScrollMode(Scroll.AUTOX);
		centerPanelData = new BorderLayoutData(LayoutRegion.CENTER);
		centerPanelData.setMargins(new Margins(0));

		southPanelData = new BorderLayoutData(LayoutRegion.SOUTH, 200);
		southPanelData.setSplit(true);
		southPanelData.setCollapsible(true);
		southPanelData.setFloatable(true);
		southPanelData.setMargins(new Margins(5, 0, 0, 0));

		westPanel = new ContentPanel();
		westPanel.setLayout(new FlowLayout());
		westPanel.setHeading(".: Layers");
		westPanel.setBodyStyle(Constants.panelsBodyStyle);
		//westPanel.setSize(500, 100);
		westPanel.setScrollMode(Scroll.AUTO);
			
//		northPanel = new ContentPanel();
//		northPanel.setBodyStyle(Constants.panelsBodyStyle);
	
		southPanel = new ContentPanel();
//		if (Constants.MODE==Mode.TEST)
		southPanel.setLayout(new FitLayout());
		southPanel.setHeading(HEADING_DATA);
		southPanel.setBodyStyle(Constants.panelsBodyStyle);
		southPanel.setScrollMode(Scroll.AUTO);
		
		centerPanel = new ContentPanel(){
			protected void onResize(int width, int height) {
					super.onResize(width, height);
					if (isOpenlayersStarted)
						openLayersMap.changeSize(
								Integer.toString(width-Constants.mapPanelSpacingX), 
								Integer.toString(height-Constants.mapPanelSpacingY)
						);
			};
		};
		centerPanel.setHeading(".: Map");
//		centerPanel.setHeaderVisible(false);
//		centerPanel.setBodyBorder(false);
//		centerPanel.setBorders(false);
		centerPanel.setBodyStyle(Constants.panelsBodyStyle);
			
		this.add(westPanel, westPanelData);
		//this.add(northPanel, northPanelData);
		this.add(southPanel, southPanelData);
		this.add(centerPanel, centerPanelData);
				
		initDataPanel();
	}

	public void setLayersPanel(LayersPanel layersPanel) {
		westPanel.add(layersPanel, new AnchorData("100%"));
		this.layout();
	}

	public void setOpenLayers(OpenLayersMap openLayersMap) {
		centerPanel.removeAll();
		
		this.openLayersMap = openLayersMap;
		centerPanel.add(this.openLayersMap.getContentPanel());
		isOpenlayersStarted = true;
		int w = centerPanel.getWidth(); // TODO
		int h = centerPanel.getHeight();
		this.openLayersMap.changeSize((w-Constants.mapPanelSpacingX)+"", (h-Constants.mapPanelSpacingY)+"");
		
		this.layout();
	}
	
	public void setMapEmptyMessage() {
		if (openLayersMap==null) {
			Label l = new Label("No such layers in the map");
			l.setStyleAttribute("font-size", "12px");
			centerPanel.add(l);
			return;
		}
	}
	
	private void initDataPanel() {
		setDataPanelMessage(MSG_NO_DATA_LOADED);

		refreshDataButton = new Button();
		refreshDataButton.setIcon(Images.iconRefresh());
		refreshDataButton.setToolTip(REFRESH_DATA_BUTTON_TOOLTIP);
		refreshDataButton.setEnabled(false);
		refreshDataButton.addListener(Events.Select, new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				handler.showDataPanel();
			}
		});

		exportDataButton = new Button();
		exportDataButton.setIcon(Images.iconTable());
		exportDataButton.setToolTip(EXPORT_DATA_BUTTON_TOOLTIP);
		exportDataButton.setEnabled(false);
		exportDataButton.addListener(Events.Select, new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				handler.exportData(false);
			}
		});

		southPanel.getHeader().addTool(refreshDataButton);
		southPanel.getHeader().addTool(exportDataButton);
		//southPanel.getHeader().addTool(fooButton);

		
		//CICCIO
//		if (!openDataPanel)
//			layout.collapse(LayoutRegion.SOUTH);
		
		//CHANGED BY FRANCESCO - TEMPORARY SOLUTION
		if (!openDataPanel)
			setVisibleSouthPanel(false);
	}
	
	private void setVisibleSouthPanel(boolean bool){
		southPanel.setVisible(bool);
	}

	private MessageBox waitMessageBox;
	
	
	public void setDataPanelWait(boolean wait) {
		if (wait)
			waitMessageBox = MessageBox.wait("", "Generating data results, please wait...", "Generating...");
		else
			waitMessageBox.close();
	}
	
	public void setDataPanelContent(DataResultPanel dpanel) {
		if (dpanel.isEmpty()) {
			this.setDataPanelMessage(MSG_DATAPANEL_EMPTY);
		}
		else {
			refreshDataButton.setEnabled(true);
			exportDataButton.setEnabled(true);
			southPanel.setLayout(new FitLayout());
			southPanel.removeAll();
			southPanel.add(dpanel);
			southPanel.layout();
			this.layout();
		}
		
		//ADDED BY FRANCESCO - TEMPORARY SOLUTION
		setVisibleSouthPanel(true);
//		southPanel.expand();
		layout.expand(LayoutRegion.SOUTH);
		handler.dataPanelOpen(true, southPanel.getHeight());
		
		//CICCIO
//		layout.expand(LayoutRegion.SOUTH);
	}
	
	public void setDataPanelMessage(String msg) {
		southPanel.removeAll();
		southPanel.setLayout(new CenterLayout());
		southPanel.add(new HTML(msg));
		southPanel.layout();
	}

	public String getMapPanelWidth() {
		return Integer.toString(centerPanel.getWidth()-Constants.mapPanelSpacingX);
	}
	
	public String getMapPanelHeight() {
		return Integer.toString(centerPanel.getHeight()-Constants.mapPanelSpacingY);
	}
	
	public void startPanelOpenMonitoring() {
		this.started = true;
	}
	
	public String getVersion() {
		return Constants.VERSION;
	}

}
