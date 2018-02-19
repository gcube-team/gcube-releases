package org.gcube.portlets.user.statisticalalgorithmsimporter.client.create;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.gcube.portlets.widgets.githubconnector.client.util.GWTMessages;
import org.gcube.portlets.widgets.githubconnector.client.wizard.WizardCard;
import org.gcube.portlets.widgets.githubconnector.client.wizard.event.WizardEvent;
import org.gcube.portlets.widgets.githubconnector.client.wizard.event.WizardEventType;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectPanel;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ProjectFolderSelectionCard extends WizardCard {

	private ItemDescription newProjectFolder;

	public ProjectFolderSelectionCard() {
		super("Setup", "Select the project folder");
		try {
			WorkspaceExplorerSelectPanel wselectPanel = new WorkspaceExplorerSelectPanel("Select Project Folder", true);
			
			WorskpaceExplorerSelectNotificationListener handler = new WorskpaceExplorerSelectNotificationListener() {

				@Override
				public void onSelectedItem(Item item) {

					if (item.getType() == ItemType.FOLDER) {
						createNewProjectFolder(item);

					} else {
						newProjectFolder = null;
						GWTMessages.alert("Attention", "Select a valid project folder!", getZIndex());
					}

				}

				@Override
				public void onFailed(Throwable throwable) {
					Log.error("Error in create project: " + throwable.getLocalizedMessage(), throwable);
					showErrorAndHide("Error", throwable.getLocalizedMessage());
				}

				@Override
				public void onAborted() {

				}

				@Override
				public void onNotValidSelection() {
					newProjectFolder = null;
					GWTMessages.alert("Attention", "Select a valid project folder!", getZIndex());
				}
			};

			wselectPanel.addWorkspaceExplorerSelectNotificationListener(handler);
			SimplePanel mainPanel=new SimplePanel();
			mainPanel.setHeight("400px");
			mainPanel.setWidget(wselectPanel);
			setContent(mainPanel);
		} catch (Exception e) {
			GWT.log("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void createNewProjectFolder(Item item) {
		Log.debug("Create Project Item selected: " + item);
		newProjectFolder = new ItemDescription(item.getId(), item.getName(), item.getOwner(), item.getPath(),
				item.getType().name());
	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				checkData();
			}

		};

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					GWT.log("PreviousCard");
					getWizardWindow().setNextButtonToDefault();
					getWizardWindow().previousCard();
				} catch (Exception e) {
					GWT.log("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setNextButtonCommand(sayNextCard);

		getWizardWindow().setNextButtonToFinish();
		setEnableBackButton(true);
		setBackButtonVisible(true);
		setEnableNextButton(true);
		setNextButtonVisible(true);
		getWizardWindow().center();

	}

	private void checkData() {
		if (newProjectFolder != null) {
			ProjectCreateWizard wiz = (ProjectCreateWizard) getWizardWindow();
			wiz.getProjectCreateSession().setNewProjectFolder(newProjectFolder);
			goNext();

		} else {
			GWTMessages.alert("Attention", "Select a valid project folder!", getZIndex());
		}

	}

	private void goNext() {
		try {
			GWT.log("NextCard");
			WizardEvent event = new WizardEvent(WizardEventType.Completed);
			getWizardWindow().fireEvent(event);
			getWizardWindow().close(false);
		} catch (Exception e) {
			GWT.log("sayNextCard :" + e.getLocalizedMessage());
		}

	}

}
