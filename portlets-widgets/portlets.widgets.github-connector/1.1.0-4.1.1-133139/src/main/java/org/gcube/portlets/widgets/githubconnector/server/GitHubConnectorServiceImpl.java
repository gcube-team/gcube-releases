package org.gcube.portlets.widgets.githubconnector.server;

import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.widgets.githubconnector.client.rpc.GitHubConnectorService;
import org.gcube.portlets.widgets.githubconnector.server.git.GitConnectorService;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ServiceException;
import org.gcube.portlets.widgets.githubconnector.shared.git.GitHubCloneSession;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.GitHubRepository;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredential;
import org.gcube.portlets.widgets.githubconnector.shared.session.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
@SuppressWarnings("serial")
public class GitHubConnectorServiceImpl extends RemoteServiceServlet implements
		GitHubConnectorService {
	
	private static Logger logger = LoggerFactory
			.getLogger(GitHubConnectorServiceImpl.class);

	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("GitHubConnectorServiceImpl started!");

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public UserInfo hello() throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			SessionUtil.getToken(aslSession);
			logger.debug("hello()");
			UserInfo userInfo = new UserInfo(aslSession.getUsername(),
					aslSession.getGroupId(), aslSession.getGroupName(),
					aslSession.getScope(), aslSession.getScopeName(),
					aslSession.getUserEmailAddress(),
					aslSession.getUserFullName());
			logger.debug("UserInfo: " + userInfo);
			return userInfo;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

	@Override
	public ArrayList<GitHubRepository> getRepositories(String repositoryOwner,
			GitHubCredential gitHubCredential) throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			SessionUtil.getToken(aslSession);
			logger.debug("getRepository(): " + gitHubCredential);
			GitConnectorService gitConnectorService = new GitConnectorService(
					aslSession.getUsername(), gitHubCredential);
			return gitConnectorService.getRepositories(repositoryOwner);
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void cloneRepository(GitHubCloneSession gitHubCloneSession)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			SessionUtil.getToken(aslSession);
			logger.debug("cloneRepository(): " + gitHubCloneSession);
			GitConnectorService gitConnectorService = new GitConnectorService(
					aslSession.getUsername(),
					gitHubCloneSession.getGitHubCredential());
			gitConnectorService.cloneRepository(gitHubCloneSession.getDestinationFolderId(),
					gitHubCloneSession.getRepositoryOwner(), gitHubCloneSession.getRepositoryName());
			return;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

}
