/**
 *
 */
package org.gcube.portlets.user.workspace.server.tostoragehub;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFileVersion;
import org.gcube.common.storagehubwrapper.shared.tohl.items.PropertyMap;
import org.gcube.portal.wssynclibrary.shared.thredds.Sync_Status;
import org.gcube.portal.wssynclibrary.thredds.WorkspaceThreddsSynchronize;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.server.GWTWorkspaceBuilder;
import org.gcube.portlets.user.workspace.server.util.UserUtil;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;




/**
 * The Class StorageHubToWorkpaceConverter.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Sep 20, 2018
 */
public class StorageHubToWorkpaceConverter implements Serializable{


	/**
	 *
	 */
	private static final long serialVersionUID = 6935303928299846569L;

	protected static Logger logger = Logger.getLogger(StorageHubToWorkpaceConverter.class);
	private String scope;
	private GCubeUser loggedUser;
	private String workspaceRootId;



	/**
	 * Instantiates a new storage hub to workpace converter.
	 */
	public StorageHubToWorkpaceConverter() {

	}


	/**
	 * To version history.
	 *
	 * @param versions the versions
	 * @return the list
	 */
	public List<FileVersionModel> toVersionHistory(List<WorkspaceFileVersion> versions){

		if(versions==null || versions.isEmpty()){
			logger.warn("Version history is null or empty!");
			return new ArrayList<FileVersionModel>();
		}

		List<FileVersionModel> listVersions = new ArrayList<FileVersionModel>(versions.size());
		for (WorkspaceFileVersion wsVersion : versions) {
			String user = UserUtil.getUserFullName(wsVersion.getOwner());
			FileVersionModel file = new FileVersionModel(wsVersion.getId(), wsVersion.getName(), wsVersion.getRemotePath(), user, GWTWorkspaceBuilder.toDate(wsVersion.getCreated()), wsVersion.isCurrentVersion());
			listVersions.add(file);
		}
		return listVersions;
	}



	/**
	 * Instantiates a new storage hub to workpace converter.
	 *
	 * @param scope the scope
	 * @param loggedUser the logged user
	 */
	public StorageHubToWorkpaceConverter(String scope, GCubeUser loggedUser) {
		this.scope = scope;
		this.loggedUser = loggedUser;
	}


	/**
	 * To root folder.
	 *
	 * @param folder the folder
	 * @return the folder model
	 * @throws InternalErrorException the internal error exception
	 */
	public FolderModel toRootFolder(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder folder) throws InternalErrorException{

		return ObjectStorageHubToWorkpaceMapper.toRootFolder(folder);

	}

	/**
	 * To tree file model item.
	 *
	 * @param wrappedItem the wrapped item
	 * @param parentFolderModel the parent folder model
	 * @param isParentShared the is parent shared
	 * @return the file model
	 * @throws InternalErrorException the internal error exception
	 */
	public FileModel toTreeFileModel(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wrappedItem, FileModel parentFolderModel, Boolean isParentShared) throws InternalErrorException{

		FileModel fileModel = ObjectStorageHubToWorkpaceMapper.toTreeFileModelItem(wrappedItem, parentFolderModel, isParentShared);
		return setSynchedThreddsStateFor(fileModel, wrappedItem);
	}



	/**
	 * To grid file model.
	 *
	 * @param wrappedItem the wrapped item
	 * @param parentFolderModel the parent folder model
	 * @return the file grid model
	 * @throws InternalErrorException the internal error exception
	 */
	public FileGridModel toGridFileModel(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wrappedItem, FileModel parentFolderModel) throws InternalErrorException{

		FileGridModel fileGridModel = ObjectStorageHubToWorkpaceMapper.toGridFileModelItem(wrappedItem, parentFolderModel, loggedUser);
		return (FileGridModel) setSynchedThreddsStateFor(fileGridModel, wrappedItem);
	}


