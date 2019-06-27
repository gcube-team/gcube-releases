package org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.FilesStorage;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ProjectShareInfoBuilder {
	public static final Logger logger = LoggerFactory.getLogger(ProjectShareInfoBuilder.class);

	private ServiceCredentials serviceCredentials;
	private Project project;

	public ProjectShareInfoBuilder(ServiceCredentials serviceCredentials,
			Project project) {
		this.serviceCredentials = serviceCredentials;
		this.project = project;

	}

	public void create() throws StatAlgoImporterServiceException {
		if (project != null && project.getInputData() != null && project.getInputData().getProjectInfo() != null
				&& project.getInputData().getProjectInfo().getProjectShareInfo() != null) {
			if (project.getInputData().getProjectInfo().getProjectShareInfo().isPrivateAlgorithm()) {
				FilesStorage filesStorage = new FilesStorage();
				List<String> users = filesStorage.getSharedList(serviceCredentials.getUserName(),
						project.getProjectFolder().getFolder().getId());
				project.getInputData().getProjectInfo().getProjectShareInfo().setUsers(new ArrayList<String>(users));
			} else {
				project.getInputData().getProjectInfo().getProjectShareInfo().setUsers(null);
			}
		} else {
			
		}

	}

}
