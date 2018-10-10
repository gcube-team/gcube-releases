package org.gcube.portlets.user.statisticalalgorithmsimporter.client.create;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectCreateSession;
import org.gcube.portlets.widgets.githubconnector.client.wizard.WizardWindow;

import com.google.gwt.core.client.GWT;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ProjectCreateWizard extends WizardWindow {

	private ProjectCreateSession projectCreateSession;

	public ProjectCreateWizard() {
		super("Project Create");
		GWT.log("ProjectCreateWizard");
	
		projectCreateSession = new ProjectCreateSession();
		create();
	}

	private void create() {
		ProjectSetupSelectionCard CredentialCard = new ProjectSetupSelectionCard();
		addCard(CredentialCard);
		CredentialCard.setup();
		
	}

	public ProjectCreateSession getProjectCreateSession() {
		return projectCreateSession;
	}

	
	

}
