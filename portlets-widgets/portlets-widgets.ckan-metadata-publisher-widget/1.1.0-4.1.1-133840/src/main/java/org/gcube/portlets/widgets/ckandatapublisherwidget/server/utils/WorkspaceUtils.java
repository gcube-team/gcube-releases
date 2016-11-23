package org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.datacatalogue.ckanutillibrary.models.ResourceBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetMetadataBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceBeanWrapper;
import org.slf4j.LoggerFactory;

public class WorkspaceUtils {

	/**
	 * logger
	 */
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WorkspaceUtils.class);

	/**
	 * Copy into the .catalogue area folder the checked resources
	 * @param resourcesToAdd
	 * @param folderId
	 * @param userName
	 * @param bean
	 * @return
	 */
	public static List<ResourceBean> copyResourcesToUserCatalogueArea(
			List<ResourceBeanWrapper> resourcesToAdd, String folderId, String userName, DatasetMetadataBean bean) throws Exception{

		logger.debug("Request to copy onto catalogue area....");
		List<ResourceBean> resources = new ArrayList<ResourceBean>();
		WorkspaceItem copiedFolder = null;
		WorkspaceCatalogue userCatalogue = null;

		// in to the .catalogue area of the user's workspace
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(userName)
				.getWorkspace();

		// Retrieve the catalogue of the user
		userCatalogue = ws.getCatalogue();

		// Create the folder in the catalogue
		copiedFolder = userCatalogue.addWorkspaceItem(folderId, userCatalogue.getId()); // add to .catalogue root area

		// change description for the folder
		copiedFolder.setDescription(bean.getDescription());

		// change name of the copied folder to match the title (append the timestamp to avoid ties)
		((WorkspaceFolder)copiedFolder).rename(org.gcube.datacatalogue.ckanutillibrary.utils.UtilMethods.fromProductTitleToName(bean.getTitle()) + "_" + System.currentTimeMillis());

		// copy only the selected ones
		for(ResourceBeanWrapper resourceBeanWrapper : resourcesToAdd){

			if (resourceBeanWrapper.isToBeAdded()) {

				// ok it is a file, so copy it into the copiedFolder
				WorkspaceItem copiedFile = userCatalogue.addWorkspaceItem(resourceBeanWrapper.getId(), copiedFolder.getId());

				// name and description could have been edited
				copiedFile.setDescription(resourceBeanWrapper.getDescription());

				resources.add(new ResourceBean(
						copiedFile.getPublicLink(true), 
						resourceBeanWrapper.getName(), 
						copiedFile.getDescription(), 
						copiedFile.getId(),
						userName, 
						null, // to be set
						((FolderItem)copiedFile).getMimeType()));

				// postpone rename operation
				copiedFile.rename(resourceBeanWrapper.getName() + "_" + System.currentTimeMillis());
			}
		}

		// return
		return resources;
	}

	/**
	 * This method receives a folder id within the user's workspace and set the list of resources in the dataset bean to be returned
	 * @param folderId
	 * @param owner
	 * @param bean
	 * @param userName
	 * @throws Exception
	 */
	public static void handleWorkspaceResources(String folderId, String userName,
			DatasetMetadataBean bean) throws Exception {

		// get workspace
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome(userName).getWorkspace();

		WorkspaceItem originalFolder = ws.getItem(folderId);

		// set some info
		String onlyAlphanumeric = originalFolder.getName().replaceAll("[^A-Za-z0-9.-_]", " "); // that is, remove characters different than the ones inside
		bean.setTitle(onlyAlphanumeric);
		bean.setDescription(originalFolder.getDescription());

		// Create the folder in the catalogue
		Map<String, String> folderItems = Utils.getGcubeItemProperties(originalFolder);
		bean.setCustomFields(folderItems);

		// check the resources within the folder (skip subdirectories for now TODO)
		List<String> childrenIds = new ArrayList<String>();

		for (WorkspaceItem file : originalFolder.getChildren()) {
			if(!file.isFolder()){
				childrenIds.add(file.getId());
			}
		}

		// set them into the bean
		bean.setResources(getWorkspaceResourcesInformation(childrenIds, ws, userName));

	}

	/**
	 * Build up the resource beans.
	 * @param resourceIds
	 * @param ws
	 * @param username
	 * @return a list of resource wrapper beans
	 */
	public static List<ResourceBeanWrapper> getWorkspaceResourcesInformation(
			List<String> resourceIds, Workspace ws, String username){

		List<ResourceBeanWrapper> toReturn = new ArrayList<>();

		for (String resourceId : resourceIds) {

			try{
				logger.debug("RESOURCE ID IS " + resourceId);

				ResourceBeanWrapper newResource = new ResourceBeanWrapper();
				WorkspaceItem item = ws.getItem(resourceId);
				newResource.setDescription(item.getDescription());
				newResource.setId(item.getId());
				newResource.setUrl(item.getPublicLink(true));
				newResource.setName(item.getName());
				newResource.setToBeAdded(true); // default is true
				newResource.setMimeType(((FolderItem)item).getMimeType());
				toReturn.add(newResource);
			}catch(Exception e ){
				logger.error("Unable to add resource with id " + resourceId + " to the product bean");
			}

		}
		
		return toReturn;
	}


}
