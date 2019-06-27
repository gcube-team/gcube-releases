package org.gcube.portlets.user.statisticalalgorithmsimporter.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterService;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.annotation.WPS4RParser;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.file.CodeReader;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator.ProjectBuilder;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator.ProjectDeploy;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.generator.ProjectShareInfoBuilder;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.is.BuildSAIDescriptor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.poolmanager.DataMinerPoolManager;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.social.Recipient;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.CodeSave;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.DeploySave;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.FilesStorage;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.ProjectArchiver;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.ProjectLanguageType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.SAIDescriptor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterSessionExpiredException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.InputData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.MainCode;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectConfig;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectCreateSession;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectFolder;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBashEdit;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportBlackBox;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectSupportREdit;
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
public class StatAlgoImporterServiceImpl extends RemoteServiceServlet implements StatAlgoImporterService {

	private static Logger logger = LoggerFactory.getLogger(StatAlgoImporterServiceImpl.class);

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
		InputStream notificationRecipientsInputStream = this.getServletContext()
				.getResourceAsStream(notificationRecipientsFile);
		String text = null;
		try {
			text = IOUtils.toString(notificationRecipientsInputStream, StandardCharsets.UTF_8.name());
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
		SessionUtil.setDefaultRecipients(this.getServletContext(), recipients);

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public UserInfo hello() throws StatAlgoImporterServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("hello()");
			UserInfo userInfo = new UserInfo(serviceCredentials.getUserName(), serviceCredentials.getGroupId(),
					serviceCredentials.getGroupName(), serviceCredentials.getScope(), serviceCredentials.getEmail(),
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
	public SAIDescriptor getSAIDescripor() throws StatAlgoImporterServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("getSAIDescriptor()");
			SAIDescriptor saiDescriptor = BuildSAIDescriptor.build(serviceCredentials.getScope());
			SessionUtil.setSAIDescriptor(httpRequest, serviceCredentials, saiDescriptor);
			return saiDescriptor;

		} catch (StatAlgoImporterServiceException e) {
			logger.error("Error retrieving SAI descriptor: " + e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving SAI descriptor: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}

	}

	@Override
	public Project restoreUISession(String value) throws StatAlgoImporterServiceException {
		HttpServletRequest httpRequest = null;
		try {
			httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("restoreUISession(): " + value);
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
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
	public FileUploadMonitor getFileUploadMonitor() throws StatAlgoImporterServiceException {

		HttpServletRequest httpRequest = this.getThreadLocalRequest();
		ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

		FileUploadMonitor fileUploadMonitor = null;
		try {
			fileUploadMonitor = SessionUtil.getFileUploadMonitor(httpRequest, serviceCredentials);
		} catch (Exception e) {
			logger.error("Error retrieving the fileUploadMonitor", e);
		}
		if (fileUploadMonitor == null) {
			throw new StatAlgoImporterServiceException("Error retrieving the fileUploadMonitor: null");
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
	public ArrayList<CodeData> getCode() throws StatAlgoImporterServiceException {

		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("getCode()");
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			CodeReader codeFileReader = new CodeReader(project, serviceCredentials);
			ArrayList<CodeData> codeList = codeFileReader.getCodeList();
			return codeList;

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
	public Project createProjectOnWorkspace(ProjectCreateSession projectCreateSession)
			throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("createProjectOnWorkspace(): " + projectCreateSession);
			Project project;
			if (ProjectArchiver.existProjectInFolder(projectCreateSession.getNewProjectFolder(), serviceCredentials)) {
				throw new StatAlgoImporterServiceException(
						"Attention a project is present in this folder, use open or another folder!");
			} else {
				ProjectFolder projectFolder = new ProjectFolder(projectCreateSession.getNewProjectFolder());
				ProjectConfig projectConfig = null;
				if (projectCreateSession.getProjectSetup() != null
						&& projectCreateSession.getProjectSetup().getProjectSupportType() != null) {
					switch (projectCreateSession.getProjectSetup().getProjectSupportType()) {
					case BLACKBOX:
						projectConfig = new ProjectConfig(projectCreateSession.getProjectSetup().getLanguage(),
								new ProjectSupportBlackBox());
						break;
					case BASHEDIT:
						projectConfig = new ProjectConfig(projectCreateSession.getProjectSetup().getLanguage(),
								new ProjectSupportBashEdit());
						break;
					case REDIT:
						projectConfig = new ProjectConfig(projectCreateSession.getProjectSetup().getLanguage(),
								new ProjectSupportREdit());
						break;
					default:
						break;

					}
				}
				project = new Project(projectFolder, projectConfig);
				SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);

			}

			logger.debug("Create Project: " + project);
			return project;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("createProjectOnWorkspace(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public Project openProjectOnWorkspace(ItemDescription newProjectFolder) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("openProjectOnWorkspace()");

			Project project = ProjectArchiver.readProject(newProjectFolder, serviceCredentials);
			if (project != null) {
				if (project.getProjectConfig() == null) {
					project.setProjectConfig(
							new ProjectConfig(ProjectLanguageType.R.getId(), new ProjectSupportREdit()));
				} else {

				}
			}

			SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);

			return project;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("openProjectOnWorkspace(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public Project setMainCode(ItemDescription itemDescription) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("SetMainCode(): " + itemDescription);
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			if (project != null) {
				FilesStorage filesStorage = new FilesStorage();
				String pLink = filesStorage.getPublicLink(serviceCredentials.getUserName(), itemDescription.getId());
				itemDescription.setPublicLink(pLink);
				project.setMainCode(new MainCode(itemDescription));
				InputData inputData=project.getInputData();
				if(inputData!=null){
					inputData.setListInputOutputVariables(null);
				}
				project.setProjectTarget(null);
				WPS4RParser wps4Parser = new WPS4RParser(project, serviceCredentials);
				project = wps4Parser.parse();
				SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
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
	public Project setBinaryCode(ItemDescription itemDescription) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("SetBinaryCode(): " + itemDescription);
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			if (project != null && project.getProjectConfig() != null) {
				if (project.getProjectConfig().getProjectSupport() != null) {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBlackBox) {
						ProjectSupportBlackBox projectSupportBlackBox = (ProjectSupportBlackBox) project
								.getProjectConfig().getProjectSupport();
						FilesStorage filesStorage = new FilesStorage();
						itemDescription = filesStorage.getFileInfoOnWorkspace(
								serviceCredentials.getUserName(), itemDescription.getId());
						String pLink = filesStorage.getPublicLink(serviceCredentials.getUserName(),
								itemDescription.getId());
						itemDescription.setPublicLink(pLink);
						projectSupportBlackBox.setBinaryItem(itemDescription);
						SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
						logger.debug("Project: " + project);
					} else {
						if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
							ProjectSupportBashEdit projectSupportBashEdit = (ProjectSupportBashEdit) project
									.getProjectConfig().getProjectSupport();
							FilesStorage filesStorage = new FilesStorage();
							itemDescription = filesStorage.getFileInfoOnWorkspace(
									serviceCredentials.getUserName(), itemDescription.getId());
							String pLink = filesStorage.getPublicLink(serviceCredentials.getUserName(),
									itemDescription.getId());
							itemDescription.setPublicLink(pLink);
							projectSupportBashEdit.setBinaryItem(itemDescription);
							SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
							logger.debug("Project: " + project);
						} else {
							throw new StatAlgoImporterServiceException("Error in project setup!");
						}
					}
				} else {
					throw new StatAlgoImporterServiceException("Error in project setup no support present!");
				}
			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

			return project;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("setBinaryCode(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public void addResourceToProject(ItemDescription itemDescription) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("addResourceToProject(): " + itemDescription);
			if (itemDescription == null || itemDescription.getId() == null) {
				throw new StatAlgoImporterServiceException(
						"Add resource to project is failed, invalid resource: " + itemDescription);
			}

			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			if (project != null && project.getProjectFolder() != null
					&& project.getProjectFolder().getFolder() != null) {
				FilesStorage fileStorage = new FilesStorage();
				fileStorage.copyItemOnFolder(serviceCredentials.getUserName(), itemDescription.getId(),
						project.getProjectFolder().getFolder().getId());
			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

			return;
		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("addResourceToProject(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public Project deleteResourceOnProject(ItemDescription itemDescription) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("deleteResourceOnProject(): " + itemDescription);
			if (itemDescription == null || itemDescription.getId() == null) {
				throw new StatAlgoImporterServiceException(
						"Delete resource on project is failed, invalid resource: " + itemDescription);
			}

			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			checkProjectInfoForDelete(itemDescription, httpRequest, serviceCredentials, project);
			FilesStorage fileStorage = new FilesStorage();
			fileStorage.deleteItem(serviceCredentials.getUserName(), itemDescription.getId());
			return project;

		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("deleteResourceOnProject(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	private void checkProjectInfoForDelete(ItemDescription itemDescription, HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, Project project)
			throws StatAlgoImporterSessionExpiredException, StatAlgoImporterServiceException {
		if (project != null) {
			if (project.getMainCode() != null && project.getMainCode().getItemDescription() != null
					&& project.getMainCode().getItemDescription().getId().compareTo(itemDescription.getId()) == 0) {
				project.setMainCode(null);
				project.setInputData(null);
				project.setProjectTarget(null);
				SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
				ProjectArchiver.archive(project, serviceCredentials);
			} else {
				if (project.getProjectTarget() != null && project.getProjectTarget().getFolder() != null
						&& project.getProjectTarget().getFolder().getId().compareTo(itemDescription.getId()) == 0) {
					project.setProjectTarget(null);
					SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
					ProjectArchiver.archive(project, serviceCredentials);
				} else {
					if (project.getProjectTarget() != null && project.getProjectTarget().getProjectCompile() != null
							&& project.getProjectTarget().getProjectCompile().getFolder() != null
							&& project.getProjectTarget().getProjectCompile().getFolder().getId()
									.compareTo(itemDescription.getId()) == 0) {
						project.getProjectTarget().setProjectCompile(null);
						SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
						ProjectArchiver.archive(project, serviceCredentials);
					} else {
						if (project.getProjectTarget() != null && project.getProjectTarget().getProjectDeploy() != null
								&& project.getProjectTarget().getProjectDeploy().getFolder() != null
								&& project.getProjectTarget().getProjectDeploy().getFolder().getId()
										.compareTo(itemDescription.getId()) == 0) {
							project.getProjectTarget().setProjectDeploy(null);
							SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
							ProjectArchiver.archive(project, serviceCredentials);
						} else {
							if (project.getProjectTarget() != null
									&& project.getProjectTarget().getProjectDeploy() != null
									&& project.getProjectTarget().getProjectDeploy().getPackageProject() != null
									&& project.getProjectTarget().getProjectDeploy().getPackageProject().getId()
											.compareTo(itemDescription.getId()) == 0) {
								project.getProjectTarget().getProjectDeploy().setPackageProject(null);
								SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
								ProjectArchiver.archive(project, serviceCredentials);
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
	public void saveProject(InputData inputData) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("saveProject():" + inputData);
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			if (project != null) {
				project.setInputData(inputData);
				ProjectShareInfoBuilder projectShareInfoBuilder=new ProjectShareInfoBuilder(serviceCredentials, project);
				projectShareInfoBuilder.create();
				SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
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
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			logger.debug("saveCode():" + code);
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			if (project != null) {
				if (project.getProjectConfig() != null && project.getProjectConfig().getProjectSupport() != null) {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportREdit) {

						MainCode mainCode = project.getMainCode();
						if (mainCode == null || mainCode.getItemDescription() == null) {
							throw new StatAlgoImporterServiceException("No main code set!");
						} else {
							CodeSave mainCodeSave = new CodeSave();
							mainCodeSave.save(serviceCredentials, mainCode.getItemDescription(), code);
						}
					} else {
						if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
							ProjectSupportBashEdit projectSupportBashEdit = (ProjectSupportBashEdit) project
									.getProjectConfig().getProjectSupport();
							if (projectSupportBashEdit == null || projectSupportBashEdit.getBinaryItem() == null) {
								throw new StatAlgoImporterServiceException("No binary code set!");
							} else {
								CodeSave mainCodeSave = new CodeSave();
								mainCodeSave.save(serviceCredentials, projectSupportBashEdit.getBinaryItem(), code);
							}

						} else {
							throw new StatAlgoImporterServiceException("Error in project Support!");
						}
					}
				} else {
					throw new StatAlgoImporterServiceException("Error in project Config!");
				}
			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

			return;
		} catch (

		StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("saveCode(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public Project setNewCode(InputData inputData,ItemDescription fileDescription, String code) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("setNewCode(): "+inputData+", " + fileDescription + ", " + code);
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			if (project != null && project.getProjectFolder() != null
					&& project.getProjectFolder().getFolder() != null) {
				
				if (project.getProjectConfig() != null && project.getProjectConfig().getProjectSupport() != null) {
					if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportREdit) {

						CodeSave codeSave = new CodeSave();
						ItemDescription mainCodeItemDescription = codeSave.saveNew(serviceCredentials, fileDescription,
								code, project.getProjectFolder().getFolder().getId());
						MainCode mainCode = new MainCode(mainCodeItemDescription);
						project.setMainCode(mainCode);
						if(inputData!=null){
							inputData.setListInputOutputVariables(null);
							project.setInputData(inputData);
						}
						project.setProjectTarget(null);
						WPS4RParser wps4Parser = new WPS4RParser(project, serviceCredentials);
						project = wps4Parser.parse();
					} else {
						if (project.getProjectConfig().getProjectSupport() instanceof ProjectSupportBashEdit) {
							ProjectSupportBashEdit projectSupportBashEdit = (ProjectSupportBashEdit) project
									.getProjectConfig().getProjectSupport();

							CodeSave codeSave = new CodeSave();
							ItemDescription binaryCodeItemDescription = codeSave.saveNew(serviceCredentials,
									fileDescription, code, project.getProjectFolder().getFolder().getId());
							projectSupportBashEdit.setBinaryItem(binaryCodeItemDescription);
							project.setInputData(inputData);
							project.setProjectTarget(null);
						} else {
							throw new StatAlgoImporterServiceException("Error in project support!");
						}

					}
				} else {
					throw new StatAlgoImporterServiceException("Error in project configuration!");
				}

				SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
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
	public String getPublicLink(ItemDescription itemDescription) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("GetPublicLink(): " + itemDescription);
			FilesStorage filesStorage = new FilesStorage();
			String link = filesStorage.getPublicLink(serviceCredentials.getUserName(), itemDescription.getId());

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
	public Project createSoftware(InputData inputData) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("createSoftware(): " + inputData);
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			if (project != null) {
				project.setInputData(inputData);
				SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
				SAIDescriptor saiDescriptor = SessionUtil.getSAIDescriptor(httpRequest, serviceCredentials);
				ProjectBuilder projectBuilder = new ProjectBuilder(project, serviceCredentials, saiDescriptor);
				project = projectBuilder.buildTarget();
				SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
				ProjectArchiver.archive(project, serviceCredentials);
				return project;

			} else {
				throw new StatAlgoImporterServiceException("No project open!");
			}

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
	public String publishSoftware() throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("PublishSoftware()");
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			if (project != null) {
				SAIDescriptor saiDescriptor = SessionUtil.getSAIDescriptor(httpRequest, serviceCredentials);
				ProjectBuilder projectBuilder = new ProjectBuilder(project, serviceCredentials, saiDescriptor);
				project = projectBuilder.buildDeploy();
				SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
				ProjectArchiver.archive(project, serviceCredentials);
				ProjectDeploy projectDeploy = new ProjectDeploy(httpRequest, serviceCredentials, project);
				DeploySave deploySave = projectDeploy.deploy();
				if (saiDescriptor != null && saiDescriptor.getPoolManagerConfig() != null
						&& saiDescriptor.getPoolManagerConfig().isEnable()) {
					logger.info("Deploy On PoolManager");
					ItemDescription codeJar = project.getProjectTarget().getProjectDeploy().getCodeJar();
					logger.debug("CodeJarAdminCopy for PoolManager: " + codeJar);
					DataMinerPoolManager poolManager = new DataMinerPoolManager(serviceCredentials);
					String operationId = poolManager.deployAlgorithm(project, deploySave.getInfoData(),
							codeJar);
					logger.info("Deploy operationId: " + operationId);
					return operationId;
				} else {
					logger.info("Deploy On PoolManager disabled");
					return null;
				}
			} else {
				throw new StatAlgoImporterServiceException(
						"The software was not created correctly try to recreate it!");
			}

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
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("RepackageSoftware()");
			Project project = SessionUtil.getProjectSession(httpRequest, serviceCredentials);
			if (project != null) {
				SAIDescriptor saiDescriptor = SessionUtil.getSAIDescriptor(httpRequest, serviceCredentials);
				ProjectBuilder projectBuilder = new ProjectBuilder(project, serviceCredentials, saiDescriptor);
				project = projectBuilder.buildRepackage();
				SessionUtil.setProjectSession(httpRequest, serviceCredentials, project);
				ProjectArchiver.archive(project, serviceCredentials);
			} else {
				throw new StatAlgoImporterServiceException("The script was not packaged correctly!");
			}

			return;
		} catch (StatAlgoImporterServiceException e) {
			logger.error("repackageSoftware(): " + e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("repackageSoftware(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public String getDeployOperationStatus(String operationId) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("getDeployOperationStatus(): OperationId=" + operationId);
			DataMinerPoolManager poolManager = new DataMinerPoolManager(serviceCredentials);
			String deployOperationStatus = poolManager.getDeployOperationStatus(operationId);
			logger.debug("Deploy Operation Status: " + deployOperationStatus);
			return deployOperationStatus;

		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("getDeployOperationStatus(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public String getDeployOperationLogs(String operationId) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("getDeployOperationLogs(): OperationId=" + operationId);
			DataMinerPoolManager poolManager = new DataMinerPoolManager(serviceCredentials);
			String deployOperationLogsLink = poolManager.getDeployOperationLogsLink(operationId);
			logger.debug("Deploy Operation Logs Link: " + deployOperationLogsLink);
			return deployOperationLogsLink;

		} catch (StatAlgoImporterServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("getDeployOperationLogs(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public ItemDescription getItemDescription(String itemId) throws StatAlgoImporterServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("getItemDescription(): [itemId=" + itemId + "]");
			FilesStorage storageUtil = new FilesStorage();
			ItemDescription itemDownloadInfo = storageUtil.getItemDescription(serviceCredentials.getUserName(), itemId);
			logger.debug("ItemDescription info: " + itemDownloadInfo);
			return itemDownloadInfo;
		} catch (StatAlgoImporterServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving item description: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException("Error retrieving item description: " + e.getLocalizedMessage(), e);
		}
	}

}
