/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.widgets;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.gcube.portlets.user.tdw.client.TabularData;
import org.gcube.portlets.user.tdw.client.TabularDataGridPanel;
import org.gcube.portlets.user.trendylyzer_portlet.client.Constants;
import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzerPortletServiceAsync;
import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.FileResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.ImagesResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.MapResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.ObjectResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.Resource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.Resource.ResourceType;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.TableResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.resources.Images;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.widget.core.client.Window;


public class ComputationOutputPanel extends LayoutContainer {

	private String computationId;
	private final TrendyLyzerPortletServiceAsync service = TrendyLyzer_portlet.getService();
	Logger log = Logger.getLogger("");
	public ComputationOutputPanel(String computationId) {
		super();
		log.log(Level.SEVERE,"constructor computationOutputPannel; id :"+ computationId );
		this.computationId = computationId;
	}


	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		// request for a jobItem linked with the computationId (or jobId)
		service.getResourceByJobId(computationId, new AsyncCallback<Resource>() {
			public void onSuccess(Resource result) {
				unmask();
				showOutputInfo(result);
			}
			public void onFailure(Throwable caught) {
				unmask();
				MessageBox.alert("Error", "Impossible to retrieve output info.", null);
			}
		});
		
		this.mask("Loading Result Info...", Constants.maskLoadingStyle);
	}
	
	/**
	 * @param jobOutput
	 */
	protected void showOutputInfo(Resource resource) {
		if (resource==null)
			return;

		ResourceType resourceType = resource.getResourceType();

		switch (resourceType) {
		
		case FILE:
			FileResource fileResource = (FileResource)resource;
		//	this.add(getHtmlTitle("The algorithm produced a <b>File</b>."));					
			
			LayoutContainer lc = getFileResourceOutput(fileResource);
			this.add(lc);
			break;
			
			
		case TABULAR:
			TableResource tabResource = (TableResource)resource;
			this.setWidth(400);
		//	this.add(getHtmlTitle("The algorithm produced a <b>Table</b>."));
			this.add(getTabResourceOutput(tabResource));
			break;

		case IMAGES:
			log.log(Level.SEVERE,"Is an Images");
		//	this.add(getHtmlTitle("The algorithm produced an <b>Set of Images</b>."));
			final ImagesResource imagesResource = (ImagesResource)resource;
			
			this.add(getImagesResourceOutput(imagesResource));
			this.layout();
			break;
			
		case MAP: 
			//this.add(getHtmlTitle("The algorithm produced <b>Multiple Results</b>."));
			
			final MapResource mapResource = (MapResource)resource;
			
			this.add(getMultipleOutput(mapResource));
			this.layout();
			break;
			
		}
		
		this.add(new Html("<br/>"));
		this.layout();
	}


	/**
	 * @param string
	 * @return
	 */
	private Html getHtmlTitle(String title) {
		Html html = new Html(title);
		html.setStyleName("jobViewer-output-outputType");
		return html;
	}


	/**
	 * @param imagesResource
	 * @return
	 */
	private Component getImagesResourceOutput(ImagesResource imagesResource) {
		Map<String, String> mapImages = imagesResource.getMapImages();
		LayoutContainer lc = new LayoutContainer();
		lc.add(new ImagesViewer(computationId, mapImages));
		return lc;
	}


	/**
	 * @param tabResource
	 */
	private LayoutContainer getTabResourceOutput(TableResource tabResource) {
		final String tableName = tabResource.getName();
		final String tableId = tabResource.getResourceId();
		LayoutContainer lc = new LayoutContainer();
		lc.add(new Html("Data Set Created: <b>" + tableName + "</b><br/><br/>"));
		
		lc.add(new Button("<b>Show Data Set</b>", Images.table(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				final TabularData tabularData = TrendyLyzer_portlet.getTabularData();
				TabularDataGridPanel grid = tabularData.getGridPanel();	
				grid.setHeaderVisible(false);
				Window window = new Window();
				window.setMaximizable(true);
				window.setHeadingText("Data Set "+tableName);
				window.setModal(true);
				window.add(grid);
				window.setWidth(900);
				window.setHeight(500);
				tabularData.openTable(tableId);
				window.show();
			}
		}));
		return lc;
	}


	/**
	 * @param fileResource
	 * @return
	 */
	private LayoutContainer getFileResourceOutput(FileResource fileResource) {
		LayoutContainer lc = new LayoutContainer();
		final String fileName = fileResource.getName();
		final String fileUrl = fileResource.getUrl();
		lc.add(new Html("File Created and stored in the Data Space: <b>" + fileName + "</b><br/><br/>"));
		lc.add(new Button("<b>Download File</b>", Images.fileDownload(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				String smpEncoded = URL.encodeQueryString(fileUrl);
				String url = GWT.getModuleBaseURL() + "DownloadService?url=" + smpEncoded + "&name="+fileName;
				com.google.gwt.user.client.Window.open(url, "_blank", "");
			}
		}));
		return lc;
	}


	/**
	 * @param map
	 * @return
	 */
	private VerticalPanel getMultipleOutput(MapResource mapResource) {
		Map<String, Resource> map = mapResource.getMap();		
		VerticalPanel vp = new VerticalPanel();
		
		Map<String, String> mapValues = new LinkedHashMap<String, String>();
		Map<String, FileResource> mapFiles = new LinkedHashMap<String, FileResource>();
		Map<String, TableResource> mapTabs = new LinkedHashMap<String, TableResource>();
		Map<String, ImagesResource> mapImages = new LinkedHashMap<String, ImagesResource>();
		
		for (String key: map.keySet()) {
			Resource resource = map.get(key);
			ResourceType resourceType = resource.getResourceType();

			switch (resourceType) {
			case OBJECT:
				mapValues.put(key, ((ObjectResource)resource).getValue());
				break;				
			case FILE:
				mapFiles.put(key, (FileResource)resource);
				break;				
			case TABULAR:
				mapTabs.put(key, (TableResource)resource);
				break;
			case IMAGES:
				mapImages.put(key, (ImagesResource)resource);
				break;
			case MAP:
				
				// TODO: for recursive-map
				
				break;				
			}
		}
		
		if (mapValues.size()>0) {
			Html html = new Html("Output Values");
			html.setStyleName("jobViewer-output-groupTitle");
			vp.add(html);
			
			vp.add(new HashMapViewer(mapValues));

			html = new Html("<div class='jobViewer-output-separator'></div>");
			vp.add(html);
		}
		
		if (mapFiles.size()>0) {
			Html html = new Html("Files"); 
			html.setStyleName("jobViewer-output-groupTitle");
			vp.add(html);
			
			for (String fileKey: mapFiles.keySet()) {
				vp.add(new Html("<i>"+fileKey+"</i>"));
				vp.add(getFileResourceOutput(mapFiles.get(fileKey)));
			}

			html = new Html("<div class='jobViewer-output-separator'></div>");
			vp.add(html);
		}
		
		if (mapTabs.size()>0) {
			Html html = new Html("Tables"); 
			html.setStyleName("jobViewer-output-groupTitle");
			vp.add(html);

			for (String tabKey: mapTabs.keySet()) {
				vp.add(new Html("<i>"+tabKey+"</i>"));
				vp.add(getTabResourceOutput(mapTabs.get(tabKey)));
			}

			html = new Html("<div class='jobViewer-output-separator'></div>");
			vp.add(html);
		}
		
		if (mapImages.size()>0) {
			Html html = new Html(""); 
			html.setStyleName("jobViewer-output-groupTitle");
			vp.add(html);
			
			for (String imagesKey: mapImages.keySet()) {
				//vp.add(new Html("<i>"+imagesKey+"</i>"));
				vp.add(getImagesResourceOutput(mapImages.get(imagesKey)));
			}
			
			html = new Html("<div class='jobViewer-output-separator'></div>");
			vp.add(html);
		}
		
		return vp;
	}
}
