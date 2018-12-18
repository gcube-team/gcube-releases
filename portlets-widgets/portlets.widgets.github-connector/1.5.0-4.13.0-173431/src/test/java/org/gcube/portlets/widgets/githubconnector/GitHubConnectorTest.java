package org.gcube.portlets.widgets.githubconnector;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.gcube.portlets.widgets.githubconnector.server.git.GitConnectorService;
import org.gcube.portlets.widgets.githubconnector.shared.Constants;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ServiceException;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredentialAnonymous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GitHubConnectorTest extends TestCase {

	private static Logger logger = LoggerFactory
			.getLogger(GitHubConnectorTest.class);

	public void testExecuteProcess() {
		if (Constants.TEST_ENABLE) {
			executeProcess();
		} else {
			assertTrue(true);

		}
	}

	public void testGitHubConnectorService() {
		if (Constants.TEST_ENABLE) {
			GitConnectorService gitConnectorService = new GitConnectorService(
					Constants.DEFAULT_USER, new GitHubCredentialAnonymous());
			try {
				gitConnectorService
						.cloneRepository(Constants.DEFAULT_FOLDER_ID, Constants.DEFAULT_REPOSITORY_OWNER,
								Constants.DEFAULT_REPOSITORY_NAME);
			} catch (ServiceException e) {
				assertFalse(true);
				e.printStackTrace();
			}
			assertTrue(true);

		} else {
			assertTrue(true);

		}
	}

	private void executeProcess() {

		try {
			logger.debug("Connect to GitHub");
			// Basic authentication
			GitHubClient client = new GitHubClient();
			client.setCredentials("account", "password");

			/*
			 * GitHubRequest req = new GitHubRequest();
			 * req.setUri("git://github.com/jonan/flingbox.git"); GitHubResponse
			 * resp = client.get(req); logger.debug(resp.toString());
			 */

			/*
			 * RepositoryService service = new RepositoryService(); for
			 * (Repository repo : service.getRepositories("jonan")) {
			 */
			RepositoryService service = new RepositoryService();
			Repository repo = service.getRepository("jonan", "jonan.github.io");

			logger.debug("Repository: [Name=" + repo.getName() + ", Watcher="
					+ repo.getWatchers() + ", GitUrl=" + repo.getGitUrl() + "]");
			MilestoneService milestoneService = new MilestoneService(client);
			List<Milestone> listMilestone = milestoneService.getMilestones(
					repo, null);
			for (Milestone milestone : listMilestone) {
				logger.debug("Milestone: [Title=" + milestone.getTitle()
						+ ", Number=" + milestone.getNumber() + ", URL="
						+ milestone.getUrl() + "]");
			}
			ContentsService contentsService = new ContentsService(client);
			List<RepositoryContents> listRepositoryContents = contentsService
					.getContents(repo);
			for (RepositoryContents contents : listRepositoryContents) {
				logger.debug("Contents: [name=" + contents.getName()
						+ ", type=" + contents.getType() + ", encoding="
						+ contents.getEncoding() + ",  path="
						+ contents.getPath() + ", sha=" + contents.getSha()
						+ "]");
				DataService dataService = new DataService(client);
				if (contents.getName().compareTo("README.md") == 0) {
					Blob blob = dataService.getBlob(repo, contents.getSha());
					logger.debug("Blob: [encoding=" + blob.getEncoding()
							+ ", content=" + blob.getContent() + "]");
					if (blob.getEncoding().compareToIgnoreCase("base64") == 0) {
						byte[] content = Base64.decodeBase64(blob.getContent()
								.getBytes());
						logger.debug(new String(content));
					}
				}
			}

			// DeployKeyService deployKeyService=new DeployKeyService(client);
			/*
			 * PullRequestService pullRequetService=new
			 * PullRequestService(client); CommitService commitService=new
			 * CommitService(client); RepositoryCommit repoCommit=new
			 * RepositoryCommit(); CommitFile commitFile=new CommitFile();
			 */
			logger.debug("---------------------------------------------");

			// }

		} catch (Exception e) {
			logger.debug(e.getLocalizedMessage());
			e.printStackTrace();
			fail(e.getLocalizedMessage());

		}

	}

}
