package org.gcube.portlets.widgets.githubconnector.client;

import java.util.ArrayList;

import org.gcube.portlets.widgets.githubconnector.client.resource.GCResources;
import org.gcube.portlets.widgets.githubconnector.client.rpc.GitHubConnectorServiceAsync;
import org.gcube.portlets.widgets.githubconnector.client.util.GWTMessages;
import org.gcube.portlets.widgets.githubconnector.client.util.WaitDialog;
import org.gcube.portlets.widgets.githubconnector.client.wizard.WizardCard;
import org.gcube.portlets.widgets.githubconnector.client.wizard.event.WizardEvent;
import org.gcube.portlets.widgets.githubconnector.client.wizard.event.WizardEventType;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ExpiredSessionServiceException;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.GitHubRepository;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class GitHubConnectorRepositorySelectionCard extends WizardCard {

	private TextBox repositoryOwner;
	private ListBox repositoryName;

	// private DecoratorPanel decPanel;

	public GitHubConnectorRepositorySelectionCard() {
		super("Repository", "Enter a owner for select repository");
		try {
			SimplePanel repositorySelectionPanel = new SimplePanel();

			repositoryOwner = new TextBox();

			repositoryName = new ListBox();
			repositoryName.setWidth("176px");
			repositoryName.setEnabled(false);
			repositoryName.ensureDebugId("repositoryNameList");

			repositorySelectionPanel.setStyleName(GCResources.INSTANCE
					.wizardCSS().getCardPanel());

			ClickHandler searchRepositoryClickHandler = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					retrieveRepositories();

				}
			};
			PushButton searchRepository = new PushButton(new Image(
					GCResources.INSTANCE.search16()),
					searchRepositoryClickHandler);
			searchRepository.getElement().getStyle().setMargin(3, Unit.PX);

			// Form
			FlexTable layout = new FlexTable();
			layout.setCellSpacing(10);

			FlexTable loginFlexTable = new FlexTable();
			loginFlexTable.setStyleName(GCResources.INSTANCE.wizardCSS()
					.getCardPanelContent());
			loginFlexTable.setCellSpacing(16);
			FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();

			loginFlexTable.setHTML(1, 0, "Owner:");
			loginFlexTable.setWidget(1, 1, repositoryOwner);
			loginFlexTable.setWidget(1, 2, searchRepository);

			loginFlexTable.setHTML(2, 0, "Name:");
			loginFlexTable.setWidget(2, 1, repositoryName);
			cellFormatter.setColSpan(2, 1, 2);

			repositorySelectionPanel.add(loginFlexTable);
			setContent(repositorySelectionPanel);
		} catch (Exception e) {
			GWT.log("Error: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void retrieveRepositories() {
		String repOwner = repositoryOwner.getValue();
		if (repOwner == null || repOwner.isEmpty()) {
			GWTMessages.alert("Attention", "Enter a owner!",getZIndex());
		} else {
			final WaitDialog waitDialog = new WaitDialog(getZIndex());
			waitDialog.show();

			final GitHubConnectorWizard wiz = (GitHubConnectorWizard) getWizardWindow();
			GitHubConnectorServiceAsync.INSTANCE.getRepositories(repOwner, wiz
					.getGitHubCloneSession().getGitHubCredential(),
					new AsyncCallback<ArrayList<GitHubRepository>>() {

						@Override
						public void onFailure(Throwable caught) {
							waitDialog.hide();
							GWT.log("Error retrieving repositories: "
									+ caught.getMessage());
							if (caught instanceof ExpiredSessionServiceException) {
								showErrorAndHide("Error", "Expired Session");
								wiz.sessionExpiredShowDelayed();

							} else {
								showErrorAndHide(
										"Error",
										"Retrieving repositories: "
												+ caught.getLocalizedMessage());
							}

						}

						@Override
						public void onSuccess(
								ArrayList<GitHubRepository> repositories) {
							waitDialog.hide();
							repositoryName.clear();
							for (GitHubRepository repository : repositories) {
								repositoryName.addItem(repository.getName());
							}
							repositoryName.setEnabled(true);
						}
					});

		}

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

	}

	private void checkData() {
		String repoOwner = repositoryOwner.getValue();
		if (repoOwner == null || repoOwner.isEmpty()) {
			GWTMessages.alert("Attention", "Enter a repository owner!",getZIndex());
		} else {
			int selected = repositoryName.getSelectedIndex();
			if (selected >= 0) {
				String repoName = repositoryName.getValue(selected);
				if (repoName == null || repoName.isEmpty()) {
					GWTMessages.alert("Attention", "Select a repository name!",getZIndex());
				} else {
					GitHubConnectorWizard wiz = (GitHubConnectorWizard) getWizardWindow();
					wiz.getGitHubCloneSession().setRepositoryName(repoName);
					wiz.getGitHubCloneSession().setRepositoryOwner(repoOwner);
					callCloneForRepository();
				}
			} else {
				GWTMessages.alert("Attention", "Select a repository name!",getZIndex());
			}
		}
	}

	private void callCloneForRepository() {
		final WaitDialog waitDialog = new WaitDialog(getZIndex());
		waitDialog.show();

		final GitHubConnectorWizard wiz = (GitHubConnectorWizard) getWizardWindow();
		GitHubConnectorServiceAsync.INSTANCE.cloneRepository(
				wiz.getGitHubCloneSession(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						waitDialog.hide();
						GWT.log("Error in clone repository: "
								+ caught.getMessage());
						if (caught instanceof ExpiredSessionServiceException) {
							showErrorAndHide("Error", "Expired Session");
							wiz.sessionExpiredShowDelayed();

						} else {
							showErrorAndHide("Error", "In clone repository: "
									+ caught.getLocalizedMessage());
						}

					}

					@Override
					public void onSuccess(Void result) {
						waitDialog.hide();
						goNext();

					}
				});
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
