package org.gcube.portlets.user.statisticalalgorithmsimporter.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterService;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.annotation.WPS4RParser;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.file.CodeReader;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator.ProjectBuilder;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.social.AlgorithmNotification;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.social.Recipient;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.FilesStorage;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.MainCodeSave;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.ProjectArchiver;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterSessionExpiredException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.InputData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.MainCode;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectFolder;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.session.UserInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StatAlgoImporterServiceImpl extends RemoteServiceServlet implements
		StatAlgoImporterService {

	private static Logger logger = LoggerFactory
			.getLogger(StatAlgoImporterServiceImpl.class);

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		System.out.println("Fix JAXP: jdk.xml.entityExpansionLimit=0");
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		System.out.println("initializing StatAlgoImporterService");
		String notificationRecipientsFile = "/statalgoimporter/properties/NotificationRecipients.txt";
		InputStream notificationRecipientsInputStream = this
				.getServletContext().getResourceAsStream(
						notificationRecipientsFile);
		String text = null;
		try {
			text = IOUtils.toString(notificationRecipientsInputStream,
					StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			System.out.println(e.getStackTrace().toString());
		}
		ArrayList<Recipient> recipients = new ArrayList<Recipient>();

		JSONObject obj = new JSONObject(text);
		System.out.println("" + obj);
		JSONArray arr = obj.getJSONArray("recipients");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject dest = arr.getJSONObject(i);
			System.out.println("" + dest);
			String user = dest.getString("user");
			String surname = dest.getString("surname");
			String name = dest.getString("name");
			Recipient rec = new Recipient(user, surname, name);
			recipients.add(rec);

		}
		System.out.println("Recipients: " + recipients);
		SessionUtil.setRecipients(this.getServletContext(), recipients);

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public UserInfo hello() throws StatAlgoImporterServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("hello()");
			UserInfo userInfo = new UserInfo(serviceCredentials.getUserName(),
					serviceCredentials.getGroupId(),
					serviceCredentials.getGroupName(),
					serviceCredentials.getScope(),
					serviceCredentials.getEmail(),
					serviceCredentials.getFullName());
			logger.debug("UserInfo: " + userInfo);
			return userInfo;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public Project restoreUISession(String value)
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			SessionUtil.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("restoreUISession(): " + value);
			Project project = SessionUtil.getProjectSession(session);
			return project;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("getCode(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FileUploadMonitor getFileUploadMonitor()
			throws StatAlgoImporterServiceException {

		HttpSession session = this.getThreadLocalRequest().getSession();
		SessionUtil.getServiceCredentials(this.getThreadLocalRequest());

		FileUploadMonitor fileUploadMonitor = SessionUtil
				.getFileUploadMonitor(session);
		if (fileUploadMonitor == null) {
			throw new StatAlgoImporterServiceException(
					"Error retrieving the fileUploadMonitor: null");
		}

		logger.debug("FileUploadMonitor: " + fileUploadMonitor);

		return fileUploadMonitor;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ArrayList<CodeData> getCode()
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			logger.debug("getCode()");
			Project projectSession = SessionUtil.getProjectSession(session);
			if (projectSession != null) {
				CodeReader codeFileReader = new CodeReader(projectSession,
						serviceCredentials);
				ArrayList<CodeData> codeList = codeFileReader.getCodeList();
				for (CodeData codeData : codeList) {
					logger.debug("" + codeData.getId() + " "
							+ codeData.getCodeLine());
				}
				return codeList;
			} else {
				return new ArrayList<CodeData>();
			}
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("getCode(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createProjectOnWorkspace(ItemDescription newProjectFolder)
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("createProjectOnWorkspace(): " + newProjectFolder);
			if (ProjectArchiver.existProjectInFolder(newProjectFolder,
					serviceCredentials)) {
				throw new StatAlgoImporterServiceException(
						"Attention a project is present in this folder, use open or another folder!");
			} else {
				ProjectFolder projectFolder = new ProjectFolder(
						newProjectFolder);
				Project projectSession = new Project(projectFolder);
				SessionUtil.setProjectSession(session, projectSession);
				logger.debug("Create Project: " + projectSession);
			}

			return;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error(
					"createProjectOnWorkspace(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public Project openProjectOnWorkspace(ItemDescription newProjectFolder)
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("openProjectOnWorkspace()");

			Project project = ProjectArchiver.readProject(newProjectFolder,
					serviceCredentials);
			SessionUtil.setProjectSession(session, project);

			return project;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error(
					"openProjectOnWorkspace(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public Project setMainCode(ItemDescription itemDescription)
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("SetMainCode(): " + itemDescription);
			Project project = SessionUtil.getProjectSession(session);
			if (project != null) {
				project.setMainCode(new MainCode(itemDescription));
				project.setInputData(null);
				project.setProjectTarget(null);
				WPS4RParser wps4Parser = new WPS4RParser(project,
						serviceCredentials);
				project = wps4Parser.parse();
				SessionUtil.setProjectSession(session, project);
				logger.debug("Project: " + project);
			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

			return project;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("setMainCode(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public void addResourceToProject(ItemDescription itemDescription)
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("addResourceToProject(): " + itemDescription);
			if (itemDescription == null || itemDescription.getId() == null) {
				throw new StatAlgoImporterServiceException(
						"Add resource to project is failed, invalid resource: "
								+ itemDescription);
			}

			Project project = SessionUtil.getProjectSession(session);
			if (project != null && project.getProjectFolder() != null
					&& project.getProjectFolder().getFolder() != null) {
				FilesStorage fileStorage = new FilesStorage();
				fileStorage.copyItemOnFolder(serviceCredentials.getUserName(),
						itemDescription.getId(), project.getProjectFolder()
								.getFolder().getId());
			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

			return;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("addResourceToProject(): " + e.getLocalizedMessage(),
					e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public Project deleteResourceOnProject(ItemDescription itemDescription)
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			logger.debug("deleteResourceOnProject(): " + itemDescription);
			if (itemDescription == null || itemDescription.getId() == null) {
				throw new StatAlgoImporterServiceException(
						"Delete resource on project is failed, invalid resource: "
								+ itemDescription);
			}

			Project project = SessionUtil.getProjectSession(session);
			checkProjectInfoForDelete(itemDescription, serviceCredentials,
					session, project);
			FilesStorage fileStorage = new FilesStorage();
			fileStorage.deleteItemOnFolder(serviceCredentials.getUserName(),
					itemDescription.getId());
			return project;

		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error(
					"deleteResourceOnProject(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	private void checkProjectInfoForDelete(ItemDescription itemDescription,
			ServiceCredentials serviceCredentials, HttpSession session,
			Project project) throws StatAlgoImporterSessionExpiredException,
			StatAlgoImporterServiceException {
		if (project != null) {
			if (project.getMainCode() != null
					&& project.getMainCode().getItemDescription() != null
					&& project.getMainCode().getItemDescription().getId()
							.compareTo(itemDescription.getId()) == 0) {
				project.setMainCode(null);
				project.setInputData(null);
				project.setProjectTarget(null);
				SessionUtil.setProjectSession(session, project);
				ProjectArchiver.archive(project, serviceCredentials);
			} else {
				if (project.getProjectTarget() != null
						&& project.getProjectTarget().getFolder() != null
						&& project.getProjectTarget().getFolder().getId()
								.compareTo(itemDescription.getId()) == 0) {
					project.setProjectTarget(null);
					SessionUtil.setProjectSession(session, project);
					ProjectArchiver.archive(project, serviceCredentials);
				} else {
					if (project.getProjectTarget() != null
							&& project.getProjectTarget().getProjectCompile() != null
							&& project.getProjectTarget().getProjectCompile()
									.getFolder() != null
							&& project.getProjectTarget().getProjectCompile()
									.getFolder().getId()
									.compareTo(itemDescription.getId()) == 0) {
						project.getProjectTarget().setProjectCompile(null);
						SessionUtil.setProjectSession(session, project);
						ProjectArchiver.archive(project, serviceCredentials);
					} else {
						if (project.getProjectTarget() != null
								&& project.getProjectTarget()
										.getProjectDeploy() != null
								&& project.getProjectTarget()
										.getProjectDeploy().getFolder() != null
								&& project.getProjectTarget()
										.getProjectDeploy().getFolder().getId()
										.compareTo(itemDescription.getId()) == 0) {
							project.getProjectTarget().setProjectDeploy(null);
							SessionUtil.setProjectSession(session, project);
							ProjectArchiver
									.archive(project, serviceCredentials);
						} else {
							if (project.getProjectTarget() != null
									&& project.getProjectTarget()
											.getProjectDeploy() != null
									&& project.getProjectTarget()
											.getProjectDeploy()
											.getPackageProject() != null
									&& project.getProjectTarget()
											.getProjectDeploy()
											.getPackageProject().getId()
											.compareTo(itemDescription.getId()) == 0) {
								project.getProjectTarget().getProjectDeploy()
										.setPackageProject(null);
								SessionUtil.setProjectSession(session, project);
								ProjectArchiver.archive(project,
										serviceCredentials);
							} else {

							}
						}
					}
				}
			}
		} else {
			throw new StatAlgoImporterServiceException("No project open!");
		}
	}

	@Override
	public void saveProject(InputData inputData)
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			logger.debug("saveProject():" + inputData);
			Project project = SessionUtil.getProjectSession(session);
			if (project != null) {
				project.setInputData(inputData);
				SessionUtil.setProjectSession(session, project);
				ProjectArchiver.archive(project, serviceCredentials);
			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

			return;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("saveProject(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public void saveCode(String code) throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			logger.debug("saveCode():" + code);
			Project project = SessionUtil.getProjectSession(session);
			if (project != null) {
				MainCode mainCode = project.getMainCode();
				if (mainCode == null || mainCode.getItemDescription() == null) {
					throw new StatAlgoImporterServiceException(
							"No main code set!");
				} else {
					MainCodeSave mainCodeSave = new MainCodeSave();
					mainCodeSave.save(serviceCredentials,
							mainCode.getItemDescription(), code, project);
				}
			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

			return;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("saveCode(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public Project setNewMainCode(ItemDescription fileDescription, String code)
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("saveCode(): itemDescription" + fileDescription
					+ ", code:" + code);
			Project project = SessionUtil.getProjectSession(session);
			if (project != null && project.getProjectFolder() != null
					&& project.getProjectFolder().getFolder() != null) {
				MainCodeSave mainCodeSave = new MainCodeSave();
				ItemDescription mainCodeItemDescription = mainCodeSave.saveNew(
						serviceCredentials, fileDescription, code, project);
				MainCode mainCode = new MainCode(mainCodeItemDescription);
				project.setMainCode(mainCode);
				project.setInputData(null);
				project.setProjectTarget(null);
				WPS4RParser wps4Parser = new WPS4RParser(project,
						serviceCredentials);
				project = wps4Parser.parse();
				SessionUtil.setProjectSession(session, project);
				return project;
			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("saveCode(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public void createSoftware(InputData inputData)
			throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("createSoftware(): " + inputData);
			Project project = SessionUtil.getProjectSession(session);
			if (project != null) {
				project.setInputData(inputData);
				SessionUtil.setProjectSession(session, project);
				ProjectBuilder projectBuilder = new ProjectBuilder(project,
						serviceCredentials);
				project = projectBuilder.buildTarget();
				SessionUtil.setProjectSession(session, project);
				ProjectArchiver.archive(project, serviceCredentials);
			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

			return;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("createSoftware(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public String getPublicLink(ItemDescription itemDescription)
			throws StatAlgoImporterServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("GetPublicLink(): " + itemDescription);
			FilesStorage filesStorage = new FilesStorage();
			String link = filesStorage.getPublicLink(serviceCredentials.getUserName(),
					itemDescription.getId());

			return link;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("getPublicLink(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public void publishSoftware() throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("PublishSoftware()");
			ArrayList<Recipient> recipients = SessionUtil.getRecipients(session
					.getServletContext());
			Project project = SessionUtil.getProjectSession(session);
			if (project != null) {
				ProjectBuilder projectBuilder = new ProjectBuilder(project,
						serviceCredentials);
				project = projectBuilder.buildDeploy();
				SessionUtil.setProjectSession(session, project);
				ProjectArchiver.archive(project, serviceCredentials);
				AlgorithmNotification notify = new AlgorithmNotification(
						this.getThreadLocalRequest(), serviceCredentials,
						project, recipients);
				notify.run();
			} else {
				throw new StatAlgoImporterServiceException(
						"The software was not created correctly try to recreate it!");
			}

			return;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("publishSoftware(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public void repackageSoftware() throws StatAlgoImporterServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("RepackageSoftware()");
			Project project = SessionUtil.getProjectSession(session);
			if (project != null) {
				ProjectBuilder projectBuilder = new ProjectBuilder(project,
						serviceCredentials);
				project = projectBuilder.buildRepackage();
				SessionUtil.setProjectSession(session, project);
				ProjectArchiver.archive(project, serviceCredentials);
			} else {
				throw new StatAlgoImporterServiceException(
						"The script was not packaged correctly!");
			}

			return;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("repackageSoftware(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

}
