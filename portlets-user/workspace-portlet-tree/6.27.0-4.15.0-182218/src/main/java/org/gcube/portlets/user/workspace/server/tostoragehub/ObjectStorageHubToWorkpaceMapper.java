/**
 *
 */
package org.gcube.portlets.user.workspace.server.tostoragehub;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.URLFile;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceSharedFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.items.FileItem;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategorySmartFolder;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FolderGridModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalImage;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalUrl;
import org.gcube.portlets.user.workspace.server.util.UserUtil;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class ObjectStorageHubToWorkpaceMapper.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 17, 2019
 */
public class ObjectStorageHubToWorkpaceMapper {


	protected static Logger logger = LoggerFactory.getLogger(ObjectStorageHubToWorkpaceMapper.class);

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
			logger.warn("Default conversion to base item for item: "+wrappedItem);
			fileModel = new FileModel(wrappedItem.getId(), wrappedItem.getName(), parentFolderModel, wrappedItem.isFolder(), wrappedItem.isShared());
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
			fileGridModel = new FolderGridModel(wrappedItem.getId(), wrappedItem.getName(), FormatterUtil.toDate(wrappedItem.getLastModificationTime()), parentFolderModel, -1, true, false,false, isPublicFolder);
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
				FormatterUtil.
				toDate(fileItem.getLastModificationTime()),
				parentFolderModel,
				fileItem.getSize()==null?0:fileItem.getSize(),
				false,
				false);
			fileGridModel = (FileGridModel) setFolderItemType(fileGridModel, fileItem);
			break;

		case VRE_FOLDER:
			fileGridModel = new FolderGridModel(wrappedItem.getId(), wrappedItem.getName(), FormatterUtil.toDate(wrappedItem.getLastModificationTime()), parentFolderModel, -1, true, false,true, false);
			fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.toString());
			fileGridModel.setShareable(true);
			fileGridModel.setDescription(wrappedItem.getDescription());
			break;

		case SHARED_FOLDER:
			WorkspaceSharedFolder shared = (WorkspaceSharedFolder) wrappedItem;
			boolean isPublicDir = ((org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) wrappedItem).isPublicFolder(); //TODO
			//String name = shared.isVreFolder()?shared.getTitle():shared.getName();
			fileGridModel = new FolderGridModel(wrappedItem.getId(), wrappedItem.getName(), FormatterUtil.toDate(wrappedItem.getLastModificationTime()), parentFolderModel, -1, true, true, shared.isVreFolder(), isPublicDir);

			if(isPublicDir)
				fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED_PUBLIC.getLabel().toString());
			else
				fileGridModel.setType(GXTFolderItemTypeEnum.FOLDER_SHARED.getLabel().toString());

			fileGridModel.setShortcutCategory(GXTCategorySmartFolder.SMF_SHARED_FOLDERS);
			fileGridModel.setShareable(true);
			fileGridModel.setDescription(wrappedItem.getDescription());
			break;
			
		case URL_ITEM:
			URLFile urlItem = (URLFile) wrappedItem;
			fileGridModel = new FileGridModel(urlItem.getId(),
					urlItem.getName(),
					FormatterUtil.
					toDate(urlItem.getLastModificationTime()),
					parentFolderModel,
					-1,
					false,
					false);
			//fileGridModel.setType("URL");
			fileGridModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_URL);
			fileGridModel.setShortcutCategory(GXTCategorySmartFolder.SMF_DOCUMENTS);
			break;

		default:
			logger.warn("Default conversion to base item for item: "+wrappedItem);
			fileGridModel = new FileGridModel(wrappedItem.getId(), wrappedItem.getName(),  FormatterUtil.toDate(wrappedItem.getLastModificationTime()), parentFolderModel, -1, wrappedItem.isFolder(), wrappedItem.isShared());
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
	

	/**
	 * Builds the GWT workspace image.
	 *
	 * @param wrappedImage the wrapped image
	 * @param isInteralImage the is interal image
	 * @param fullDetails the full details
	 * @param currentGroupId the current group id
	 * @param currentUserId the current user id
	 * @return the GWT workspace item
	 */
	@SuppressWarnings("unchecked")
	public static GWTWorkspaceItem buildGWTWorkspaceImage(org.gcube.common.storagehubwrapper.shared.tohl.items.ImageFileItem wrappedImage, boolean isInteralImage, boolean fullDetails, String currentGroupId, String currentUserId)
	{

		GWTWorkspaceItem gwtImage;
		GWTProperties gwtProperties = null;
		GWTWorkspaceItemAction lastAction = null;
		GWTWorkspaceFolder parent = null;

		if(fullDetails){

				gwtImage = new GWTExternalImage(
						FormatterUtil.toDate(wrappedImage.getCreationTime()),
						wrappedImage.getId(),
						gwtProperties,
						wrappedImage.getName(),
						wrappedImage.getOwner(),
						wrappedImage.getDescription(),
						FormatterUtil.toDate(wrappedImage.getLastModificationTime()),
						lastAction,
						parent, //parent
						BuildServiceURLUtil.buildImageUrl(wrappedImage.getId(), currentGroupId, currentUserId),
						BuildServiceURLUtil.buildThumbnailUrl(wrappedImage.getId(), currentGroupId, currentUserId),
						toValidInt(wrappedImage.getWidth()),
						toValidInt(wrappedImage.getHeight()),
						wrappedImage.getSize(),
						toValidInt(wrappedImage.getThumbnailWidth()),
						toValidInt(wrappedImage.getThumbnailHeight()),
						-1,
						wrappedImage.getMimeType());
			}else{
				gwtImage = new GWTExternalImage(wrappedImage.getId(), wrappedImage.getName(),
						BuildServiceURLUtil.buildImageUrl(wrappedImage.getId(), currentGroupId, currentUserId),
						BuildServiceURLUtil.buildThumbnailUrl(wrappedImage.getId(), currentGroupId, currentUserId),
						toValidInt(wrappedImage.getWidth()),
						toValidInt(wrappedImage.getHeight()),
						toValidInt(wrappedImage.getSize()),
						toValidInt(wrappedImage.getThumbnailWidth()),
						toValidInt(wrappedImage.getThumbnailHeight()),
						-1,
						wrappedImage.getMimeType());
			}
		return gwtImage;
	}
	
	
	/**
	 * To valid int.
	 *
	 * @param aLong the a long
	 * @return the int
	 */
	public static int toValidInt(Long aLong){
		
		if(aLong==null)
			return -1;
		
		try {
			return Integer.parseInt(aLong.toString());
		}catch (Exception e) {
			logger.warn("Exception on parsing the value "+aLong+" as long");
			return -1;
		}
		
	}
	

	/**
	 * Builds the GWT worspace url.
	 *
	 * @param workspace the workspace
	 * @param fileItem the file item
	 * @param isInternalUrl the is internal url
	 * @param fullDetails the full details
	 * @return the GWT workspace item
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")
	public static GWTWorkspaceItem buildGWTWorspaceUrl(Workspace workspace, FileItem fileItem, boolean isInternalUrl, boolean fullDetails) throws Exception
	{

		
		//TODO EXTRACT THE URL FROM THE FILE
		GWTWorkspaceItem gwtUrl = null;
		GWTProperties gwtProperties = null;
		GWTWorkspaceItemAction lastAction = null;

		if(fullDetails){
			gwtUrl = new GWTExternalUrl(
					FormatterUtil.toDate(fileItem.getCreationTime()),
					fileItem.getId(),
					gwtProperties,
					fileItem.getName(),
					fileItem.getOwner(),
					fileItem.getDescription(),
					FormatterUtil.toDate(fileItem.getLastModificationTime()),
					lastAction,
					null,
					fileItem.getSize(),
					getUrl(workspace, fileItem));
		}
		else
			gwtUrl = new GWTExternalUrl(getUrl(workspace, fileItem));
		
		return gwtUrl;
	}
	
	/**
	 * Gets the url.
	 *
	 * @param workspace the workspace
	 * @param fileItem the file item
	 * @return the url
	 * @throws Exception the exception
	 */
	public static String getUrl(Workspace workspace, org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem fileItem) throws Exception {
		try {
			return readStreamAsString(workspace.downloadFile(fileItem.getId(), fileItem.getName(), null, null).getStream());
		} catch (IOException e) {
			logger.error("GET URL error for file: " +fileItem, e);
			throw new InternalErrorException(e.getMessage());
		}
	}
	
	/**
	 * Read the entire input stream as string. The system encoding is used.
	 *
	 * @param is the input stream.
	 * @return the read string.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String readStreamAsString(InputStream is) throws java.io.IOException{
		StringBuilder sb = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			sb.append(buf, 0, numRead);
		}
		reader.close();
		return sb.toString();
	}


}
