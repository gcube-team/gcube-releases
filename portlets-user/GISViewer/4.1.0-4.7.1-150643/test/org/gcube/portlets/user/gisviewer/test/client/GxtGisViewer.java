package org.gcube.portlets.user.gisviewer.test.client;

import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.GisViewer;
import org.gcube.portlets.user.gisviewer.client.GisViewerParameters;
import org.gcube.portlets.user.gisviewer.client.GisViewerSaveHandler;
import org.gcube.portlets.user.gisviewer.client.GisViewerService;
import org.gcube.portlets.user.gisviewer.client.GisViewerServiceAsync;
import org.gcube.portlets.user.gisviewer.client.Constants.Mode;
import org.gcube.portlets.user.gisviewer.client.commons.beans.GroupInfo;
import org.gcube.portlets.user.gisviewer.client.commons.beans.SavedGroup;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GxtGisViewer implements EntryPoint {

	private GisViewerServiceAsync service;
	private GisViewer gisViewer;

	public void onModuleLoad() {
		//disableDefaultContextMenu();
		service = (GisViewerServiceAsync) GWT.create(GisViewerService.class);
		
		Button openButton = new Button(".: click :.", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				initGeoWindow();
			}
		});
		//openButton.setIcon(IconHelper.createPath("images/engine_start.jpg", 330, 290));
		//openButton.setSize(336, 300);
		
		RootPanel.get("entryDiv").add(new HTML("<br/>"));
		RootPanel.get("entryDiv").add(openButton);
		
		if (Constants.MODE==Mode.TEST) {
			RootPanel.get("entryDiv").add(new GridPluginsExample());
			//initGeoWindow();
			//RootPanel.get("entryDiv").add(new TreeExample());
		}
		
		RootPanel.get().add(new Button("Add layer \"salinity\"", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (gisViewer!=null)
					gisViewer.addLayerByName("salinity");
			}
		}));
}

	protected void initGeoWindow() {
		GisViewerParameters gisViewerParameters = new GisViewerParameters();
	
		/*
		 * ADDING AN ARRAYLIST OF LAYER TITLES
		 */
//		List<String> layers = new ArrayList<String>();
//		layers.add("TrueMarble.16km.2700x1350");
//		layers.add("depthmean");
//		layers.add("eezall");
//		gisViewerParameters.setOpeningLayers(layers);

		/*
		 * ADDING AN ARRAY OF LAYERS TITLES
		 */
//		String[] layers = {"TrueMarble.16km.2700x1350", "depthmean", "eezall", "v_point_geometries_example"};
//		gisViewerParameters.setOpeningLayers(layers);
		
		/*
		 * ADDING A GROUP NAME
		 */
		gisViewerParameters.setOpeningGroup(Constants.defaultGroup);
		gisViewerParameters.setGisViewerSaveHandler(new GisViewerSaveHandler() {
			@Override
			public void saveLayerImage(String name, String contentType, String url) {
				// TODO Auto-generated method stub
			}

			@Override
			public void saveMapImage(String name, String outputFormat,
					String bbox, String width, String height,
					String[] geoservers, String[] layers, String[] styles,
					String[] opacities, String[] cqlfilters, String[] gsrefs) {
				// TODO Auto-generated method stub
				
			}

		});

		gisViewer = new GisViewer(gisViewerParameters);
		gisViewer.show();
	}

	private void showSaveDialogLayer(final String format, String content_type, String link) {

		final Window dialogBox = new Window();
		// dialogBox.addStyleName("z_index_1200");

		VerticalPanel hcafLegend = new VerticalPanel();

		// link = Constants.geoServerURL + link;

		// hcafLegend.add(new Label("Link to save: " + link));
		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("close",
				new SelectionListener<ButtonEvent>() {
					public void componentSelected(ButtonEvent ce) {
						dialogBox.hide();
					}
				});
		closeButton.addStyleName("border_left border_right border_top");

		closeButton.setWidth("50px");

		Button saveButton = new Button("save",
				new SelectionListener<ButtonEvent>() {
					public void componentSelected(ButtonEvent ce) {
					}
				});
		saveButton.addStyleName("border_left border_right border_top");
		saveButton.setWidth("100px");

		HorizontalPanel hp = new HorizontalPanel();
		hp.add(saveButton);
		hp.add(closeButton);
		hcafLegend.add(hp);
		hcafLegend.setCellHorizontalAlignment(hp,
				HasHorizontalAlignment.ALIGN_RIGHT);

		// legendPanel.insert(hcafLegend, 1);
		dialogBox.setTitle("Save Layer as " + format);
		// dialogBox.setAnimationEnabled(true);
		// dialogBox.setWidth("350px");
		hcafLegend.setWidth("400px");
		dialogBox.add(hcafLegend);

		dialogBox.center();

		// Show the popup
		dialogBox.show();
	}	
}