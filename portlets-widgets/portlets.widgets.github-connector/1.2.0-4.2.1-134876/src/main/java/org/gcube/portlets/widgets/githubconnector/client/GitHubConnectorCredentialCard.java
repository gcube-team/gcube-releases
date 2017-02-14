package org.gcube.portlets.widgets.githubconnector.client;

import org.gcube.portlets.widgets.githubconnector.client.util.GWTMessages;
import org.gcube.portlets.widgets.githubconnector.client.wizard.WizardCard;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredentialAnonymous;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredentialLogin;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredentialOAuth2;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class GitHubConnectorCredentialCard extends WizardCard {
	private TabLayoutPanel tabPanel;
	private TextBox userName;
	private TextBox password;
	private TextBox token;
	private CheckBox anonymousCheck;

	// private DecoratorPanel decPanel;

	public GitHubConnectorCredentialCard() {
		super("Credential", "Select the credentials to use[OAuth2 for big repository]");
		tabPanel = new TabLayoutPanel(2.5, Unit.EM);
		tabPanel.setAnimationDuration(1000);
		tabPanel.setAnimationVertical(true);
		tabPanel.getElement().getStyle().setMarginBottom(10.0, Unit.PX);
		tabPanel.setHeight("120px");
		tabPanel.setWidth("265px");

		// Add Login Tab
		SimplePanel loginContainer = new SimplePanel();
		userName = new TextBox();
		userName.setWidth("166px");
		userName.setEnabled(false);

		password = new TextBox();
		password.setWidth("166px");
		password.setEnabled(false);

		anonymousCheck = new CheckBox();
		anonymousCheck.setValue(true);
		anonymousCheck.ensureDebugId("credentialCheckBoxAnonymous");
		anonymousCheck.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (anonymousCheck.getValue()) {
					userName.setEnabled(false);
					password.setEnabled(false);
					userName.setValue("");
					password.setValue("");
				} else {
					userName.setEnabled(true);
					password.setEnabled(true);
					userName.setValue("");
					password.setValue("");

				}

			}
		});

		FlexTable loginFlexTable = new FlexTable();
		loginFlexTable.setCellSpacing(16);
		loginFlexTable.setHTML(1, 0, "Anonymous:");
		loginFlexTable.setWidget(1, 1, anonymousCheck);

		loginFlexTable.setHTML(2, 0, "User:");
		loginFlexTable.setWidget(2, 1, userName);

		loginFlexTable.setHTML(3, 0, "Password:");
		loginFlexTable.setWidget(3, 1, password);

		loginContainer.add(loginFlexTable);
		tabPanel.add(loginContainer, "Login");

		// Add OAuth2 Tab
		SimplePanel oAuth2Container = new SimplePanel();
		token = new TextBox();
		token.setWidth("200px");

		FlexTable oAuth2FlexTable = new FlexTable();
		oAuth2FlexTable.setCellSpacing(16);
		oAuth2FlexTable.setHTML(1, 0, "Token:");
		oAuth2FlexTable.setWidget(1, 1, token);

		oAuth2Container.add(oAuth2FlexTable);
		tabPanel.add(oAuth2Container, "OAuth2");

		tabPanel.selectTab(0);
		tabPanel.ensureDebugId("credentialTabPanel");

		setContent(tabPanel);

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
					getWizardWindow().previousCard();
				} catch (Exception e) {
					GWT.log("sayPreviousCard :" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setNextButtonCommand(sayNextCard);

		setEnableBackButton(false);
		setBackButtonVisible(false);
		setEnableNextButton(true);
		setNextButtonVisible(true);
	}

	private void checkData() {
		int selectedIndex = tabPanel.getSelectedIndex();
		if (selectedIndex > -1) {
			GWT.log("Selected Tab:" + selectedIndex);
			if (selectedIndex == 0) {
				if (anonymousCheck.getValue()) {
					GitHubConnectorWizard wiz = (GitHubConnectorWizard) getWizardWindow();
					wiz.getGitHubCloneSession().setGitHubCredential(
							new GitHubCredentialAnonymous());

					goNext();
				} else {
					String userN = userName.getValue();
					if (userN == null || userN.isEmpty()) {
						GWTMessages.alert("Attention", "Enter a user name!",getZIndex());
					} else {
						String pwd = password.getValue();
						if (pwd == null || pwd.isEmpty()) {
							GWTMessages.alert("Attention",
									"Enter a user password!", getZIndex());
						} else {
							GitHubConnectorWizard wiz = (GitHubConnectorWizard) getWizardWindow();
							wiz.getGitHubCloneSession().setGitHubCredential(
									new GitHubCredentialLogin(userN, pwd));
							goNext();
						}

					}

				}
			} else {
				if (selectedIndex == 1) {
					String tk = token.getValue();
					if (tk == null || tk.isEmpty()) {
						GWTMessages.alert("Attention", "Enter a token!",getZIndex());
					} else {
						GitHubConnectorWizard wiz = (GitHubConnectorWizard) getWizardWindow();
						wiz.getGitHubCloneSession().setGitHubCredential(
								new GitHubCredentialOAuth2(tk));
						goNext();
					}
				} else {
					GWTMessages.alert("Attention", "Select a valid tab!",getZIndex());
				}
			}

		}

	}

	private void goNext() {
		try {
			GWT.log("NextCard: GitHubConnectorRepositorySelectionCard");
			GitHubConnectorRepositorySelectionCard card = new GitHubConnectorRepositorySelectionCard();
			getWizardWindow().addCard(card);
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			GWT.log("sayNextCard :" + e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

}
