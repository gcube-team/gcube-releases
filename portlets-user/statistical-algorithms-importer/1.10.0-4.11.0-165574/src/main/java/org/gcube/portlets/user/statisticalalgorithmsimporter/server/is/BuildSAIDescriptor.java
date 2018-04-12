package org.gcube.portlets.user.statisticalalgorithmsimporter.server.is;

import java.util.ArrayList;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.PoolManagerConfig;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.ProjectLanguageType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.ProjectSetup;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.ProjectSupportType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.SAIDescriptor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class BuildSAIDescriptor {

	private static Logger logger = LoggerFactory.getLogger(BuildSAIDescriptor.class);

	public static SAIDescriptor build(String scope) throws StatAlgoImporterServiceException {
		SAIDescriptor saiDescriptor = null;

		if (Constants.DEBUG_MODE) {
			logger.info("Debug Mode");
			PoolManagerConfig poolManagerConfig = new PoolManagerConfig(false);

			ArrayList<ProjectSetup> availableProjectConfigurations = new ArrayList<>();
			ProjectSetup r = new ProjectSetup(ProjectLanguageType.R.getId(), ProjectSupportType.REDIT);
			availableProjectConfigurations.add(r);
			ProjectSetup rBlackBox = new ProjectSetup(ProjectLanguageType.R_BLACKBOX.getId(),
					ProjectSupportType.BLACKBOX);
			availableProjectConfigurations.add(rBlackBox);
			ProjectSetup java = new ProjectSetup(ProjectLanguageType.JAVA.getId(), ProjectSupportType.BLACKBOX);
			availableProjectConfigurations.add(java);
			ProjectSetup knimeWorkflow = new ProjectSetup(ProjectLanguageType.KNIME_WORKFLOW.getId(),
					ProjectSupportType.BLACKBOX);
			availableProjectConfigurations.add(knimeWorkflow);
			ProjectSetup linuxCompiled = new ProjectSetup(ProjectLanguageType.LINUX_COMPILED.getId(),
					ProjectSupportType.BLACKBOX);
			availableProjectConfigurations.add(linuxCompiled);
			ProjectSetup octave = new ProjectSetup(ProjectLanguageType.OCTAVE.getId(), ProjectSupportType.BLACKBOX);
			availableProjectConfigurations.add(octave);
			ProjectSetup python = new ProjectSetup(ProjectLanguageType.PYTHON.getId(), ProjectSupportType.BLACKBOX);
			availableProjectConfigurations.add(python);
			ProjectSetup windowsCompiled = new ProjectSetup(ProjectLanguageType.WINDOWS_COMPILED.getId(),
					ProjectSupportType.BLACKBOX);
			availableProjectConfigurations.add(windowsCompiled);
			ProjectSetup preInstalled = new ProjectSetup(ProjectLanguageType.PRE_INSTALLED.getId(),
					ProjectSupportType.BASHEDIT);
			availableProjectConfigurations.add(preInstalled);
			saiDescriptor = new SAIDescriptor(poolManagerConfig, Constants.REMOTE_TEMPLATE_FILE,
					availableProjectConfigurations);
		} else {
			logger.info("Production Mode");
			SAIDescriptorJAXB saiDescriptorJAXB = null;
			try {
				saiDescriptorJAXB = InformationSystemUtils.retrieveSAIDescriptor(scope);
			} catch (StatAlgoImporterServiceException e) {
				logger.info(e.getLocalizedMessage());
			}
			PoolManagerConfig poolManagerConfig;
			String remoteTemplateFile;
			ArrayList<ProjectSetup> availableProjectConfigurations = new ArrayList<>();

			logger.debug("SAIDescriptorJAXB: " + saiDescriptorJAXB);
			if (saiDescriptorJAXB != null) {
				if (saiDescriptorJAXB.getPoolmanager() != null) {
					PoolManagerJAXB poolManagerJAXB = saiDescriptorJAXB.getPoolmanager();
					poolManagerConfig = new PoolManagerConfig(poolManagerJAXB.isEnable());
				} else {
					logger.info("PoolManager disabled for scope: " + scope);
					poolManagerConfig = new PoolManagerConfig(false);
				}

				if(saiDescriptorJAXB.getRemotetemplatefile()!=null&&!saiDescriptorJAXB.getRemotetemplatefile().isEmpty()){
					remoteTemplateFile = saiDescriptorJAXB.getRemotetemplatefile();
				} else {
					remoteTemplateFile = Constants.REMOTE_TEMPLATE_FILE;
				}

				if (saiDescriptorJAXB.getAvailableprojectconfiguration() != null
						&& !saiDescriptorJAXB.getAvailableprojectconfiguration().isEmpty()) {

					ProjectSupportType type;

					for (AvailableProjectConfigJAXB availableProjectConfigJAXB : saiDescriptorJAXB
							.getAvailableprojectconfiguration()) {
						type = ProjectSupportType.valueFromLabel(availableProjectConfigJAXB.getSupport());
						if (type != null) {
							availableProjectConfigurations
									.add(new ProjectSetup(availableProjectConfigJAXB.getLanguage(), type));

						}
					}

				} else {
					logger.info("Available Project Configuration use default configuration, scope: " + scope);

					ProjectSetup r = new ProjectSetup(ProjectLanguageType.R.getId(), ProjectSupportType.REDIT);
					availableProjectConfigurations.add(r);
					ProjectSetup rBlackBox = new ProjectSetup(ProjectLanguageType.R_BLACKBOX.getId(),
							ProjectSupportType.BLACKBOX);
					availableProjectConfigurations.add(rBlackBox);
					ProjectSetup java = new ProjectSetup(ProjectLanguageType.JAVA.getId(), ProjectSupportType.BLACKBOX);
					availableProjectConfigurations.add(java);
					ProjectSetup knimeWorkflow = new ProjectSetup(ProjectLanguageType.KNIME_WORKFLOW.getId(),
							ProjectSupportType.BLACKBOX);
					availableProjectConfigurations.add(knimeWorkflow);
					ProjectSetup linuxCompiled = new ProjectSetup(ProjectLanguageType.LINUX_COMPILED.getId(),
							ProjectSupportType.BLACKBOX);
					availableProjectConfigurations.add(linuxCompiled);
					ProjectSetup octave = new ProjectSetup(ProjectLanguageType.OCTAVE.getId(),
							ProjectSupportType.BLACKBOX);
					availableProjectConfigurations.add(octave);
					ProjectSetup python = new ProjectSetup(ProjectLanguageType.PYTHON.getId(),
							ProjectSupportType.BLACKBOX);
					availableProjectConfigurations.add(python);
					ProjectSetup windowsCompiled = new ProjectSetup(ProjectLanguageType.WINDOWS_COMPILED.getId(),
							ProjectSupportType.BLACKBOX);
					availableProjectConfigurations.add(windowsCompiled);
					ProjectSetup preInstalled = new ProjectSetup(ProjectLanguageType.PRE_INSTALLED.getId(),
							ProjectSupportType.BASHEDIT);
					availableProjectConfigurations.add(preInstalled);
				}

			} else {
				logger.info("Production Mode Default");
				logger.info("SAIDescriptorJAXB use default configuration for scope: " + scope);
				poolManagerConfig = new PoolManagerConfig(false);
				remoteTemplateFile = Constants.REMOTE_TEMPLATE_FILE;
				
				ProjectSetup r = new ProjectSetup(ProjectLanguageType.R.getId(), ProjectSupportType.REDIT);
				availableProjectConfigurations.add(r);
				ProjectSetup rBlackBox = new ProjectSetup(ProjectLanguageType.R_BLACKBOX.getId(),
						ProjectSupportType.BLACKBOX);
				availableProjectConfigurations.add(rBlackBox);
				ProjectSetup java = new ProjectSetup(ProjectLanguageType.JAVA.getId(), ProjectSupportType.BLACKBOX);
				availableProjectConfigurations.add(java);
				ProjectSetup knimeWorkflow = new ProjectSetup(ProjectLanguageType.KNIME_WORKFLOW.getId(),
						ProjectSupportType.BLACKBOX);
				availableProjectConfigurations.add(knimeWorkflow);
				ProjectSetup linuxCompiled = new ProjectSetup(ProjectLanguageType.LINUX_COMPILED.getId(),
						ProjectSupportType.BLACKBOX);
				availableProjectConfigurations.add(linuxCompiled);
				ProjectSetup octave = new ProjectSetup(ProjectLanguageType.OCTAVE.getId(), ProjectSupportType.BLACKBOX);
				availableProjectConfigurations.add(octave);
				ProjectSetup python = new ProjectSetup(ProjectLanguageType.PYTHON.getId(), ProjectSupportType.BLACKBOX);
				availableProjectConfigurations.add(python);
				ProjectSetup windowsCompiled = new ProjectSetup(ProjectLanguageType.WINDOWS_COMPILED.getId(),
						ProjectSupportType.BLACKBOX);
				availableProjectConfigurations.add(windowsCompiled);
				ProjectSetup preInstalled = new ProjectSetup(ProjectLanguageType.PRE_INSTALLED.getId(),
						ProjectSupportType.BASHEDIT);
				availableProjectConfigurations.add(preInstalled);

			}

			saiDescriptor = new SAIDescriptor(poolManagerConfig, remoteTemplateFile, availableProjectConfigurations);

		}

		logger.debug("SAIDescriptor: " + saiDescriptor);
		return saiDescriptor;
	}

}
