/**
 *
 */
package org.gcube.portlets.user.workspace.server.tostoragehub;


import org.apache.log4j.Logger;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceSharedFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.items.FileItem;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategorySmartFolder;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FolderGridModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.server.GWTWorkspaceBuilder;
import org.gcube.portlets.user.workspace.server.util.UserUtil;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;



/**
 * The Class StorageHubToWorkpaceConveter.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Sep 20, 2018
 */
public class ObjectStorageHubToWorkpaceMapper {


	protected static Logger logger = Logger.getLogger(ObjectStorageHubToWorkpaceMapper.class);

	/**
	 * To root folder.
	 *
	 * @param folder the folder
	 * @return the folder model
	 * @throws InternalErrorException the internal error exception
	 */
	public static FolderModel toRootFolder(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder folder) throws InternalErrorException{

		if(folder==null)
			return null;

		boolean isVreFolder = false;
		boolean isPublicFolder = false; //TODO
		if(folder.isShared()){
			WorkspaceSharedFolder sharedFolder = (WorkspaceSharedFolder) folder;
			isVreFolder = sharedFolder.isVreFolder();
		}

//		FolderModel root = new FolderModel(workspaceRoot.getId(),workspaceRoot.getName(),null, true, workspaceRoot.isShared(), false, workspaceRoot.isPublic());
//		root.setIsRoot(true);

		FolderModel theFolder = new FolderModel(folder.getId(), folder.getName(), null, folder.isFolder(), folder.isShared(), isVreFolder, isPublicFolder);
		theFolder.setIsRoot(folder.isRoot());
		return theFolder;

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
	public static FileModel toTreeFileModelItem(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wrappedItem, FileModel parentFolderModel, boolean isParentShared) throws InternalErrorException{

		FileModel fileModel = null;
		//boolean isPublic = false;

		switch (wrappedItem.getType()) {

		case FOLDER:
			boolean isPublicFolder = ((org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) wrappedItem).isPublicFolder(); //TODO
			fileModel = new FolderModel(wrappedItem.getId(), wrappedItem.getName(), parentFolderModel, true, false, false, isPublicFolder);
			if(isPublicFolder)
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER_PUBLIC.getLabel());
			else
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER.getLabel());

			fileModel.setType(GXTFolderItemTypeEnum.FOLDER.getLabel());
			fileModel.setShareable(true);
			fileModel.setDescription(wrappedItem.getDescription());
			break;

		case FILE_ITEM:
			fileModel = new FileModel(wrappedItem.getId(), wrappedItem.getName(), parentFolderModel, false, false);
			FileItem folderItem = (FileItem) wrappedItem;
			fileModel = setFolderItemType(fileModel, folderItem);
			fileModel.setShareable(true);
			break;

		case VRE_FOLDER:
			fileModel = new FolderModel(wrappedItem.getId(), wrappedItem.getName(), parentFolderModel, true, true, true, false);
			fileModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.toString());
			fileModel.setShareable(true);
			fileModel.setDescription(wrappedItem.getDescription());
			break;
		case SHARED_FOLDER:
			//WorkspaceSharedFolder shared = (WorkspaceSharedFolder) wrappedItem;
			boolean isPublicDir = ((org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) wrappedItem).isPublicFolder(); //TODO
			//isPublic = ((WorkspaceFolder) shared).isPublic(); //TODO isPublic
			//OLD HL
			//String name = shared.isVreFolder()?shared.getDisplayName():item.getName();
			fileModel = new FolderModel(wrappedItem.getId(), wrappedItem.getName(), parentFolderModel, true, true, false, isPublicDir);
			if(isPublicDir)
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED_PUBLIC.toString());
			else
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.toString());

			fileModel.setShareable(true);
			fileModel.setDescription(wrappedItem.getDescription());
			break;

		default:
			logger.error("ALERT Conversion RETURNING null for item "+wrappedItem.getName());
			break;

		}

		//SET SHARE POLICY
		if(parentFolderModel!=null && parentFolderModel.isShared()){
			fileModel.setShared(true);
			fileModel.setShareable(false); //UPDATED TO CHANGE PERMISSIONS TO SHARED SUBFOLDERS
		}else if(parentFolderModel==null && wrappedItem.isShared()){  //ADDED TO FIX #1808
			fileModel.setShared(true);
			if(wrappedItem.getParentId()!=null && isParentShared)
				fileModel.setShareable(false);
		}

		//setSynchedThreddsStateFor(fileModel);
		fileModel.setIsRoot(wrappedItem.isRoot());
		logger.trace("Returning converted tree item: "+fileModel);
		return fileModel;
	}



	/**
	 * To grid file model item.
	 *
	 * @param wrappedItem the wrapped item
	 * @param parentFolderModel the parent folder model
	 * @param isParentShared the is parent shared
	 * @param loggedUser the logged user
	 * @return the file grid model
	 * @throws InternalErrorException the internal error exception
	 */
	public static FileGridModel toGridFileModelItem(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wrappedItem, FileModel parentFolderModel, GCubeUser loggedUser) throws InternalErrorException{

		FileGridModel fileGridModel = null;

		//logger.debug("toGridFileModelItem: "+wrappedItem);
		//logger.debug("toGridFileModelItem getType: "+wrappedItem.getType());

		switch (wrappedItem.getType()) {

		case FOLDER:
			boolean isPublicFolder = ((org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) wrappedItem).isPublicFolder(); //TODO
			fileGridModel = new FolderGridModel(wrappedItem.getId(), wrappedItem.getName(), GWTWorkspaceBuilder.toDate(wrappedItem.getLastModificationTime()), parentFolderModel, -1, true, false,false, isPublicFolder);
			if(isPublicFolder)
				fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_PUBLIC.getLabel().toString());
			else
				fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER.getLabel().toString());

			fileGridModel.setShortcutCategory(GXTCategorySmartFolder.SMF_FOLDERS);
			fileGridModel.setShareable(true);
			fileGridModel.setDescription(wrappedItem.getDescription());
			break;

		case FILE_ITEM:
			FileItem fileItem = (FileItem) wrappedItem;
			fileGridModel = new FileGridModel(fileItem.getId(),
				fileItem.getName(),
				GWTWorkspaceBuilder.
				toDate(fileItem.getLastModificationTime()),
				parentFolderModel,
				fileItem.getSize()==null?0:fileItem.getSize(),
				false,
				false);
			fileGridModel = (FileGridModel) setFolderItemType(fileGridModel, fileItem);
			break;

		case VRE_FOLDER:
			fileGridModel = new FolderGridModel(wrappedItem.getId(), wrappedItem.getName(), GWTWorkspaceBuilder.toDate(wrappedItem.getLastModificationTime()), parentFolderModel, -1, true, false,true, false);
			fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.toString());
			fileGridModel.setShareable(true);
			fileGridModel.setDescription(wrappedItem.getDescription());
			break;

		case SHARED_FOLDER:
			WorkspaceSharedFolder shared = (WorkspaceSharedFolder) wrappedItem;
			boolean isPublicDir = ((org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) wrappedItem).isPublicFolder(); //TODO
			//String name = shared.isVreFolder()?shared.getTitle():shared.getName();
			fileGridModel = new FolderGridModel(wrappedItem.getId(), wrappedItem.getName(), GWTWorkspaceBuilder.toDate(wrappedItem.getLastModificationTime()), parentFolderModel, -1, true, true, shared.isVreFolder(), isPublicDir);

			if(isPublicDir)
				fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED_PUBLIC.getLabel().toString());
			else
				fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.getLabel().toString());

			fileGridModel.setShortcutCategory(GXTCategorySmartFolder.SMF_SHARED_FOLDERS);
			fileGridModel.setShareable(true);
			fileGridModel.setDescription(wrappedItem.getDescription());
			break;

		default:
			logger.error("gxt conversion return null for item "+wrappedItem.getName());
			break;

		}

		if(parentFolderModel!=null && parentFolderModel.isShared()){
			fileGridModel.setShared(true);
			fileGridModel.setShareable(false); //UPDATED TO CHANGE PERMISSIONS TO SHARED SUBFOLDERS
		}

		//OWNER
		if(wrappedItem.isShared()){ //IT IS READ FROM SHUB ONLY IF THE ITEM IS SHARED
			fileGridModel.setShared(true); //NOT REMOVE IT IS IMPORTANT, SEE #1459
			String ownerUsername = wrappedItem.getOwner();
			if(ownerUsername!=null){
				//					System.out.println("++++reading owner");
				//String portalLogin = owner.getPortalLogin();
				fileGridModel.setOwnerFullName(UserUtil.getUserFullName(ownerUsername));
			}
		}
		else{
			String ownerUsername = wrappedItem.getOwner();
			//Task #12911 I'm calling getUserFullName for any user other than logged user
			if(ownerUsername!=null && ownerUsername.compareToIgnoreCase(loggedUser.getUsername())!=0){
				fileGridModel.setOwnerFullName(UserUtil.getUserFullName(ownerUsername));
			}else
				fileGridModel.setOwnerFullName(loggedUser.getFullname());
		}

		logger.trace("Returning converted grid item: "+fileGridModel);
		return fileGridModel;
	}


	/**
	 * Sets the folder item type.
	 *
	 * @param fileModel the file model
	 * @param wrappedFileItem the worspace folder item
	 * @return the file model
	 * @throws InternalErrorException the internal error exception
	 */
	public static FileModel setFolderItemType(FileModel fileModel, FileItem wrappedFileItem) throws InternalErrorException{

		fileModel.setType(wrappedFileItem.getMimeType());

		//logger.debug("WrappedItem: "+wrappedFileItem);
		//logger.debug("FolderItemType: "+wrappedFileItem.getFileItemType());

		switch(wrappedFileItem.getFileItemType()){

		case PDF_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_PDF_FILE);
			fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
//			PDFFileItem extFile = (PDFFileItem) wrappedFileItem;
			break;
		case IMAGE_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_IMAGE);
			fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_IMAGES);
			break;
		case DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_FILE);
			fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
			//				Document doc = (Document) worspaceFolderItem;
			break;
		case URL_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_URL);
			fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
			break;
		case METADATA:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.METADATA);
			fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
			break;
		case GCUBE_ITEM:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.GCUBE_ITEM);
			fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_GCUBE_ITEMS);
			break;
		default:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.UNKNOWN_TYPE);
			fileModel.setShortcutCategory(GXTCategorySmartFolder.SMF_UNKNOWN);
			fileModel.setType(GXTFolderItemTypeEnum.UNKNOWN_TYPE.toString());
			break;
		}

		return fileModel;
	}

}
