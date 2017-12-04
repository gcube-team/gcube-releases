/**
 *
 */
package org.gcube.portlets.user.geoexplorer.client.layerinfo;

import java.util.List;

import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.client.DialogGisLink;
import org.gcube.portlets.user.geoexplorer.client.GeoExplorer;
import org.gcube.portlets.user.geoexplorer.client.WindowMetadataView;
import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.client.resources.Images;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 4, 2013
 *
 */
public class ToolbarSummaryLayerInfo extends ToolBar {



	/**
	 *
	 */
	public static final String ISOCORE = "ISOCORE";
	/**
	 *
	 */
	public static final String INSPIRE = "INSPIRE";
	/**
	 *
	 */
	public static final String SIMPLE = "SIMPLE";
	private LayerItem lastLayerItem;
	private MenuItem menuxpandAsGeonSimple;
	private MenuItem menuxpandAsGeonInspire;
	private MenuItem menuxpandAsGeonISOCORE;
	private MenuItem menuNewBrowserGeonSimple;
	private MenuItem menuNewBrowserGeonInspire;
	private MenuItem menuNewBrowserGeonISOCORE;
	private String scope;


	private Button buttonGetGisViewerLink;
	/**
	 *
	 */
	public ToolbarSummaryLayerInfo() {
		instanceToolbar();
	}

