/**
 *
 */
package org.gcube.portlets.user.geoexplorer.client.layerinfo;

import java.util.List;

import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.client.GeoExplorer;
import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ceras
 * modified by Francesco Mangiacrapa
 *
 */
public class LayerInfoPanel extends ContentPanel {
	/**
	 *
	 */
//	protected static final String MAP_PREVIEW_GENERATOR = "MapPreviewGenerator";
	private static final ImageResource IMAGE_PRELOADER = GeoExplorer.resources.iconPreload();
	private String html;
//	private LayerInfoPanel instance;

	private ContentPanel north = new ContentPanel();
	private ContentPanel center = new ContentPanel();
	private BorderLayout borderLayout = new BorderLayout();
	private LayerItem lastLayerItem;

	private ToolbarSummaryLayerInfo toolbarLayerInfo;

	private String scope;

	/**
	 *
	 */
	public LayerInfoPanel(String title) {
		super();
		this.setLayout(borderLayout);
		this.createLayout();
		this.setHeading(title);
		this.setBodyStyle(Constants.panelsBodyStyle);
		this.setScrollMode(Scroll.AUTO);
//		this.instance = this;

		toolbarLayerInfo = new ToolbarSummaryLayerInfo();

		north.setTopComponent(toolbarLayerInfo);

	}

	/**
	 *
	 * @param uuid
	 * @param returnTagHead
	 * @param returnTagBody
	 * @param loadPreviewLayer
	 * @return
	 */
	public String getMetadataSourceViewerURL(String uuid) {
		//<serverName>/<servletName>?geoserver=<geoserverUrl>&layer=<layerCompleteName>
		return GWT.getModuleBaseURL() + Constants.METADATA_ISO19139_SOURCE_VIEW+"?"
				+Constants.UUID +"="+ uuid+"&"
				+Constants.RANDOM +"="+ Random.nextInt(Constants.UPPERBOUND)*Random.nextInt(Constants.UPPERBOUND)
				+ "&" + Constants.SCOPE +"=" +scope;
	}

	/**
	 * @param layerItem
	 */
	public void showLayerDetails(LayerItem layerItem) {

		GWT.log("show details for "+layerItem);

		this.lastLayerItem = layerItem;
		this.toolbarLayerInfo.setLastLayerItem(lastLayerItem);


//		String[][] fields = {{LayerItem.LAYER_TITLE,"Title"}, {LayerItem.LAYER_NAME,"Name"}, {LayerItem.LAYER,"Full Name"}, {LayerItem.GEOSERVER_URL,"WMS Geoserver base URL"}};
		String[][] fields = {{LayerItem.GEOSERVER_URL,"WMS Geoserver base URL"}};

		this.html = "<ul class='layerInfoPanel'>";
		for (String[] field: fields) {
			String value = layerItem.get(field[0]);
			String escValue = SafeHtmlUtils.htmlEscape(value);
			html += "<li class='layerInfoPanel-field'>" + field[1] +
					"	<ul>" +
					"		<li title="+escValue+" class='layerInfoPanel-item-value'><nobr>" + escValue + "</nobr></li>" +
					"	</ul>" +
					"</li>";
			//&nbsp; <b>" + field[1] + ":</b><br>&nbsp;&nbsp;&nbsp;&nbsp;<nobr>"+value+"</nobr><br><br>";
			//html += "&nbsp; <b>" + field[1] + ":</b><br>&nbsp;&nbsp;&nbsp;&nbsp;<nobr>"+value+"</nobr><br><br>";
		}
		html += parseDescription(layerItem.getDescription());
		html += "<li class='layerInfoPanel-field'>Layer Preview</li>";
		html += "</ul>";

		String urlImage = getUrlPreviewMap(layerItem);
		final Image imgPreview = new Image(urlImage);
		imgPreview.setStyleName("layerInfoPanel-imgPreview");
		imgPreview.setVisible(false);

		final Image imgPreloader = new Image(IMAGE_PRELOADER);
		imgPreloader.setStyleName("layerInfoPanel-imgPreload");

		String urlMetadataViewer = MetadataServletURLBinder.getMetadataViewerURL(Constants.SUMMARY_METADATA_ISO19139_VIEW, layerItem, true, true, false, scope);
//		System.out.println(urlMetadataViewer);

		Frame frame = new Frame(urlMetadataViewer);

		center.removeAll();
		north.removeAll();

		north.add(frame);

		center.add(new Html(html));
		center.add(imgPreloader);
		center.add(imgPreview);


		this.layout();

		imgPreview.addLoadHandler(new LoadHandler(){
			public void onLoad(LoadEvent event) {
				center.remove(imgPreloader);
				imgPreview.setVisible(true);
				layout();
			}
		});
	}


	private void createLayout(){

		   BorderLayout layout = new BorderLayout();
		   setLayout(layout);
		   setStyleAttribute("padding", "0px");

		    //uncomment this section if you dont want to see headers
		    /*
		     * west.setHeaderVisible(false);
		     * center.setHeaderVisible(false);
		     */

		    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,390);
		    northData.setSplit(true);
		    northData.setCollapsible(true);
		    northData.setMargins(new Margins(0,0,2,0));

		    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		    centerData.setMargins(new Margins(0));

