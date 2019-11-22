/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectPanel;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * 
 * 
 *        
 * 
 */
public class CSVWorkSpaceSelectionCard extends WizardCard {
	private static CSVImportWizardTDMessages msgs= GWT.create(CSVImportWizardTDMessages.class);
	
	private CSVImportSession importSession;
	private CSVWorkSpaceSelectionCard thisCard;

	private VerticalLayoutContainer p;
	private WorkspaceExplorerSelectPanel wpanel;

	private CommonMessages msgsCommon;

	public CSVWorkSpaceSelectionCard(final CSVImportSession importSession) {
		super(msgs.csvImportFromWorkspace(), "");
		this.importSession = importSession;
		thisCard = this;
		initMessages();
		
		p = new VerticalLayoutContainer();

		Log.debug("Set Workspace Panel");
		List<ItemType> selectableTypes = new ArrayList<ItemType>();
		selectableTypes.add(ItemType.EXTERNAL_FILE);
		List<ItemType> showableTypes = new ArrayList<ItemType>();
		showableTypes.addAll(Arrays.asList(ItemType.values()));

		List<String> allowedMimeTypes = Arrays.asList("text/csv",
				"application/zip", "application/x-zip",
				"application/x-zip-compressed", "application/octet",
				"application/octet-stream");
		List<String> allowedFileExtensions = Arrays.asList("csv", "zip");

		FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes,
				allowedFileExtensions, new HashMap<String, String>());

		wpanel = new WorkspaceExplorerSelectPanel(msgs.workspaceSelection(), filterCriteria, selectableTypes);

		WorskpaceExplorerSelectNotificationListener handler = new WorskpaceExplorerSelectNotificationListener() {

			@Override
			public void onSelectedItem(Item item) {
				Log.debug("Selected Item:" + item);
				if (item.getType() != ItemType.FOLDER&&item.getType() != ItemType.PRIVATE_FOLDER
						&&item.getType() != ItemType.SHARED_FOLDER&&item.getType() != ItemType.VRE_FOLDER) {
					String filename = item.getName();
					if (filename != null && !filename.isEmpty()) {
						Log.debug("Item name: " + filename);
						Log.debug("Item path: " + item.getPath());

						importSession.setItemId(item.getId());
						getWizardWindow().setEnableNextButton(true);

					} else {
						Log.debug("Item name null or empty");
						getWizardWindow().setEnableNextButton(false);
					}

				} else {
					Log.debug("Item type:" + item.getType());
					getWizardWindow().setEnableNextButton(false);
				}

			}

			@Override
			public void onFailed(Throwable throwable) {
				Log.error("Error in workspaceExplorer: "
						+ throwable.getLocalizedMessage());
				throwable.printStackTrace();
			}

			@Override
			public void onAborted() {
				Log.debug("WorkspaceExplorer Aborted");

			}

			@Override
			public void onNotValidSelection() {
				importSession.setItemId(null);
				getWizardWindow().setEnableNextButton(false);

			}
		};

		wpanel.addWorkspaceExplorerSelectNotificationListener(handler);

		p.add(wpanel, new VerticalLayoutData(1, 1, new Margins(0)));
		setCenterWidget(p, new MarginData(0));

	}
	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
	}

	@Override
	public void setup() {
		Log.debug("CSVWorkSpaceSelectionCard Call Setup ");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("CSVWorkSpaceSelectionCard Call sayNextCard wpanel:"
						+ wpanel);
				getWizardWindow().setEnableNextButton(false);
				getWizardWindow().setEnableBackButton(false);
				getFileFromWorkspace();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.debug("Remove CSVWorkSpaceSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(false);
		setNextButtonVisible(true);
		setBackButtonVisible(true);

	}

	protected void getFileFromWorkspace() {
		TDGWTServiceAsync tdGwtServiceAsync = TDGWTServiceAsync.INSTANCE;
		tdGwtServiceAsync.getFileFromWorkspace(importSession,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							thisCard.showErrorAndHide(
									msgsCommon.error(),
									msgs.errorRetrievingTheFileFromWorkspace(),
									caught.getLocalizedMessage(), caught);
						}
					}

					public void onSuccess(Void result) {
						goForward();

					}

				});

	}

	protected void goForward() {
		CSVConfigCard csvConfigCard = new CSVConfigCard(importSession);
		getWizardWindow().addCard(csvConfigCard);
		Log.info("NextCard CSVConfigCard");
		getWizardWindow().nextCard();

	}

}
