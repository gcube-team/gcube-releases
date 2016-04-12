package org.gcube.portlets.user.td.sharewidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;
import org.gcube.portlets.user.td.gwtservice.shared.share.ShareRule;
import org.gcube.portlets.user.td.sharewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
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
public class RuleShare {

	private EventBus eventBus;
	private RuleDescriptionData ruleDescriptionData;
	private ShareRule shareRule;

	public RuleShare(RuleDescriptionData ruleData, EventBus eventBus) {
		this.ruleDescriptionData=ruleData;
		this.eventBus = eventBus;

		shareWindow(); 
	}

	
	

	/**
	 * Call Window
	 */
	protected void shareWindow() {

		FileModel file = new FileModel(String.valueOf(ruleDescriptionData.getId()), ruleDescriptionData.getName(), false);

		List<CredentialModel> listAlreadySharedContact = new ArrayList<CredentialModel>();
		for (Contacts contacts : ruleDescriptionData.getContacts()) {
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
		shareRule=new ShareRule(ruleDescriptionData,listContacts);

		TDGWTServiceAsync.INSTANCE.setShareRule(shareRule,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.debug("Share Error: "
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error sharing rule",
									"Error sharing rule: "
											+ caught.getLocalizedMessage());
						}
					}

					public void onSuccess(Void result) {
						Log.debug("Rule Shared: "
								+ shareRule.getRuleDescriptionData().getId());
						UtilsGXT3.info("Shared", "Rule is shared");

					}

				});

	}

	

}