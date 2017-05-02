/**
 *
 */
package org.gcube.portlets.user.geoexplorer.client.layerinfo;

import java.util.List;

import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.client.GeoExplorer;
import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.client.resources.Images;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;


/**
 * The Class LayerInfoPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 26, 2016
 */
public class LayerInfoPanel extends ContentPanel {
	private LayerItem lastLayerItem;
	private ToolbarSummaryLayerInfo toolbarLayerInfo;
	private String scope;
	private LayerInfoPanel INSTANCE = this;

	private AbstractImagePrototype loading = Images.iconLoading();

	/**
	 * Instantiates a new layer info panel.
	 *
	 * @param title the title
	 */
	public LayerInfoPanel(String title) {
		this.setLayout(new FitLayout());
		this.setHeading(title);
		this.setBodyStyle(Constants.panelsBodyStyle);
		toolbarLayerInfo = new ToolbarSummaryLayerInfo();
		setTopComponent(toolbarLayerInfo);

	}

	/**
	 * Gets the metadata source viewer url.
	 *
	 * @param uuid the uuid
	 * @return the metadata source viewer url
	 */
	public String getMetadataSourceViewerURL(String uuid) {
		//<serverName>/<servletName>?geoserver=<geoserverUrl>&layer=<layerCompleteName>
		return GWT.getModuleBaseURL() + Constants.METADATA_ISO19139_SOURCE_VIEW+"?"
				+Constants.UUID +"="+ uuid+"&"
				+Constants.RANDOM +"="+ Random.nextInt(Constants.UPPERBOUND)*Random.nextInt(Constants.UPPERBOUND)
				+ "&" + Constants.SCOPE +"=" +scope;
	}

	/**
	 * Show layer details.
	 *
	 * @param layerItem the layer item
	 */
	public void showLayerDetails(final LayerItem layerItem) {
		GWT.log("show details for "+layerItem);
		this.lastLayerItem = layerItem;
		this.toolbarLayerInfo.setLastLayerItem(lastLayerItem);
		removeAll();
		final Image loader = loading.createImage();
		add(loader);
		final Frame frame = new Frame();

		GeoExplorer.service.isSessionExpired(new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				if(!result){
					String urlMetadataViewer = MetadataServletURLBinder.getMetadataViewerURL(Constants.SUMMARY_METADATA_ISO19139_VIEW, layerItem, true, true, true, scope);
					frame.setUrl(urlMetadataViewer);
					frame.setWidth("100%");
					frame.setHeight("100%");
					frame.getElement().getStyle().setBorderWidth(0, Unit.PX);

					frame.addLoadHandler(new LoadHandler() {

						@Override
						public void onLoad(LoadEvent event) {
							try{
							remove(loader);
							}catch(Exception e){

							}
						}
					});

					INSTANCE.layout();

				}
				else
					CheckSession.getInstance().showLogoutDialog();
			}

			@Override
			public void onFailure(Throwable caught) {

				CheckSession.getInstance().showLogoutDialog();
			}
		});


		add(frame);
	}

	/**
	 * Gets the capital words.
	 *
	 * @param string the string
	 * @return the capital words
	 */
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
	 * Update metadata styles to show.
	 *
	 * @param styles the styles
	 */
	public void updateMetadataStylesToShow(List<? extends GeoexplorerMetadataStyleInterface> styles) {
		toolbarLayerInfo.showMetadataStyles(styles);
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Update scope.
	 *
	 * @param scope the scope
	 */
	public void updateScope(String scope) {
		this.scope = scope;
		this.toolbarLayerInfo.updateScope(scope);
	}
}
