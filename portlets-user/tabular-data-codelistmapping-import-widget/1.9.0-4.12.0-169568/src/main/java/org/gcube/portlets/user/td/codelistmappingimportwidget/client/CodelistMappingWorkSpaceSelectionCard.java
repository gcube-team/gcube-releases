/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * 
 * @author Giancarlo Panichi
 *         
 * 
 */
public class CodelistMappingWorkSpaceSelectionCard extends WizardCard {
	private static CodelistMappingMessages msgs = GWT
			.create(CodelistMappingMessages.class);
	private CommonMessages msgsCommon;

	private CodelistMappingSession codelistMappingSession;
	private CodelistMappingWorkSpaceSelectionCard thisCard;

	private VerticalLayoutContainer p;
	private WorkspaceExplorerSelectPanel wpanel;

	public CodelistMappingWorkSpaceSelectionCard(
			final CodelistMappingSession codelistMappingSession) {
		super(msgs.codelistMappingWorkSpaceSelectionCardHead(), "");
		this.codelistMappingSession = codelistMappingSession;
		thisCard = this;
		initMessages();
		create();
	}

	protected void initMessages() {
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void create() {
		p = new VerticalLayoutContainer();

		Log.debug("Set Workspace Panel");
		List<ItemType> selectableTypes = new ArrayList<ItemType>();
		selectableTypes.add(ItemType.EXTERNAL_FILE);
		List<ItemType> showableTypes = new ArrayList<ItemType>();
		showableTypes.addAll(Arrays.asList(ItemType.values()));

		List<String> allowedMimeTypes = Arrays.asList("application/xml",
				"application/zip", "application/x-zip",
				"application/x-zip-compressed", "application/octet",
				"application/octet-stream");
		List<String> allowedFileExtensions = Arrays.asList("xml", "zip");

		FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes,
				allowedFileExtensions, new HashMap<String, String>());

		wpanel = new WorkspaceExplorerSelectPanel(
				msgs.workspaceExplorerSelectPanelHead(), filterCriteria, selectableTypes);

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

						codelistMappingSession.setItemId(item.getId());
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
				codelistMappingSession.setItemId(null);
				getWizardWindow().setEnableNextButton(false);

			}
		};

		wpanel.addWorkspaceExplorerSelectNotificationListener(handler);

		p.add(wpanel, new VerticalLayoutData(1, 1, new Margins(0, 0, 0, 44)));

		setContent(p);
	}

	@Override
	public void setup() {
		Log.debug("CodelistMappingWorkSpaceSelectionCard Call Setup ");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("CodelistMappingWorkSpaceSelectionCard Call sayNextCard wpanel:"
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
					Log.debug("Remove CodelistMappingWorkSpaceSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		setEnableNextButton(false);
		setNextButtonVisible(true);
		setEnableBackButton(true);
		setBackButtonVisible(true);

	}

	protected void getFileFromWorkspace() {
		TDGWTServiceAsync tdGwtServiceAsync = TDGWTServiceAsync.INSTANCE;
		tdGwtServiceAsync.getFileFromWorkspace(codelistMappingSession,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								showErrorAndHide(msgsCommon.errorLocked(),
										caught.getLocalizedMessage(), "",
										caught);
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									showErrorAndHide(msgsCommon.errorFinal(),
											caught.getLocalizedMessage(), "",
											caught);
								} else {
									showErrorAndHide(
											msgsCommon.error(),
											msgs.errorRetrievingTheFileFromWorkspaceFixed(),
											caught.getLocalizedMessage(),
											caught);
								}
							}
						}
					}

					public void onSuccess(Void result) {
						goNext();

					}

				});

	}

	protected void goNext() {
		try {
			TabResourcesSelectionCard tabResourceSelectionCard = new TabResourcesSelectionCard(
					codelistMappingSession);
			getWizardWindow().addCard(tabResourceSelectionCard);
			Log.info("NextCard TabResourceSelectionCard");
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			Log.error("goNext: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

}
