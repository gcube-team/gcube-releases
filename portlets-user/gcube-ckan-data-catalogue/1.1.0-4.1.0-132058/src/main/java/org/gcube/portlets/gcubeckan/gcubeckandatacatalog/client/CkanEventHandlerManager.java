
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.EditMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.EditMetadataEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.IFrameInstanciedEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.IFrameInstanciedEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.InsertMetadataEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.InsertMetadataEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.NotifyLogoutEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.NotifyLogoutEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowHomeEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowHomeEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowStatisticsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowStatisticsEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowUserDatasetsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowUserDatasetsEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowUserGroupsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowUserGroupsEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowUserOrganizationsEvent;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event.ShowUserOrganizationsEventHandler;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view.GCubeCkanDataCatalogPanel;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.CreateDatasetForm;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Costantino Perciante costantino.perciante@isti.cnr.it Jun 10, 2016
 */
public class CkanEventHandlerManager {

	private HandlerManager eventBus = new HandlerManager(null);
	private GCubeCkanDataCatalogPanel panel;

	/**
	 *
	 */
	public CkanEventHandlerManager() {
		bind();
	}

	/**
	 * @param panel
	 *            the panel to set
	 */
	public void setPanel(GCubeCkanDataCatalogPanel panel) {

		this.panel = panel;
	}

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

				modal.setTitle("Publish Product");
				modal.addStyleName("insert-metadata-modal-style");
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
		eventBus.addHandler(
				EditMetadataEvent.TYPE, new EditMetadataEventHandler() {

					@Override
					public void onEditMetadata(EditMetadataEvent editMetadataEvent) {

						// TODO Auto-generated method stub
					}
				});
		// show datasets event
		eventBus.addHandler(
				ShowUserDatasetsEvent.TYPE, new ShowUserDatasetsEventHandler() {

					@Override
					public void onShowDatasets(
							ShowUserDatasetsEvent showUserDatasetsEvent) {
						String request = getCkanRequest("/dashboard/datasets", null);
						panel.instanceCkanFrame(request);
					}
				});
		eventBus.addHandler(
				ShowUserOrganizationsEvent.TYPE,
				new ShowUserOrganizationsEventHandler() {

					@Override
					public void onShowOrganizations(
							ShowUserOrganizationsEvent showUserDatasetsEvent) {

						panel.showOrganizations();

					}
				});
		eventBus.addHandler(
				ShowUserGroupsEvent.TYPE, new ShowUserGroupsEventHandler() {

					@Override
					public void onShowGroups(ShowUserGroupsEvent showUserDatasetsEvent) {
						String request = getCkanRequest("/dashboard/groups", null);
						panel.instanceCkanFrame(request);
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
			public void onNewInstance(IFrameInstanciedEvent iFrameInstanciedEent) {

				panel.updateSize();
			}
		});
	}

	private String getCkanRequest(String pathInfo, String query){
		CkanConnectorAccessPoint ckan = new CkanConnectorAccessPoint(panel.getBaseURLCKANConnector(),"");
		ckan.addGubeToken(panel.getGcubeTokenValueToCKANConnector());
		pathInfo = CkanConnectorAccessPoint.checkNullString(pathInfo);
		query = CkanConnectorAccessPoint.checkNullString(query);
		ckan.addPathInfo(pathInfo);
		ckan.addQueryString(query);
		return ckan.buildURI();
	}

	private static native void logutWindow(String uri, boolean timeout)/*-{
		var newWindow = window.open(uri, '_blank', 'width=50,height=50');
		newWindow.onload = function() {
  			setTimeout();
		};
		setTimeout(function() {
      		newWindow.close();
    	}, 500);
    	return false;
	}-*/;


	/**
	 * @return
	 */
	public HandlerManager getEventBus() {

		return eventBus;
	}
}
