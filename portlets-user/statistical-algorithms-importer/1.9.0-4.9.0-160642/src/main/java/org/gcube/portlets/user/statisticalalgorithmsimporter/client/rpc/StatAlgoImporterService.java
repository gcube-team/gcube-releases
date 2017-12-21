package org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor.SAIDescriptor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.InputData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.ProjectCreateSession;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.session.UserInfo;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
@RemoteServiceRelativePath("statalgoimporterservice")
public interface StatAlgoImporterService extends RemoteService {
	/**
	 * Get informations on the current user
	 * 
	 * @return UserInfo user inforamations
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public UserInfo hello() throws StatAlgoImporterServiceException;

	/**
	 * Retrieve SAI descriptort
	 * 
	 * @return SAI descriptort
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public SAIDescriptor getSAIDescripor() throws StatAlgoImporterServiceException;

	// File Upload Monitor
	/**
	 * Get File Upload Monitor during the file upload operation in Import CSV
	 * 
	 * @return FileUploadMonitor file upload monitor
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public FileUploadMonitor getFileUploadMonitor() throws StatAlgoImporterServiceException;

	// Code
	public ArrayList<CodeData> getCode() throws StatAlgoImporterServiceException;

	public Project createProjectOnWorkspace(ProjectCreateSession projectCreateSession)
			throws StatAlgoImporterServiceException;

	//
	public Project setMainCode(ItemDescription itemDescription) throws StatAlgoImporterServiceException;

	public Project setBinaryCode(ItemDescription itemDescription) throws StatAlgoImporterServiceException;

	public void addResourceToProject(ItemDescription itemDescription) throws StatAlgoImporterServiceException;

	public Project deleteResourceOnProject(ItemDescription itemDescription) throws StatAlgoImporterServiceException;

	public void saveProject(InputData inputData) throws StatAlgoImporterServiceException;

	public Project openProjectOnWorkspace(ItemDescription newProjectFolder) throws StatAlgoImporterServiceException;

	public void saveCode(String code) throws StatAlgoImporterServiceException;

	public Project createSoftware(InputData inputData) throws StatAlgoImporterServiceException;

	public String getPublicLink(ItemDescription itemDescription) throws StatAlgoImporterServiceException;

	public Project restoreUISession(String value) throws StatAlgoImporterServiceException;

	public Project setNewCode(InputData inputData, ItemDescription itemDescription, String code)
			throws StatAlgoImporterServiceException;

	public String publishSoftware() throws StatAlgoImporterServiceException;

	public String getDeployOperationStatus(String operationId) throws StatAlgoImporterServiceException;

	public String getDeployOperationLogs(String operationId) throws StatAlgoImporterServiceException;

	public void repackageSoftware() throws StatAlgoImporterServiceException;

}
