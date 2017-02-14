package org.gcube.portlets.widgets.githubconnector.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.widgets.githubconnector.shared.git.GitHubCloneSession;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.GitHubRepository;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredential;
import org.gcube.portlets.widgets.githubconnector.shared.session.UserInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface GitHubConnectorServiceAsync {

	public static GitHubConnectorServiceAsync INSTANCE = (GitHubConnectorServiceAsync) GWT
			.create(GitHubConnectorService.class);

	void hello(AsyncCallback<UserInfo> callback);

	void getRepositories(String repositoryOwner,
			GitHubCredential gitHubCredential,
			AsyncCallback<ArrayList<GitHubRepository>> callback);

	void cloneRepository(GitHubCloneSession gitHubCloneSession,
			AsyncCallback<Void> callback);

}
