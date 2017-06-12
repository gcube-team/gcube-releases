package org.gcube.portlets.widgets.githubconnector.client;

import org.gcube.portlets.widgets.githubconnector.client.wizard.WizardWindow;
import org.gcube.portlets.widgets.githubconnector.shared.git.GitHubCloneSession;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GitHubConnectorWizard extends WizardWindow {

	private GitHubCloneSession gitHubCloneSession;

	public GitHubConnectorWizard(String destinationFolderId) {
		super("GitHub Connector");
		GWT.log("GitHubConnectorWizard: " + destinationFolderId);
		checkSession();
		gitHubCloneSession = new GitHubCloneSession(destinationFolderId);
		create();
	}

	private void create() {
		GitHubConnectorCredentialCard CredentialCard = new GitHubConnectorCredentialCard();
		addCard(CredentialCard);
		CredentialCard.setup();
		
	}

	public GitHubCloneSession getGitHubCloneSession() {
		return gitHubCloneSession;
	}
	
	private void checkSession() {
		// if you do not need to something when the session expire
		//CheckSession.getInstance().startPolling();
	}

	private void sessionExpiredShow() {
		//CheckSession.showLogoutDialog();
	}

	public void sessionExpiredShowDelayed() {
		Timer timeoutTimer = new Timer() {
			public void run() {
				sessionExpiredShow();

			}
		};
		int TIMEOUT = 3; // 3 second timeout

		timeoutTimer.schedule(TIMEOUT * 1000); // timeout is in milliseconds
	}

	

}
