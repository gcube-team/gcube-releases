package org.gcube.portlets.user.trainingcourse.server.hl;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class TrainingCourseHLPathManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2018
 */
public class TrainingCourseHLPathManager {
	
	
	/** The Constant BASE_FOLDER_NAME. */
	public static final String BASE_FOLDER_NAME = "_trainingcourses";
	
	/** The Constant BASE_FOLDER_DESCRIPTION. */
	public static final String BASE_FOLDER_DESCRIPTION = "This is the root folder used to store training courses created by user";
	
	/** The logger. */
	public static Logger logger = LoggerFactory.getLogger(TrainingCourseHLPathManager.class);
	
	/**
	 * Instantiates a new training course HL path manager.
	 */
	public TrainingCourseHLPathManager() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Check training course path. It the root path exists nothing is done, otherwise the root folder to save training courses is created.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the root folder {@link TrainingCourseHLPathManager#BASE_FOLDER_NAME}
	 * @throws Exception the exception
	 */
	private static WorkspaceFolder checkTraininingCoursePath(HttpServletRequest httpServletRequest) throws Exception {
		
		String rootFolderPath = null;
		try {
			Workspace workspace = WsUtil.getWorkspace(httpServletRequest);
			WorkspaceFolder root = workspace.getRoot();
			rootFolderPath = root.getPath()+"/"+BASE_FOLDER_NAME;
			if(!workspace.exists(BASE_FOLDER_NAME, root.getId())) {
				WorkspaceFolder folder = workspace.createFolder(BASE_FOLDER_NAME, BASE_FOLDER_DESCRIPTION, root.getId());
				//TODO LATER
				//folder.setHidden(true);
				return folder;
			}else {
				
				return (WorkspaceFolder) workspace.getItemByPath(rootFolderPath);
			}
			
		} catch (WorkspaceFolderNotFoundException | InternalErrorException | HomeNotFoundException e) {
			logger.error("Error on instancing the workspace, try again later", e);
			throw new Exception("An error occurred contacting the workspace, try again later");
		} catch (ItemNotFoundException e) {
			logger.error("Error on checking root folder path: "+rootFolderPath, e);
			throw new Exception("An error occurred checking root folder path: "+rootFolderPath);
		}
	}
	
	
//	/**
//	 * Gets the workspace folder for id.
//	 *
//	 * @param httpServletRequest the http servlet request
//	 * @param folderId the folder id
//	 * @return the workspace folder for id
//	 * @throws Exception the exception
//	 */
//	public static WorkspaceFolder getWorkspaceFolderForId(HttpServletRequest httpServletRequest, String folderId) throws Exception {
//		;
//		try {
//			Workspace workspace = WsUtil.getWorkspace(httpServletRequest);
//			return (WorkspaceFolder) workspace.getItem(folderId);
//			
//		} catch (WorkspaceFolderNotFoundException | InternalErrorException | HomeNotFoundException e) {
//			logger.error("Error on instancing the workspace, try again later", e);
//			throw new Exception("An error occurred contacting the workspace, try again later");
//		} catch (ItemNotFoundException e) {
//			logger.error("Error on getting workspace folder for id: "+folderId, e);
//			throw new Exception("An error occurred getting the folder id: "+folderId +" from Workspace");
//		}
//	}
	

	/**
	 * Gets the workspace item for id.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param folderId the folder id
	 * @return the workspace item for id
	 * @throws Exception the exception
	 */
	public static WorkspaceItem getWorkspaceItemForId(HttpServletRequest httpServletRequest, String folderId) throws Exception {
		
		try {
			Workspace workspace = WsUtil.getWorkspace(httpServletRequest);
			return workspace.getItem(folderId);
			
		} catch (WorkspaceFolderNotFoundException | InternalErrorException | HomeNotFoundException e) {
			logger.error("Error on instancing the workspace, try again later", e);
			throw new Exception("An error occurred contacting the workspace, try again later");
		} catch (ItemNotFoundException e) {
			logger.error("Error on getting workspace folder item: "+folderId, e);
			throw new Exception("An error occurred getting the workspace item for id: "+folderId +" from Workspace");
		}
	}
	
	
	
	/**
	 * Gets the root folder for training courses.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the root folder for training courses
	 * @throws Exception the exception
	 */
	public static WorkspaceFolder getRootFolderForTrainingCourses(HttpServletRequest httpServletRequest) throws Exception {
		
		try {
			return checkTraininingCoursePath(httpServletRequest);
			
		} catch (WorkspaceFolderNotFoundException | InternalErrorException | HomeNotFoundException e) {
			logger.error("Error on getting root folder "+BASE_FOLDER_NAME, e);
			throw new Exception("An error occurred getting root folder "+BASE_FOLDER_NAME+", try again later or contact the support");
		}
		
	}

}
