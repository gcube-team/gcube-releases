package org.gcube.portlets.widgets.githubconnector.server.git;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.gcube.portlets.widgets.githubconnector.server.storage.StorageUtil;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ServiceException;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.GitHubRepository;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredential;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredentialLogin;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.credential.GitHubCredentialOAuth2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GitConnectorService {
	private static Logger logger = LoggerFactory
			.getLogger(GitConnectorService.class);

	private GitHubClient gitHubClient;
	private String userName;
	
	/**
	 * 
	 * @param userName user name
	 * @param gitHubCredential github credential 
	 */
	public GitConnectorService(String userName, GitHubCredential gitHubCredential) {
		this.userName = userName;
		if(gitHubCredential!=null&& gitHubCredential.getType()!=null){
			if(gitHubCredential instanceof GitHubCredentialLogin){
				GitHubCredentialLogin gitHubCredentialLogin=(GitHubCredentialLogin) gitHubCredential;
				gitHubClient = new GitHubClient();
				gitHubClient.setCredentials(gitHubCredentialLogin.getUser()
						,gitHubCredentialLogin.getPassword());
			} else {
				if(gitHubCredential instanceof GitHubCredentialOAuth2){
					GitHubCredentialOAuth2 gitHubCredentialOAuth2=(GitHubCredentialOAuth2) gitHubCredential;
					gitHubClient = new GitHubClient();
					gitHubClient.setOAuth2Token(gitHubCredentialOAuth2.getToken());
				} else {
					
				}
			}
		}
	}

	public ArrayList<GitHubRepository> getRepositories(String repositoryOwner)
			throws ServiceException {
		try {
			RepositoryService service;
			if (gitHubClient == null) {
				service = new RepositoryService();
			} else {
				service = new RepositoryService(gitHubClient);
			}

			List<Repository> repositories = service
					.getRepositories(repositoryOwner);
			ArrayList<GitHubRepository> gitHubRepositories = GitHubRepositoryBuilder
					.build(repositories);
			logger.debug("Repositories: "+gitHubRepositories);
			return gitHubRepositories;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	public void cloneRepository(String destinationFolderId,
			String repositoryOwner, String repositoryName)
			throws ServiceException {
		try {

			RepositoryService service;
			if (gitHubClient == null) {
				service = new RepositoryService();
			} else {
				service = new RepositoryService(gitHubClient);
			}
			Repository repository = service.getRepository(repositoryOwner,
					repositoryName);

			logger.debug("Repository: [Name=" + repository.getName()
					+ ", Watcher=" + repository.getWatchers() + ", GitUrl="
					+ repository.getGitUrl() + "]");
			createContent(destinationFolderId, repository, null);

		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	private void createContent(String destinationFolderId, Repository repo,
			String path) throws ServiceException {
		try {
			ContentsService contentsService;
			if (gitHubClient == null) {
				contentsService = new ContentsService();
			} else {
				contentsService = new ContentsService(gitHubClient);

			}
			List<RepositoryContents> listRepositoryContents;
			if (path == null) {
				listRepositoryContents = contentsService.getContents(repo);
			} else {
				listRepositoryContents = contentsService
						.getContents(repo, path);
			}

			for (RepositoryContents contents : listRepositoryContents) {
				logger.debug("Contents: [name=" + contents.getName()
						+ ", type=" + contents.getType() + ", encoding="
						+ contents.getEncoding() + ",  path="
						+ contents.getPath() + ", sha=" + contents.getSha()
						+ "]");
				if (contents.getType() != null) {
					if (contents.getType().compareToIgnoreCase(
							RepositoryContents.TYPE_FILE) == 0) {
						createFile(destinationFolderId, repo, contents);
					} else {
						if (contents.getType().compareToIgnoreCase(
								RepositoryContents.TYPE_DIR) == 0) {
							createDirectory(destinationFolderId, repo, contents);
						} else {

						}
					}
				}

			}
		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	private void createDirectory(String destinationFolderId,
			Repository repository, RepositoryContents contents)
			throws ServiceException {
		try {
			logger.debug("Directory: [destinationFolderId="
					+ destinationFolderId + ", folderName="
					+ contents.getName() + ", folderDescription="
					+ contents.getName() + "]");
			String internalFolderId=StorageUtil.createFolderOnWorkspace(userName, destinationFolderId,
					contents.getName(), contents.getName());

			createContent(internalFolderId, repository, contents.getPath());
		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	private void createFile(String destinationFolderId, Repository repository,
			RepositoryContents contents) throws ServiceException {
		try {
			DataService dataService;
			if (gitHubClient == null) {
				dataService = new DataService();
			} else {
				dataService = new DataService(gitHubClient);
			}

			Blob blob = dataService.getBlob(repository, contents.getSha());
			logger.debug("Blob: [encoding=" + blob.getEncoding() + "]");
			if (blob.getEncoding().compareToIgnoreCase(
					RepositoryContents.ENCODING_BASE64) == 0) {
				byte[] decodedBytes = Base64.decodeBase64(blob.getContent()
						.getBytes());

				InputStream is = new ByteArrayInputStream(decodedBytes);
				StorageUtil.saveOnWorkspace(userName, destinationFolderId,
						contents.getName(), contents.getName(), is);
				// logger.debug(new String(decodedBytes));
			}

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

}
