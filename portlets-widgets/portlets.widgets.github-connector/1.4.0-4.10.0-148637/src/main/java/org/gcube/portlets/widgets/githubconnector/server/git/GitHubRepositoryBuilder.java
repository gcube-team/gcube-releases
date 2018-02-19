package org.gcube.portlets.widgets.githubconnector.server.git;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ServiceException;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.GitHubRepository;
import org.gcube.portlets.widgets.githubconnector.shared.git.data.GitHubUser;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class GitHubRepositoryBuilder {
	private static Logger logger = Logger.getLogger(GitHubRepositoryBuilder.class);

	/**
	 * 
	 * @param repository
	 *            repository
	 * @return github repository
	 * @throws ServiceException
	 *             service exception
	 */
	public static GitHubRepository build(Repository repository) throws ServiceException {
		try {
			if (repository != null) {
				if (repository.getOwner() != null) {
					GitHubUser gitHubUser = new GitHubUser(repository.getOwner().getId(),
							repository.getOwner().getName(), repository.getOwner().getLogin(),
							repository.getOwner().getCompany(), repository.getOwner().getLocation(),
							repository.getOwner().getUrl(), repository.getOwner().getEmail());

					GitHubRepository gitHubRepository = new GitHubRepository(repository.getId(), repository.getName(),
							gitHubUser, repository.getDescription(), repository.getGitUrl(), repository.getWatchers(),
							repository.getCreatedAt(), repository.getUpdatedAt());
					return gitHubRepository;
				} else {
					logger.error("Invalid owner for repository: " + repository.getName());
					throw new ServiceException("Invalid owner for repository: " + repository.getName());
				}
			} else {
				logger.error("Invalid repository: null");
				throw new ServiceException("Invalid repository: null");

			}

		} catch (Throwable e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 
	 * @param repositories
	 *            list of repositories
	 * @return list of github repository
	 * @throws ServiceException
	 *             service exception
	 */
	public static ArrayList<GitHubRepository> build(List<Repository> repositories) throws ServiceException {
		if (repositories != null) {
			ArrayList<GitHubRepository> gitHubRepositories = new ArrayList<>();
			for (Repository repository : repositories) {
				GitHubRepository gitHubRepository = build(repository);
				gitHubRepositories.add(gitHubRepository);
			}
			return gitHubRepositories;
		} else {
			logger.error("Invalid list of repositories: null!");
			throw new ServiceException("Invalid list of repositories: null!");
		}

	}
}