	private void instanceToolbar() {

		Button buttonFullWindow = new Button("Fully");
		buttonFullWindow.setToolTip("View the metadata in a window");
		buttonFullWindow.setIcon(Images.viewFull());

		// EXPAND
		Menu menuExpand = new Menu();

		MenuItem menuExpandSummary = new MenuItem("Expand Summary");
		menuExpandSummary.setToolTip("View the summary in a window");
		menuExpandSummary.setIcon(Images.iconSummary());

		menuExpandSummary.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						if (lastLayerItem != null) {
							String urlMetadataViewer = MetadataServletURLBinder.getMetadataViewerURL(Constants.SUMMARY_METADATA_ISO19139_VIEW, lastLayerItem, true, true, true,scope);
							String heading = "Summary Metadata View of "+lastLayerItem.getName() + " - uuid: "+lastLayerItem.getUuid();
							WindowMetadataView win = new WindowMetadataView(heading, lastLayerItem, urlMetadataViewer);
						}

					}
				});

		menuExpand.add(menuExpandSummary);

		MenuItem menuExpandAsTable = new MenuItem("Expand as Table");
		menuExpandAsTable
				.setToolTip("Display the metadata as tables in a window");
		menuExpandAsTable.setIcon(Images.iconTable());

		menuExpandAsTable.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						if (lastLayerItem != null) {
							String urlMetadataViewer = MetadataServletURLBinder.getMetadataViewerURL(
									Constants.METADATA_ISO19139_VIEW,
									lastLayerItem, true, true, true,scope);
							String heading = "Table Metadata View of "+lastLayerItem.getName() + " - uuid: "+lastLayerItem.getUuid();
							new WindowMetadataView(heading, lastLayerItem,
									urlMetadataViewer);
						}

					}
				});

		menuExpand.add(menuExpandAsTable);


		menuxpandAsGeonSimple = new MenuItem("Geonetwork Simple");
		menuxpandAsGeonSimple.setData(SIMPLE, SIMPLE);
		menuxpandAsGeonSimple.setToolTip("Display the metadata as geonetwork simple style in a window");
		menuxpandAsGeonSimple.setIcon(Images.iconGeonetworkSimple());

		menuxpandAsGeonSimple.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						if (lastLayerItem != null) {
							String urlMetadataViewer = MetadataServletURLBinder.getEmbeddedGeonetworkMetadataViewerURL(lastLayerItem, Constants.SIMPLE,scope);
							String heading = "Geonetwork simple Metadata View of "+lastLayerItem.getName() + " - uuid: "+lastLayerItem.getUuid();
							new WindowMetadataView(heading, lastLayerItem,urlMetadataViewer);
						}

					}
				});

		menuExpand.add(menuxpandAsGeonSimple);


		menuxpandAsGeonInspire = new MenuItem("Geonetwork Inspire");
		menuxpandAsGeonInspire.setData(INSPIRE, INSPIRE);
		menuxpandAsGeonInspire.setToolTip("Display the metadata as geonetwork inspire style in a window");
		menuxpandAsGeonInspire.setIcon(Images.iconGeonetworkInspire());

		menuxpandAsGeonInspire.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						if (lastLayerItem != null) {
							String urlMetadataViewer = MetadataServletURLBinder.getEmbeddedGeonetworkMetadataViewerURL(lastLayerItem, Constants.INSPIRE,scope);
							String heading = "Geonetwork inspire Metadata View of "+lastLayerItem.getName() + " - uuid: "+lastLayerItem.getUuid();
							new WindowMetadataView(heading, lastLayerItem,urlMetadataViewer);
						}

					}
				});

		menuExpand.add(menuxpandAsGeonInspire);

		menuxpandAsGeonISOCORE = new MenuItem("Geonetwork IsoCore");
		menuxpandAsGeonInspire.setData(ISOCORE, ISOCORE);
		menuxpandAsGeonISOCORE.setToolTip("Display the metadata as geonetwork ISOCORE style in a window");
		menuxpandAsGeonISOCORE.setIcon(Images.iconGeonetworkIsoCore());

		menuxpandAsGeonISOCORE.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						if (lastLayerItem != null) {
							String urlMetadataViewer = MetadataServletURLBinder.getEmbeddedGeonetworkMetadataViewerURL(lastLayerItem, Constants.ISOCORE,scope);
							String heading = "Geonetwork ISOCORE Metadata View of "+lastLayerItem.getName() + " - uuid: "+lastLayerItem.getUuid();
							new WindowMetadataView(heading, lastLayerItem,urlMetadataViewer);
						}

					}
				});

		menuExpand.add(menuxpandAsGeonISOCORE);



		buttonFullWindow.setMenu(menuExpand);

		// "New Browser Tab"
		Button buttonNewTabMetadata = new Button("New Browser Tab");
		buttonNewTabMetadata.setIcon(Images.expandInNewWindow());
		buttonNewTabMetadata
				.setToolTip("Opens the metadata in a new tab of the browser");
		buttonNewTabMetadata
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

					}
				});

		Menu menuNewBrowserTab = new Menu();

		MenuItem menuNewBrowserSummary = new MenuItem("View Summary");
		menuNewBrowserSummary.setToolTip("View the summary in a new tab of the browser");
		menuNewBrowserSummary.setIcon(Images.iconSummary());

		menuNewBrowserSummary
				.addSelectionListener(new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						if (lastLayerItem != null) {

							GeoExplorer.service.isSessionExpired(new AsyncCallback<Boolean>() {

								@Override
								public void onSuccess(Boolean result) {

									if(!result){
										String urlMetadataViewer = MetadataServletURLBinder.getMetadataViewerURL(
											Constants.SUMMARY_METADATA_ISO19139_VIEW,
											lastLayerItem, true, true, true, scope);
										Window.open(urlMetadataViewer, "_blank", null);
									}
									else
										CheckSession.getInstance().showLogoutDialog();
								}

								@Override
								public void onFailure(Throwable caught) {

									CheckSession.getInstance().showLogoutDialog();
								}
							});
						}

					}
				});

		menuNewBrowserTab.add(menuNewBrowserSummary);

		MenuItem menuNewBrowserTable = new MenuItem("View Tables");
		menuNewBrowserTable
				.setToolTip("Display the metadata as table in a new tab of the browser");
		menuNewBrowserTable.setIcon(Images.iconTable());

		menuNewBrowserTable.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				if (lastLayerItem != null) {

					GeoExplorer.service.isSessionExpired(new AsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {

							if(!result){
								String urlMetadataViewer = MetadataServletURLBinder.getMetadataViewerURL(
									Constants.METADATA_ISO19139_VIEW,
									lastLayerItem, true, true, true,scope);
								Window.open(urlMetadataViewer, "_blank", null);
							}
							else
								CheckSession.getInstance().showLogoutDialog();
						}

						@Override
						public void onFailure(Throwable caught) {

							CheckSession.getInstance().showLogoutDialog();
						}
					});
				}
			}
		});

		menuNewBrowserTab.add(menuNewBrowserTable);

		menuNewBrowserGeonSimple = new MenuItem("Geonetwork Simple");
		menuNewBrowserGeonSimple.setData(SIMPLE, SIMPLE);
		menuNewBrowserGeonSimple.setToolTip("Display the metadata as geonetwork simple style in a new tab of the browser");
		menuNewBrowserGeonSimple.setIcon(Images.iconGeonetworkSimple());

		menuNewBrowserGeonSimple.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				if (lastLayerItem != null) {
					GeoExplorer.service.isSessionExpired(new AsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {

							if(!result){
								String urlMetadataViewer = MetadataServletURLBinder.getEmbeddedGeonetworkMetadataViewerURL(lastLayerItem, Constants.SIMPLE,scope);
								Window.open(urlMetadataViewer, "_blank", null);
							}
							else
								CheckSession.getInstance().showLogoutDialog();
						}

						@Override
						public void onFailure(Throwable caught) {

							CheckSession.getInstance().showLogoutDialog();
						}
					});
				}
			}
		});

		menuNewBrowserTab.add(menuNewBrowserGeonSimple);

		menuNewBrowserGeonInspire = new MenuItem("Geonetwork Inspire");
		menuNewBrowserGeonInspire.setData(INSPIRE, INSPIRE);
		menuNewBrowserGeonInspire.setToolTip("Display the metadata as geonetwork inspire style in a new tab of the browser");
		menuNewBrowserGeonInspire.setIcon(Images.iconGeonetworkInspire());

		menuNewBrowserGeonInspire.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				if (lastLayerItem != null) {
					GeoExplorer.service.isSessionExpired(new AsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {

							if(!result){
								String urlMetadataViewer = MetadataServletURLBinder.getEmbeddedGeonetworkMetadataViewerURL(lastLayerItem, Constants.INSPIRE,scope);
								Window.open(urlMetadataViewer, "_blank", null);
							}
							else
								CheckSession.getInstance().showLogoutDialog();
						}

						@Override
						public void onFailure(Throwable caught) {

							CheckSession.getInstance().showLogoutDialog();
						}
					});

				}

			}
		});


		menuNewBrowserTab.add(menuNewBrowserGeonInspire);


		menuNewBrowserGeonISOCORE = new MenuItem("Geonetwork IsoCore");
		menuNewBrowserGeonISOCORE.setData(ISOCORE, ISOCORE);
		menuNewBrowserGeonISOCORE.setToolTip("Display the metadata as geonetwork ISOCORE style in a new tab of the browser");
		menuNewBrowserGeonISOCORE.setIcon(Images.iconGeonetworkIsoCore());

		menuNewBrowserGeonISOCORE.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				if (lastLayerItem != null) {
					GeoExplorer.service.isSessionExpired(new AsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {

							if(!result){
								String urlMetadataViewer = MetadataServletURLBinder.getEmbeddedGeonetworkMetadataViewerURL(lastLayerItem, Constants.ISOCORE,scope);
								Window.open(urlMetadataViewer, "_blank", null);
							}
							else
								CheckSession.getInstance().showLogoutDialog();
						}

						@Override
						public void onFailure(Throwable caught) {

							CheckSession.getInstance().showLogoutDialog();
						}
					});
				}

			}
		});

		menuNewBrowserTab.add(menuNewBrowserGeonISOCORE);


		buttonNewTabMetadata.setMenu(menuNewBrowserTab);

		Button buttonSourceView = new Button("View XML source");
		buttonSourceView.setIcon(Images.viewSource());
		buttonSourceView.setToolTip("Opens the metadata xml source in a new tab  of the browser");

		buttonSourceView.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (lastLayerItem != null) {
					GeoExplorer.service.isSessionExpired(new AsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {

							if(!result){
								String urlMetadataSourceViewer = MetadataServletURLBinder.getMetadataSourceViewerURLWithUUIDParameter(lastLayerItem.getUuid(),scope);
								Window.open(urlMetadataSourceViewer, "_blank", null);
							}
							else
								CheckSession.getInstance().showLogoutDialog();
						}

						@Override
						public void onFailure(Throwable caught) {

							CheckSession.getInstance().showLogoutDialog();
						}
					});
				}

			}
		});

		buttonGetGisViewerLink = new Button("Gis Link");
		buttonGetGisViewerLink.setTitle("Get a public link to open the layer in Gis Viewer Application");
		buttonGetGisViewerLink.setIcon(Images.iconMapLink());
		buttonGetGisViewerLink.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (lastLayerItem != null && lastLayerItem.getUuid()!=null) {
					GeoExplorer.service.isSessionExpired(new AsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {

							if(!result){
								DialogGisLink gisLink = new DialogGisLink("Copy to clipboard Gis Link: Ctrl+C", lastLayerItem.getUuid());
								gisLink.show();
							}
							else
								CheckSession.getInstance().showLogoutDialog();
						}

						@Override
						public void onFailure(Throwable caught) {

							CheckSession.getInstance().showLogoutDialog();
						}
					});
				}

			}
		});

		add(buttonFullWindow);
		add(buttonNewTabMetadata);
		add(buttonSourceView);
		add(buttonGetGisViewerLink);
	}

	public static native Document getBodyElement() /*-{
        var win = window.open("about:blank", "xml");
		win.document.open("Content-type: text/xml");
		win.document.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		win.document.close();
		win.focus();

        return win.document;
    }-*/;



	public LayerItem getLastLayerItem() {
		return lastLayerItem;
	}

	public void setLastLayerItem(LayerItem lastLayerItem) {
		this.lastLayerItem = lastLayerItem;
	}

	/**
	 * @param styles
	 */
	public void showMetadataStyles(List<? extends GeoexplorerMetadataStyleInterface> styles) {
		if(styles==null)
			return;

		for (GeoexplorerMetadataStyleInterface geoexplorerMetadataStyleInterface : styles) {
			String style = geoexplorerMetadataStyleInterface.getStyle();
			boolean isDisplay = geoexplorerMetadataStyleInterface.isDisplay();
			GWT.log("Setting metadata style displaying, style: "+style+ " display: "+isDisplay);
			if(style.compareTo(SIMPLE)==0){
				menuNewBrowserGeonInspire.setVisible(isDisplay);
				menuxpandAsGeonInspire.setVisible(isDisplay);
			}else if(style.compareTo(ISOCORE)==0){
				menuNewBrowserGeonISOCORE.setVisible(isDisplay);
				menuxpandAsGeonISOCORE.setVisible(isDisplay);
			}else if(style.compareTo(INSPIRE)==0){
				menuNewBrowserGeonInspire.setVisible(isDisplay);
				menuxpandAsGeonInspire.setVisible(isDisplay);
			}
		}
	}

	/**
	 * @param scope
	 */
	public void updateScope(String scope) {
		this.scope = scope;
	}

}
