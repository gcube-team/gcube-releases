package org.gcube.portlets.user.td.sdmxexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TableType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 * 
 */
public class SDMXExportWizardTD extends WizardWindow {

	protected SDMXExportSession exportSession;

	/**
	 * 
	 * 
	 * @param title
	 *            Title
	 * @param eventBus
	 *            Event bus
	 */
	public SDMXExportWizardTD(String title, final EventBus eventBus) {
		super(title, eventBus);
		setWidth(550);
		setHeight(520);

		exportSession = new SDMXExportSession();

		final AutoProgressMessageBox box = new AutoProgressMessageBox("Wait", "Retrieving Information, please wait...");
		box.setProgressText("Retrieving...");
		box.auto();
		box.show();

		retrieveTabularDataInfo(box);

		SDMXRegistrySelectionCard sdmxRegistrySelectionCard = new SDMXRegistrySelectionCard(exportSession);
		addCard(sdmxRegistrySelectionCard);
		sdmxRegistrySelectionCard.setup();

	}

	private void retrieveTabularDataInfo(final AutoProgressMessageBox box) {
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(new AsyncCallback<TabResource>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						showErrorAndHide("Error Locked", caught.getLocalizedMessage(), "", caught);
					} else {
						Log.error(
								"No Tabular Resource Information retrived from server " + caught.getLocalizedMessage());
						box.hide();
						showErrorAndHide("Error", "Error retrieving tabular resource information: ",
								caught.getLocalizedMessage(), caught);
					}
				}
			}

			public void onSuccess(TabResource result) {
				Log.debug("Tabular Resource Information retrived");
				exportSession.setTabResource(result);
				exportSession.setExportType(TableType.getColumnDataTypeFromId(result.getTableTypeName()));
				box.hide();

			}
		});
	}

}