		    north.setHeaderVisible(false);
		    center.setHeaderVisible(false);

//		    center.setLayout(new FitLayout());

		    north.setLayout(new FitLayout());

		    add(north, northData);
		    add(center, centerData);

	}

//	/**
//	 * @param layerItem
//	 * @return
//	 */
//	private String getMetadataUrl(String uuid, boolean returnTagHead, boolean returnTagBody) {
//		//<serverName>/<servletName>?geoserver=<geoserverUrl>&layer=<layerCompleteName>
//		return GWT.getModuleBaseURL() + "MetadataISO19139View?"
//				+Constants.UUID +"="+ uuid +"&"
//				+Constants.GETBODYHTML+"="+returnTagBody +"&"
//				+Constants.GETHEADHTML+"="+returnTagHead +"&"
//				+Constants.LOADPREVIEW+"=false";
//	}
//


	/**
	 * @param description
	 * @return
	 */
	private String parseDescription(String description) {

		if (description==null || description.trim().contentEquals(""))
			return "";

		// try split by pipe
		String[] splitPipe = description.split("\\|");

		if (splitPipe.length==1)
			return
				"<li class='layerInfoPanel-field'>Description" +
				"	<ul>" +
				"		<li class='layerInfoPanel-item-value'>" + SafeHtmlUtils.htmlEscape(description.trim()) + "</li>" +
				"	</ul>" +
				"</li>";

		// map case
		String ris = "<li class='layerInfoPanel-field'>Metadata<ul>";
		for (String metadataItem: splitPipe) {
			// try split by ":"
			String[] splitPoints = metadataItem.split(":");
			String metadata = splitPoints.length==0 ?  SafeHtmlUtils.htmlEscape(metadataItem.trim()) :
				"<span class='layerInfoPanel-key'>"+getCapitalWords(splitPoints[0]) + ": </span>" +
				"<span class='layerInfoPanel-value'>" + SafeHtmlUtils.htmlEscape(metadataItem.substring(splitPoints[0].length()+1).trim()) + "</span>";
			ris += "<li class='layerInfoPanel-item-value'>" + metadata  + "</li>";
		}
		ris += "</ul></li>";
		return ris;
	}

	public String getCapitalWords(String string) {
		String ris = "";

		boolean precUnderscore = true;
		for (int i=0; i<string.length(); i++) {
			char c = string.charAt(i);

			if (c == '_') {
				precUnderscore = true;
				ris += " ";
			} else {
				ris += precUnderscore ? Character.toUpperCase(c) : Character.toLowerCase(c);
				if (precUnderscore == true)
					precUnderscore = false;
			}
		}
		return SafeHtmlUtils.htmlEscape(ris.trim());
	}

	/**
	 * @param layerItem
	 * @return
	 */
	private String getUrlPreviewMap(LayerItem layerItem) {
		//<serverName>/<servletName>?geoserver=<geoserverUrl>&layer=<layerCompleteName>
//		String url = GWT.getModuleBaseURL() + "MapPreviewGenerator?"
//				+ "geoserver=" + layerItem.getGeoserverUrl()+ "/wms/"
//				+ "&layer=" + layerItem.getLayer();

//		String url = GWT.getModuleBaseURL() + MAP_PREVIEW_GENERATOR+"?"
//				+ Constants.GEOSERVER+"=" + layerItem.getWmsServiceUrl()
//				+ "&"+Constants.LAYER+"=" + layerItem.getLayer()
//				+ "&"+Constants.CRS+"=" + layerItem.getCrs()
//				+ "&"+Constants.WMSVERSION+"=" + layerItem.getVersionWMS();


		String url = GWT.getModuleBaseURL() + Constants.MAP_PREVIEW_GENERATOR+"?" +
		Constants.WMS_REQUEST_PARAMETER+"="+URL.encodeQueryString(layerItem.getWMSRequest());

//		+ "&"+Constants.RANDOM +"="+ Random.nextInt(Constants.UPPERBOUND)*Random.nextInt(Constants.UPPERBOUND);

		if(layerItem.getMapWmsNotStandardParameters()!=null && layerItem.getMapWmsNotStandardParameters().size()>0)
			for (String key : layerItem.getMapWmsNotStandardParameters().keySet()) {
				url+="&"+key+"="+layerItem.getMapWmsNotStandardParameters().get(key); //&PARAM = VALUE
			}

		GWT.log("Returning url preview map: "+url);

		return url;
	}

	/**
	 * @param styles
	 */
	public void updateMetadataStylesToShow(List<? extends GeoexplorerMetadataStyleInterface> styles) {
		toolbarLayerInfo.showMetadataStyles(styles);
	}

	public String getScope() {
		return scope;
	}

	public void updateScope(String scope) {
		this.scope = scope;
		this.toolbarLayerInfo.updateScope(scope);
	}
}
