package org.gcube.portlets.user.td.sharewidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareTabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.sharewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.widgets.workspacesharingwidget.client.WorkspaceSmartSharingController;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.SmartShare;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TRShare {
	private TRId trId;

	private EventBus eventBus;
	private TabResource tabResource;
	private ShareTabResource shareInfo;

	public TRShare(TRId trId, EventBus eventBus) {
		this.trId = trId;
		this.eventBus = eventBus;

		retrieveInfo();

	}

	
	public void retrieveInfo() {
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(trId,
				new AsyncCallback<TabResource>() {

					public void onSuccess(TabResource result) {
						Log.info("Retrived TR: " + result.getTrId());
						tabResource = result;
						shareWindow();

					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());

							} else {
								Log.error("Error retrienving properties: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error",
										"Retrieving tabular resource info: "
												+ caught.getLocalizedMessage());

							}

						}
					}

				});
	}

	/**
	 * Call Window
	 */
	protected void shareWindow() {

		FileModel file = new FileModel(tabResource.getTrId()
				.getId(), tabResource.getName(), false);

		List<CredentialModel> listAlreadySharedContact = new ArrayList<CredentialModel>();
		for (Contacts contacts : tabResource.getContacts()) {
			CredentialModel cm = new CredentialModel(null, contacts.getLogin(),
					false);
			listAlreadySharedContact.add(cm);
		}

		WorkspaceSmartSharingController controller = new WorkspaceSmartSharingController(
				file, listAlreadySharedContact, false, true);

		final SmartShare sharingWindow = controller.getSharingDialog();

		sharingWindow.show();

		sharingWindow.getButtonById(Dialog.OK).addListener(Events.Select,
				new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {

						if (sharingWindow.isValidForm(true)) {
							shareCall(sharingWindow
									.getSharedListUsersCredential());

						}

					}
				});

	}

	protected void shareCall(List<CredentialModel> credentials) {
		ArrayList<Contacts> listContacts = new ArrayList<Contacts>();
		for (CredentialModel cred : credentials) {
			Contacts cont = new Contacts(cred.getId(), cred.getLogin(),
					cred.isGroup());
			listContacts.add(cont);
		}
		shareInfo=new ShareTabResource(tabResource,listContacts);

		TDGWTServiceAsync.INSTANCE.setShare(shareInfo,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.debug("Share Error: "
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error sharing tabular resource",
									"Error sharing tabular resource: "
											+ caught.getLocalizedMessage());
						}
					}

					public void onSuccess(Void result) {
						Log.debug("Tabular Resource Shared: "
								+ shareInfo.getTabResource().getTrId()
										.toString());
						UtilsGXT3.info("Shared", "Tabular Resource is shared");

						ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
						ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
								ChangeTableRequestType.SHARE, trId, why);
						eventBus.fireEvent(changeTableRequestEvent);

					}

				});

	}

	

}