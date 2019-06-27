package org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.gcube.datacatalogue.ckanutillibrary.shared.ResourceBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class WorkspaceUtils {

	//private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WorkspaceUtils.class);
	private static final Log logger = LogFactoryUtil.getLog(WorkspaceUtils.class);
	private static final String RESOURCES_NAME_SEPARATOR = "_";
	private static final String STRIP_NOT_ALPHANUMERIC = "[^A-Za-z0-9.-_]";

	/**
	 * This method receives a folder id within the user's workspace and set the list of resources in the dataset bean to be returned
	 * @param folderId
	 * @param owner
	 * @param bean
	 * @param userName
	 * @throws Exception
	 */
	public static void handleWorkspaceResources(String folderId, String userName,
			DatasetBean bean) throws Exception {

		// get workspace
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome().getWorkspace();

		WorkspaceItem originalFolderOrFile = ws.getItem(folderId);

		logger.debug("Item retrieved is " + originalFolderOrFile);

		if(!originalFolderOrFile.isFolder()){

			ResourceElementBean resource = new ResourceElementBean();
			resource.setDescription(originalFolderOrFile.getDescription());
			resource.setFolder(false);
			resource.setEditableName(originalFolderOrFile.getName());
			resource.setName(originalFolderOrFile.getName());
			resource.setOriginalIdInWorkspace(folderId);
			bean.setResourceRoot(resource);
			bean.setTitle(originalFolderOrFile.getName().replaceAll(STRIP_NOT_ALPHANUMERIC, " "));
			bean.setDescription(originalFolderOrFile.getDescription());

		}else{

			String onlyAlphanumericTitle = originalFolderOrFile.getName().replaceAll(STRIP_NOT_ALPHANUMERIC, " "); 
			bean.setTitle(onlyAlphanumericTitle);
			bean.setDescription(originalFolderOrFile.getDescription());

			// Create the folder in the catalogue
			Map<String, String> folderItems = getGcubeItemProperties(originalFolderOrFile);

			if(folderItems != null){
				// transform this properties
				Map<String, List<String>> tempItems = new HashMap<String, List<String>>(folderItems.size());

				Iterator<Entry<String, String>> iterator = folderItems.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<java.lang.String, java.lang.String> entry = (Map.Entry<java.lang.String, java.lang.String>) iterator
							.next();
					tempItems.put(entry.getKey(), Arrays.asList(entry.getValue()));
				}
				bean.setCustomFields(tempItems);
			}

			// set them into the bean
			bean.setResourceRoot(WorkspaceUtils.getTreeFromFolder(folderId, ws));
		}

	}

	/** Gets the gcube item properties.
	 *
	 * @param item the item
	 * @return the gcube item properties
	 */
	public static Map<String, String> getGcubeItemProperties(WorkspaceItem item) {

		if(item instanceof GCubeItem){
			GCubeItem gItem = (GCubeItem) item;
			try {
				if(gItem.getProperties()!=null){
					Map<String, String> map = gItem.getProperties().getProperties();
					HashMap<String, String> properties = new HashMap<String, String>(map.size()); //TO PREVENT GWT SERIALIZATION ERROR
					for (String key : map.keySet())
						properties.put(key, map.get(key));

					return properties;
				}
			} catch (InternalErrorException e) {
				logger.error("Error in server getItemProperties: ", e);
			}
		}
		return null;
	}

	/**
	 * Returns a tree object
	 * @param workspaceFolderId
	 * @param ws
	 * @return ResourceElementBean a tree object
	 * @throws Exception 
	 */
	public static ResourceElementBean getTreeFromFolder(String workspaceFolderId, Workspace ws) throws Exception{

		ResourceElementBean rootElem = new ResourceElementBean();
		String pathSeparator = ws.getPathSeparator();
		WorkspaceItem initialItem = ws.getItem(workspaceFolderId);
		String fullPathBase = initialItem.getPath();
		fullPathBase = fullPathBase.endsWith(ws.getPathSeparator()) ? fullPathBase : fullPathBase + ws.getPathSeparator();
		rootElem.setFolder(initialItem.isFolder());
		rootElem.setFullPath(initialItem.getPath().replace(fullPathBase, ""));
		rootElem.setName(initialItem.getName());
		rootElem.setOriginalIdInWorkspace(initialItem.getId());
		rootElem.setDescription(initialItem.getDescription());
		extractEditableNameFromPath(rootElem, pathSeparator);

		// recursive visiting
		if(initialItem.isFolder())
			visit(rootElem, initialItem, fullPathBase, pathSeparator);

		logger.debug("Tree that is going to be returned is " + rootElem);
		return rootElem;
	}

	/**
	 * Recursive visit of a workspace item
	 * @param rootElem
	 * @param initialItemWS
	 * @throws InternalErrorException
	 */
	private static void visit(ResourceElementBean parent, WorkspaceItem initialItemWS, String fullPathBase, String pathSeparator) throws InternalErrorException {
		List<? extends WorkspaceItem> children = initialItemWS.getChildren();
		ArrayList<ResourceElementBean> childrenInTree = new ArrayList<ResourceElementBean>(children.size());
		for (WorkspaceItem item : children) {
			//			logger.debug("Path BEFORE REPLACE is " + item.getPath());
			//			logger.debug("Path AFTER REPLACE is " + item.getPath().replace(fullPathBase, ""));
			//			logger.debug("Name is " + item.getName());
			//			logger.debug("id is " + item.getId());
			ResourceElementBean elem = new ResourceElementBean();
			elem.setFolder(item.isFolder());
			elem.setOriginalIdInWorkspace(item.getId());
			elem.setFullPath(item.getPath().replace(fullPathBase, "")); 
			elem.setParent(parent);
			elem.setName(item.getName());
			elem.setDescription(item.getDescription());
			extractEditableNameFromPath(elem, pathSeparator);
			childrenInTree.add(elem);
			logger.trace("Elem is " + elem);
			if(item.isFolder())
				visit(elem, item, fullPathBase, pathSeparator);
		}
		// add these list as child of the rootElem
		parent.setChildren(childrenInTree);
	}

	/**
	 * Replaces the "/" char with a custom one and return an editable name for the user
	 * @param rootElem
	 * @param pathSeparatorInWs
	 */
	private static void extractEditableNameFromPath(ResourceElementBean rootElem, String pathSeparatorInWs) {

		if(rootElem == null)
			return;

		String elemName = rootElem.getName();
		String fullPath = rootElem.getFullPath();
		logger.info("Element original is " + rootElem);

		int lastIndex = rootElem.getFullPath().lastIndexOf(elemName);
		fullPath = rootElem.getFullPath().substring(0, lastIndex);
		fullPath = fullPath.replaceAll(pathSeparatorInWs, RESOURCES_NAME_SEPARATOR) + elemName;
		rootElem.setEditableName(fullPath);
	}

	/**
	 * Copy into the .catalogue area folder the checked resources.
	 * There is no difference among a single-file-publish and a folder-publish.
	 * @param folderId
	 * @param userName
	 * @param bean
	 * @return
	 */
	public static List<ResourceBean> copyResourcesToUserCatalogueArea(String folderOrFileId, String userName, DatasetBean bean) throws Exception{

		logger.debug("Request to copy onto catalogue area....");
		List<ResourceBean> resources = new ArrayList<ResourceBean>();
		WorkspaceItem copiedFolder = null;
		WorkspaceCatalogue userCatalogue = null;
		ResourceElementBean rootResource = bean.getResourceRoot();

		// into the .catalogue area of the user's workspace
		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome()
				.getWorkspace();

		// Retrieve the catalogue of the user
		userCatalogue = ws.getCatalogue();

		// get workspace item (it could be a file or a folder)
		WorkspaceItem originalItem = ws.getItem(folderOrFileId);

		// copy the folder in the catalogue if it is a folder, or create a new folder
		long referenceTime = System.currentTimeMillis();
		if(originalItem.isFolder()){
			copiedFolder = userCatalogue.addWorkspaceItem(folderOrFileId, userCatalogue.getId()); // add to .catalogue root area
			copiedFolder.setDescription(bean.getDescription());
			((WorkspaceFolder)copiedFolder).rename(CatalogueUtilMethods.fromProductTitleToName(bean.getTitle()) + "_" + referenceTime);
		}
		else{
			copiedFolder = userCatalogue.createFolder(CatalogueUtilMethods.fromProductTitleToName(bean.getTitle()) + "_" + referenceTime, bean.getDescription());
		}

		// retrieve the children
		List<ResourceElementBean> resourcesToAdd = rootResource.getChildren();

		// copy only the selected ones
		for(ResourceElementBean resource : resourcesToAdd){

			if (resource.isToBeAdded()) {

				logger.debug("Resource to add is " + resource);

				// ok it is a file, so copy it into the copiedFolder
				WorkspaceItem copiedFile = userCatalogue.addWorkspaceItem(resource.getOriginalIdInWorkspace(), copiedFolder.getId());

				// name and description could have been edited
				copiedFile.setDescription(resource.getDescription());

				// check if it is an external url
				String externalUrl = null;
				try{
					boolean isExternalUrl = ((FolderItem)copiedFile).getFolderItemType().equals(FolderItemType.EXTERNAL_URL);
					externalUrl = isExternalUrl ? ((ExternalUrl)copiedFile).getUrl() : null;
				}catch(Exception e){
					logger.warn("Unable to check if it is an external url file ", e);
				}

				resources.add(new ResourceBean(
						externalUrl != null ? externalUrl : copiedFile.getPublicLink(true), 
								resource.getEditableName(), 
								copiedFile.getDescription(), 
								copiedFile.getId(),
								userName, 
								null, // dataset id, to be set
								((FolderItem)copiedFile).getMimeType()));

				// postpone rename operation
				copiedFile.rename(resource.getEditableName());
			}

		}
		return resources;
	}

}