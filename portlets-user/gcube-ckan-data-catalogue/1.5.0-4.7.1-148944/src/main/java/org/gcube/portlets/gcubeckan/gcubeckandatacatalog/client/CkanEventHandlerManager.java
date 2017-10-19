
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import org.gcube.datacatalogue.grsf_manage_widget.client.view.ManageProductWidget;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.EditMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.EditMetadataEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.IFrameInstanciedEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.IFrameInstanciedEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.InsertMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.InsertMetadataEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.NotifyLogoutEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.NotifyLogoutEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShareLinkEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShareLinkEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowDatasetsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowDatasetsEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowGroupsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowGroupsEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowHomeEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowHomeEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowManageProductWidgetEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowManageProductWidgetEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowOrganizationsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowOrganizationsEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowStatisticsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowStatisticsEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view.GCubeCkanDataCatalogPanel;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.form.CreateDatasetForm;
import org.gcube.portlets_widgets.catalogue_sharing_widget.client.ShareCatalogueWidget;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class CkanEventHandlerManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Costantino Perciante costantino.perciante@isti.cnr.it Jun 10, 2016
 */
public class CkanEventHandlerManager {

	protected static final String WIDGET_CREATE_TITLE = "Publish Item";
	private HandlerManager eventBus = new HandlerManager(null);
	private GCubeCkanDataCatalogPanel panel;
	private String logoutFromCKANURL;

	/**
	 * Instantiates a new ckan event handler manager.
	 */
	public CkanEventHandlerManager() {
		bind();
	}

	/**
	 * Sets the panel.
	 *
	 * @param panel            the panel to set
	 */
	public void setPanel(GCubeCkanDataCatalogPanel panel) {

		this.panel = panel;
	}

	/**
	 * Bind.
	 */
	private void bind() {

		// bind on show home event
		eventBus.addHandler(ShowHomeEvent.TYPE, new ShowHomeEventHandler() {

			@Override
			public void onShowHome(ShowHomeEvent showHomeEvent) {
				String request = getCkanRequest(null, null);
				panel.instanceCkanFrame(request);

			}
		});
		// bind on insert metadata
		eventBus.addHandler(InsertMetadataEvent.TYPE, new InsertMetadataEventHandler() {

			@Override
			public void onInsertMetadata(InsertMetadataEvent loadSelecteReleaseEvent) {

				final Modal modal = new Modal(true, true);

				modal.setTitle(WIDGET_CREATE_TITLE);
				modal.addStyleName("insert-metadata-modal-style");
				modal.addStyleName("modal-top-custom");
				((Element)modal.getElement().getChildNodes().getItem(1)).addClassName("modal-body-custom");
				modal.add(new CreateDatasetForm(eventBus));
				modal.setCloseVisible(true);
				modal.show();

				// hide any popup panel opened
				modal.addHideHandler(new HideHandler() {

					@Override
					public void onHide(HideEvent hideEvent) {
						eventBus.fireEvent(new CloseCreationFormEvent());
					}
				});
			}
		});
		// bind on edit (TODO)
		eventBus.addHandler(EditMetadataEvent.TYPE, new EditMetadataEventHandler() {

			@Override
			public void onEditMetadata(EditMetadataEvent editMetadataEvent) {

				// TODO Auto-generated method stub
			}
		});

		// show datasets event
		eventBus.addHandler(
				ShowDatasetsEvent.TYPE, new ShowDatasetsEventHandler() {

					@Override
					public void onShowDatasets(
							ShowDatasetsEvent showUserDatasetsEvent) {
						String request = null;
						if(showUserDatasetsEvent.isOwnOnly())
							request = getCkanRequest("/dashboard/datasets", null);
						else
							request = getCkanRequest("/dataset", null);
						panel.instanceCkanFrame(request);
					}
				});
		eventBus.addHandler(
				ShowOrganizationsEvent.TYPE,
				new ShowOrganizationsEventHandler() {

					@Override
					public void onShowOrganizations(
							ShowOrganizationsEvent showUserDatasetsEvent) {

						if(showUserDatasetsEvent.isOwnOnly())
							panel.showOrganizations();
						else{
							String request = getCkanRequest("/organization", null);
							panel.instanceCkanFrame(request);
						}
					}
				});
		eventBus.addHandler(
				ShowGroupsEvent.TYPE, new ShowGroupsEventHandler() {

					@Override
					public void onShowGroups(ShowGroupsEvent showGroupsEvent) {
						//panel.instanceCkanFrame(request);
						if(showGroupsEvent.isOwnOnly())
							panel.showGroups();
						else{
							String request = getCkanRequest("/group", null);
							panel.instanceCkanFrame(request);
						}
					}
				});

		// show statistics event
		eventBus.addHandler(
				ShowStatisticsEvent.TYPE, new ShowStatisticsEventHandler() {

					@Override
					public void onShowStatistics(
							ShowStatisticsEvent showStatisticsEvent) {
						String request = getCkanRequest("/stats", null);
						panel.instanceCkanFrame(request);
					}
				});

		eventBus.addHandler(NotifyLogoutEvent.TYPE, new NotifyLogoutEventHandler() {

			@Override
			public void onLogout(NotifyLogoutEvent editMetadataEvent) {

				//				CKanLeaveFrame frame = new CKanLeaveFrame(GCubeCkanDataCatalog.CKAN_LOGUT_SERVICE);
				//				DOM.appendChild(RootPanel.getBodyElement(), frame.getElement());
			}
		});

		eventBus.addHandler(IFrameInstanciedEvent.TYPE, new IFrameInstanciedEventHandler() {

			@Override
			public void onNewInstance(IFrameInstanciedEvent iFrameInstanciedEvent) {

				panel.updateSize();
				if(logoutFromCKANURL==null)
					instanceLogoutSystem();
			}
		});

		eventBus.addHandler(ShowManageProductWidgetEvent.TYPE, new ShowManageProductWidgetEventHandler() {

			@Override
			public void onShowManageProductWidget(ShowManageProductWidgetEvent event) {
				new ManageProductWidget(event.getProductIdentifier());
			}
		});
		
		eventBus.addHandler(ShareLinkEvent.TYPE, new ShareLinkEventHandler() {
			
			@Override
			public void onShareLink(ShareLinkEvent event) {
				
				new ShareCatalogueWidget(event.getUuidItem());
				
			}
		});
	}

