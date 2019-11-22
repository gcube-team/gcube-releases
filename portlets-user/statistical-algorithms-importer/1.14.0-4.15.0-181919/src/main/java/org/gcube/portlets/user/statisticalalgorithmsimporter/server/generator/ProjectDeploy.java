package org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator;

import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.social.AlgorithmNotification;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.DeploySave;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.info.InfoData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ProjectDeploy {
	private static Logger logger = LoggerFactory.getLogger(ProjectDeploy.class);

	private ServiceCredentials serviceCredentials;
	private Project project;
	private HttpServletRequest httpServletRequest;

	public ProjectDeploy(HttpServletRequest httpServletRequest, ServiceCredentials serviceCredentials,
			Project project) {
		this.serviceCredentials = serviceCredentials;
		this.project = project;
		this.httpServletRequest = httpServletRequest;

	}

	public DeploySave deploy() throws StatAlgoImporterServiceException {
		logger.debug("ProjectDeploy deploy()");
		InfoGenerator infoGenerator = new InfoGenerator(project, serviceCredentials);
		InfoData infoData = infoGenerator.readInfo();
		logger.debug("Deploy Save");
		DeploySave deploySave = new DeploySave(serviceCredentials, project, infoData);
		deploySave.save();
		logger.debug("Send notify");
		sendNotify(deploySave.getInfoText());
		return deploySave;
	}

	private void sendNotify(String body) {
		AlgorithmNotification notify = new AlgorithmNotification(httpServletRequest, serviceCredentials,
				body);
		notify.run();
	}

}
