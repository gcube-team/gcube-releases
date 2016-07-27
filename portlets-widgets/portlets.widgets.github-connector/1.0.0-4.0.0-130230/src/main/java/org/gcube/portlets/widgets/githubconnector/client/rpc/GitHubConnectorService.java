package org.gcube.portlets.widgets.githubconnector.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.widgets.githubconnector.shared.exception.ServiceException;
import org.gcube.portlets.widgets.githubconnector.shared.git.GitHubCloneSession;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.GitHubRepository;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredential;
import org.gcube.portlets.widgets.githubconnector.shared.session.UserInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
@RemoteServiceRelativePath("githubconnectorservice")
public interface GitHubConnectorService extends RemoteService {

	public UserInfo hello() throws ServiceException;

	public ArrayList<GitHubRepository> getRepositories(String repositoryOwner, GitHubCredential gitHubCredential) throws ServiceException;
	
	public void cloneRepository(GitHubCloneSession gitHubCloneSession) throws ServiceException;
	
	
}
