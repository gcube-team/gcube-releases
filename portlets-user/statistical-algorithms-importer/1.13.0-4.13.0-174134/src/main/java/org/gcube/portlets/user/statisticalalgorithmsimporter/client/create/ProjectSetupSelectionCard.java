package org.gcube.portlets.user.statisticalalgorithmsimporter.client.create;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterServiceAsync;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.ProjectSetup;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.SAIDescriptor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterSessionExpiredException;
import org.gcube.portlets.widgets.githubconnector.client.resource.GCResources;
import org.gcube.portlets.widgets.githubconnector.client.util.GWTMessages;
import org.gcube.portlets.widgets.githubconnector.client.util.WaitDialog;
import org.gcube.portlets.widgets.githubconnector.client.wizard.WizardCard;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ProjectSetupSelectionCard extends WizardCard {

	private SAIDescriptor saiDescription;
	private ListBox projectSetupListBox;

	// private DecoratorPanel decPanel;

	public ProjectSetupSelectionCard() {
		super("Setup", "Configure the project");
		create();

	}

	private void create() {
		try {

			SimplePanel projectSetupSelectionPanel = new SimplePanel();

			projectSetupListBox = new ListBox();
			projectSetupListBox.setWidth("176px");
			projectSetupListBox.ensureDebugId("projectSetupListBox");
			projectSetupListBox.setEnabled(false);

			// Form
			FlexTable layout = new FlexTable();
			layout.setCellSpacing(10);

			FlexTable loginFlexTable = new FlexTable();
			loginFlexTable.setStyleName(GCResources.INSTANCE.wizardCSS().getCardPanelContent());
			loginFlexTable.setCellSpacing(16);

			loginFlexTable.setHTML(1, 0, "Type:");
			loginFlexTable.setWidget(1, 1, projectSetupListBox);

			projectSetupSelectionPanel.add(loginFlexTable);
			setContent(projectSetupSelectionPanel);

			Scheduler.get().scheduleDeferred(new Command() {
				public void execute() {
					init();
				}
			});

		} catch (Exception e) {
			GWT.log("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void init() {
		final WaitDialog waitDialog = new WaitDialog(getZIndex());
		waitDialog.show();

		StatAlgoImporterServiceAsync.INSTANCE.getSAIDescripor(new AsyncCallback<SAIDescriptor>() {

			@Override
			public void onFailure(Throwable caught) {
				waitDialog.hide();
				GWT.log("Error retrieving repositories: " + caught.getMessage());
				if (caught instanceof StatAlgoImporterSessionExpiredException) {
					showErrorAndHide("Error", "Expired Session");

				} else {
					showErrorAndHide("Error", "Retrieving repositories: " + caught.getLocalizedMessage());
				}

			}

			@Override
			public void onSuccess(SAIDescriptor saiDesc) {
				waitDialog.hide();
				GWT.log("SAI Descriptor: " + saiDesc);
				saiDescription = saiDesc;
				for (ProjectSetup projectSetup : saiDescription.getAvailableProjectConfigurations()) {
					projectSetupListBox.addItem(projectSetup.getLanguage());
				}
				projectSetupListBox.setEnabled(true);
				

			}

		});

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

		setEnableBackButton(false);
		setBackButtonVisible(false);
		setEnableNextButton(true);
		setNextButtonVisible(true);
		getWizardWindow().center();

	}

	private void checkData() {
		int selected = projectSetupListBox.getSelectedIndex();
		if (selected >= 0) {
			String language = projectSetupListBox.getValue(selected);
			if (language == null || language.isEmpty()) {
				GWTMessages.alert("Attention", "Select a type!", getZIndex());
			} else {
				ProjectSetup selectedSetup = null;
				ProjectCreateWizard wiz = (ProjectCreateWizard) getWizardWindow();
				for (ProjectSetup pSetup : saiDescription.getAvailableProjectConfigurations()) {
					if (pSetup.getLanguage().compareTo(language) == 0) {
						selectedSetup = pSetup;
						break;
					}
				}
				if (selectedSetup != null) {
					wiz.getProjectCreateSession().setProjectSetup(selectedSetup);
					goNext();
				} else {
					GWTMessages.alert("Attention", "Select a type!", getZIndex());
				}
			}
		} else {
			GWTMessages.alert("Attention", "Select a type!", getZIndex());
		}

	}

	private void goNext() {
		try {
			GWT.log("NextCard: ProjectFolderSelectionCard");
			ProjectFolderSelectionCard card = new ProjectFolderSelectionCard();
			getWizardWindow().addCard(card);
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			GWT.log("sayNextCard :" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

}