	/**
	 * Sets the synched thredds state for.
	 *
	 * @param fileModel the file model
	 * @param wrappedItem
	 * @return the file model
	 */
	protected FileModel setSynchedThreddsStateFor(FileModel fileModel, WorkspaceItem wrappedItem) {

		Sync_Status status = null;

		try {
			if(wrappedItem.getPropertyMap()!=null) {
				String wsSyncStatus = null;
				try{

					PropertyMap map = wrappedItem.getPropertyMap();
					logger.debug("Property Map for folder: "+fileModel.getName()+" has value: "+map.getValues());
					wsSyncStatus = (String) map.getValues().get(WorkspaceThreddsSynchronize.WS_SYNCH_SYNCH_STATUS);
					logger.debug("Item id: "+wrappedItem.getId()+" read from Shub has current: "+WorkspaceThreddsSynchronize.WS_SYNCH_SYNCH_STATUS +" value at: "+wsSyncStatus);
					if(wsSyncStatus!=null)
						status = Sync_Status.valueOf(wsSyncStatus);
				}catch (Exception e) {
					logger.warn(wsSyncStatus + " is not value of "+Sync_Status.values()+", returning null");
				}
			}
		} catch (Exception e) {
			logger.warn("It is not possible to get synched status for item: "+fileModel.getIdentifier());
		}

		fileModel.setSyncThreddsStatus(status);
		return fileModel;
	}


	/**
	 * To file trashed model.
	 *
	 * @param trashItem the trash item
	 * @return the file trashed model
	 */
	public FileTrashedModel toFileTrashedModel(org.gcube.common.storagehubwrapper.shared.tohl.TrashedItem trashItem) {

		FileTrashedModel fileTrashModel = new FileTrashedModel();
		try {
			fileTrashModel.setName(trashItem.getTitle()); //ADDING THE TITLE INSTEAD OF NAME

			fileTrashModel.setIdentifier(trashItem.getId());

			//SETTING PARENT
			FileModel oldParent = new FileModel(trashItem.getOriginalParentId(), "", true);

			fileTrashModel.setOrginalPath(trashItem.getDeletedFrom());
			fileTrashModel.setParentFileModel(oldParent);
	//
	//		//SETTING DELETED BY
			InfoContactModel deleteUser = buildGxtInfoContactFromPortalLogin(trashItem.getDeletedBy());
			fileTrashModel.setDeleteUser(deleteUser);

			//SETTING MIME TYPE

//			String mimeType = "";
//			if(trashItem instanceof FileItem){
//				FileItem fileItem = (FileItem) trashItem;
//				mimeType = fileItem.getMimeType();
//			}

			String mimeType = trashItem.getMimeType()!=null?trashItem.getMimeType():"";
			fileTrashModel.setType(mimeType);

			//SETTING IS DIRECTORY
			fileTrashModel.setIsDirectory(trashItem.isFolder());

			//SETTING DELETE DATE
			fileTrashModel.setDeleteDate(GWTWorkspaceBuilder.toDate(trashItem.getDeletedTime()));

			fileTrashModel.setShared(trashItem.isShared());

			logger.debug("Converting return trash item: "+fileTrashModel.getName() +" id: "+fileTrashModel.getIdentifier());

			logger.trace("Returning trash item: "+fileTrashModel);

		}catch (Exception e) {

			logger.debug("Error into toFileTrashedModel for item: "+fileTrashModel.getName() +" id: "+fileTrashModel.getIdentifier());
			return null;

		}

		return fileTrashModel;

	}

	/**
	 * Builds the gxt info contact from portal login.
	 *
	 * @param portalLogin the portal login
	 * @return the info contact model
	 */
	protected InfoContactModel buildGxtInfoContactFromPortalLogin(String portalLogin){

		if(portalLogin==null){
			logger.warn("portal login is null, return empty");
			portalLogin = "";
		}

		return new InfoContactModel(portalLogin, portalLogin, UserUtil.getUserFullName(portalLogin), false);
	}



	/**
	 * Gets the workspace root id.
	 *
	 * @return the workspaceRootId
	 */
	public String getWorkspaceRootId() {

		return workspaceRootId;
	}



	/**
	 * Sets the workspace root id.
	 *
	 * @param workspaceRootId the workspaceRootId to set
	 */
	public void setWorkspaceRootId(String workspaceRootId) {

		this.workspaceRootId = workspaceRootId;
	}

}