	/**
	 * Gets the ckan request.
	 *
	 * @param pathInfo the path info
	 * @param query the query
	 * @return the ckan request
	 */
	private String getCkanRequest(String pathInfo, String query){
		CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(panel.getBaseURLCKANConnector(),"");
		if(panel.getGcubeTokenValueToCKANConnector() != null) 
			ckan.addGubeToken(panel.getGcubeTokenValueToCKANConnector());
		pathInfo = CkanConnectorAccessPoint.checkNullString(pathInfo);
		query = CkanConnectorAccessPoint.checkNullString(query);
		ckan.addPathInfo(pathInfo);
		ckan.addQueryString(query);
		return ckan.buildURI();
	}

	/**
	 * Instance logout system.
	 */
	private void instanceLogoutSystem() {

		GCubeCkanDataCatalog.service.logoutFromCkanURL(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				logoutFromCKANURL = result;
				GWT.log("Loaded logout url: "+logoutFromCKANURL);
				performLogoutOnBrowserClosedEvent(logoutFromCKANURL);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});

	}

	/**
	 * Perform logout on browser closed event.
	 *
	 * @param logoutService the logout service
	 */
	private static native void performLogoutOnBrowserClosedEvent(String logoutService)/*-{

		var validNavigation = false;

		function wireUpEvents() {
			console.log("wireUpEvents");
			var dont_confirm_leave = 1; //set dont_confirm_leave to 1 when you want the user to be able to leave without confirmation
			var leave_message = 'You sure you want to leave?'

			function disconnect(e) {

				if (!validNavigation) {
					var logoutPerformed = false;

					var ifrm = $doc.createElement("iframe");
					ifrm.id = 'logout-iframe';
					ifrm.onload = function() {
						logoutPerformed = true;
						console.log("ifrm loaded exit is: " + exit);
					}
					ifrm.style.width = "1px";
					ifrm.style.height = "1px";
					ifrm.src = logoutService;
					$doc.body.appendChild(ifrm);

					//sleep 500ms in order to loasad disconnect response performed by IFrame
					function sleep(milliseconds) {
						var start = new Date().getTime();
						for (var i = 0; i < 1e7; i++) {
							if ((new Date().getTime() - start) > milliseconds || logoutPerformed) {
								break;
							}
						}
					}
					//sleep 500ms in order to have time to load disconnect response returned by IFrame
					sleep(500);

					if (dont_confirm_leave !== 1) {
						if (!e)
							e = window.event;
						//e.cancelBubble is supported by IE - this will kill the bubbling process.
						e.cancelBubble = true;
						e.returnValue = leave_message;
						//e.stopPropagation works in Firefox.
						if (e.stopPropagation) {
							e.stopPropagation();
							e.preventDefault();
						}
						//return works for Chrome and Safari
						return leave_message;
					}
				}
			}

			window.onbeforeunload = disconnect;

			// Attach the event keypress to exclude the F5 refresh
			$wnd.$(document).bind('keypress', function(e) {
				if (e.keyCode == 116) {
					validNavigation = true;
					console.log("keypress: " + validNavigation);
				}
			});

			// Attach the event click for all links in the page
			$wnd.$("a").bind("click", function() {
				validNavigation = true;
				console.log("click: " + validNavigation);
			});

			// Attach the event submit for all forms in the page
			$wnd.$("form").bind("submit", function() {
				validNavigation = true;
				console.log("form: " + validNavigation);
			});

			// Attach the event click for all inputs in the page
			$wnd.$("input[type=submit]").bind("click", function() {
				validNavigation = true;
				console.log("submit: " + validNavigation);
			});

		}

		// Wire up the events as soon as the DOM tree is ready
		$wnd.$(document).ready(function() {
			wireUpEvents();
		});

		//wireUpEvents();

	}-*/;


	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public HandlerManager getEventBus() {

		return eventBus;
	}


	/**
	 * Gets the logout from ckanurl.
	 *
	 * @return the logoutFromCKANURL
	 */
	public String getLogoutFromCKANURL() {

		return logoutFromCKANURL;
	}
}
