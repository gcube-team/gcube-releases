/**
 *
 */
package org.gcube.portlets.user.workspace.server.tostoragehub;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntryCreate;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntryDisabledPublicAccess;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntryEnabledPublicAccess;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntryPaste;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntryRead;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntryRestore;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntryShare;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntryUnshare;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntryUpdate;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountFolderEntryAdd;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountFolderEntryCut;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountFolderEntryRemoval;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountFolderEntryRenaming;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountingEntryType;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;
import org.gcube.common.storagehubwrapper.shared.tohl.impl.WorkspaceFileVersion;
import org.gcube.common.storagehubwrapper.shared.tohl.items.GCubeItem;
import org.gcube.common.storagehubwrapper.shared.tohl.items.PropertyMap;
import org.gcube.portal.wssynclibrary.shared.thredds.Sync_Status;
import org.gcube.portal.wssynclibrary.thredds.WorkspaceThreddsSynchronize;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.server.util.UserUtil;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingEntryType;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * The Class StorageHubToWorkpaceConverter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 20, 2018
 */
public class StorageHubToWorkpaceConverter implements Serializable{


	/**
	 *
	 */
	private static final long serialVersionUID = 6935303928299846569L;

	protected static Logger logger = LoggerFactory.getLogger(StorageHubToWorkpaceConverter.class);
	private String scope;
	private GCubeUser loggedUser;
	private String workspaceRootId;



	/**
	 * Instantiates a new storage hub to workpace converter.
	 */
	public StorageHubToWorkpaceConverter() {

	}
	
	/**********************************************************************************************************************************************
	 * 
	 * 
	 * 
	 * 
	 * TESTING MODE METHODS 
	 * 
	 * 
	 * 
	 * 
	 ***********************************************************************************************************************************************/
	protected static HashMap<String, InfoContactModel> hashTestUser = null;
	/**
	 * Used in test mode.
	 *
	 * @return the hash test users
	 */
	public static HashMap<String, InfoContactModel> getHashTestUsers(){

		if(hashTestUser==null){
			hashTestUser = new HashMap<String, InfoContactModel>();

			//USERS
			hashTestUser.put("federico.defaveri", new InfoContactModel("federico.defaveri", "federico.defaveri", "Federico de Faveri",null, false));
			hashTestUser.put("antonio.gioia", new InfoContactModel("antonio.gioia", "antonio.gioia", "Antonio Gioia",null, false));
			hashTestUser.put("fabio.sinibaldi", new InfoContactModel("fabio.sinibaldi", "fabio.sinibaldi", "Fabio Sinibaldi",null, false));
			hashTestUser.put("pasquale.pagano", new InfoContactModel("pasquale.pagano", "pasquale.pagano", "Pasquale Pagano",null, false));
			hashTestUser.put("valentina.marioli", new InfoContactModel("valentina.marioli", "valentina.marioli", "Valentina Marioli",null, false));
			hashTestUser.put("roberto.cirillo", new InfoContactModel("roberto.cirillo", "roberto.cirillo", "Roberto Cirillo",null, false));
			hashTestUser.put("francesco.mangiacrapa", new InfoContactModel("francesco.mangiacrapa", "francesco.mangiacrapa", "Francesco Mangiacrapa",null, false));
			hashTestUser.put("massimiliano.assante", new InfoContactModel("massimiliano.assante", "massimiliano.assante", "Massimiliano Assante",null, false));

		}

		return hashTestUser;
	}
	
	
	public static List<InfoContactModel> buildGxtInfoContactFromPortalLoginTestMode(List<String> listPortalLogin){

		List<InfoContactModel> listContact = new ArrayList<InfoContactModel>();

		for (String portalLogin : listPortalLogin)
			listContact.add(getHashTestUsers().get(portalLogin));

		return listContact;
	}
	
	/**********************************************************************************************************************************************
	 * 
	 * 
	 * 
	 * 
	 * END TESTING MODE 
	 * 
	 * 
	 * 
	 * 
	 ***********************************************************************************************************************************************/
	


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
			FileVersionModel file = new FileVersionModel(wsVersion.getId(), wsVersion.getName(), wsVersion.getRemotePath(), user, FormatterUtil.toDate(wsVersion.getCreated()), wsVersion.isCurrentVersion());
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
	 * @param wrappedItem item
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
			fileTrashModel.setDeleteDate(FormatterUtil.toDate(trashItem.getDeletedTime()));

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
	public static InfoContactModel buildGxtInfoContactFromPortalLogin(String portalLogin){

		if(portalLogin==null){
			logger.warn("portal login is null, return empty");
			portalLogin = "";
		}
		GCubeUser theUser = null;
		try {
			theUser = UserUtil.getUserByUsername(portalLogin);
		}catch (Exception e) {
			logger.warn("Error on retrieving user information, so using the portal login");
		}
		String fullName = null;
		String emailDomain = null;
		if(theUser!=null) {
			fullName = theUser.getFullname();
			emailDomain = theUser.getEmail();
		}
		return new InfoContactModel(portalLogin, portalLogin, fullName, emailDomain,false);
	}
	
	/**
	 * TODO ********TEMPORARY SOLUTION HL MUST MANAGE SPECIAL FOLDER AS WORKSPACESPECIALFOLDER****
	 * REMOVE THIS METHOD AND ADDING INSTANCE OF AT buildGXTFolderModelItem.
	 *
	 * @param wsFolder the ws folder
	 * @param parent the parent
	 * @param specialFolderName the special folder name
	 * @return the folder model
	 * @throws InternalErrorException the internal error exception
	 */
	public FolderModel buildGXTFolderModelItemHandleSpecialFolder(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem wsFolder, org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem parent, String specialFolderName) throws InternalErrorException {

		String name = "";

		logger.debug("buildGXTFolderModelItemHandleSpecialFolder to folder: "+wsFolder.getName());
		if(logger.isTraceEnabled()) {
			logger.trace("buildGXTFolderModelItemHandleSpecialFolder has parent: "+parent);
		}
		//MANAGEMENT SHARED FOLDER NAME
		if(wsFolder.isShared() && wsFolder.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
			logger.debug("MANAGEMENT SHARED Folder name..");
			WorkspaceSharedFolder shared = (WorkspaceSharedFolder) wsFolder;
			logger.debug("shared.isVreFolder(): "+shared.isVreFolder());
			//name = shared.isVreFolder()?shared.getName():wsFolder.getName();
			name = shared.getName();

			//MANAGEMENT SPECIAL FOLDER
		}else if(wsFolder.getName().compareTo(ConstantsExplorer.MY_SPECIAL_FOLDERS)==0 && parent!=null && parent.isRoot()){
			//MANAGEMENT SPECIAL FOLDER
			logger.debug("MANAGEMENT SPECIAL FOLDER NAME REWRITING AS: "+specialFolderName);
			if(specialFolderName!=null && !specialFolderName.isEmpty())
				name = specialFolderName;
			else
				name = wsFolder.getName();
		}else{
			logger.debug("MANAGEMENT Base Folder name..");
			name = wsFolder.getName();
		}

		logger.debug("Name is: "+name);
		boolean isPublicDir = ((org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder) wsFolder).isPublicFolder(); //TODO
		FileModel parentModel = null;
		if(parent!=null)
			parentModel = ObjectStorageHubToWorkpaceMapper.toTreeFileModelItem(parent, null, parent.isShared());
		
		FolderModel folder = new FolderModel(wsFolder.getId(), name, parentModel, true, wsFolder.isShared(), false, isPublicDir);
		folder.setShareable(true);
		folder.setIsRoot(wsFolder.isRoot());
		folder.setDescription(wsFolder.getDescription());
		//		folder.setOwner(wsFolder.getOwner());

		if(parent != null && parent.isShared()){
			folder.setShared(true);
			folder.setShareable(false);
		}
		return folder;
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
	


	/**
	 * To simple map.
	 *
	 * @param propertyMap the property map
	 * @return the map
	 */
	public Map<String, String> toSimpleMap(PropertyMap propertyMap) {

		if (propertyMap == null)
			return null;

		try {
			Map<String, String> properties = null;
			Map<String, Object> map = propertyMap.getValues();
			
			if (map != null) {
				properties = new HashMap<String, String>(map.size());
				for (String key : map.keySet()) {
					Object theValue = map.get(key);
					properties.put(key, (String) theValue);
				}
			}
			
			if(properties!=null)
				logger.error("Converted: "+properties.size()+" property/properties");
			
			return properties;
		} catch (Exception e) {
			logger.error("Error on converting a PropertyMap to simple Map<String,String>: ", e);
			return null;
		}
	}
	

	/**
	 * Builds the GXT accounting item.
	 *
	 * @param accoutings the accoutings
	 * @param gxtEntryType the gxt entry type
	 * @return the list
	 */
	public List<GxtAccountingField> buildGXTAccountingItem(List<AccountEntry> accoutings, GxtAccountingEntryType gxtEntryType) {

		List<GxtAccountingField> listAccFields = new ArrayList<GxtAccountingField>();

		if(accoutings!=null){
			logger.trace("accouting entry/entrie is/are "+accoutings.size()+ ", converting it/them...");

			for (AccountEntry shubAccEntry : accoutings) {

				GxtAccountingField af = new GxtAccountingField();
				InfoContactModel user = buildGxtInfoContactFromPortalLogin(shubAccEntry.getUser());
				af.setUser(user);
				af.setDate(FormatterUtil.toDate(shubAccEntry.getDate()));

				switch (shubAccEntry.getType()) {

				case CREATE:

					if(gxtEntryType==null || gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.CREATE)){
						AccountEntryCreate create = (AccountEntryCreate) shubAccEntry;
						af.setOperation(GxtAccountingEntryType.CREATE);

						//							af.setDescription(GxtAccountingEntryType.CREATE.getName() + " by "+user.getName());
						String msg = "";
						if(create.getItemName()==null || create.getItemName().isEmpty())
							msg = GxtAccountingEntryType.CREATE.getId() + " by "+user.getName();
						else{

							if(create.getVersion()==null)
								msg = create.getItemName() + " " + GxtAccountingEntryType.CREATE.getName() + " by "+user.getName();
							else
								msg = create.getItemName() + " v. "+create.getVersion()+" "+ GxtAccountingEntryType.CREATE.getName() + " by "+user.getName();
						}

						af.setDescription(msg);
					}

					break;

				case READ:

					if(gxtEntryType==null || gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.READ)){

						AccountEntryRead read = (AccountEntryRead) shubAccEntry;
						af.setOperation(GxtAccountingEntryType.READ);
						af.setDescription(read.getItemName() + " " + GxtAccountingEntryType.READ.getName() + " by "+user.getName());

						String msg = "";
						if(read.getItemName()==null || read.getItemName().isEmpty())
							msg = GxtAccountingEntryType.READ.getId() + " by "+user.getName();
						else{

							if(read.getVersion()==null)
								msg = read.getItemName() + " " + GxtAccountingEntryType.READ.getName() + " by "+user.getName();
							else
								msg = read.getItemName() + " v."+read.getVersion() +" "+ GxtAccountingEntryType.READ.getName() + " by "+user.getName();
						}

						af.setDescription(msg);
					}

					break;

				case CUT:

					if(gxtEntryType==null || gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.CUT)){

						af.setOperation(GxtAccountingEntryType.CUT);
						
						if(shubAccEntry instanceof AccountFolderEntryCut) {
							
							AccountFolderEntryCut cut = (AccountFolderEntryCut) shubAccEntry;
	
							String msg = "";
							if(cut.getItemName()==null || cut.getItemName().isEmpty())
								msg = GxtAccountingEntryType.CUT.getName() +" by "+user.getName();
							else{
								if(cut.getVersion()==null)
									msg = cut.getItemName()+" "+GxtAccountingEntryType.CUT.getName() +" by "+user.getName();
								else
									msg = cut.getItemName()+" v."+cut.getVersion()+" "+GxtAccountingEntryType.CUT.getName() +" by "+user.getName();
							}
	
							af.setDescription(msg);
						}else {
							logger.warn("Found an "+AccountingEntryType.class.getSimpleName()+" of kind "+shubAccEntry.getType()+ " not castable to (instance of) "+AccountFolderEntryCut.class.getSimpleName());
						}
					}

					break;

				case PASTE:

					if(gxtEntryType==null || gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.PASTE)){

						af.setOperation(GxtAccountingEntryType.PASTE);
						AccountEntryPaste paste = (AccountEntryPaste) shubAccEntry;

						if(paste.getVersion()==null)
							af.setDescription(GxtAccountingEntryType.PASTE.getName() + " from "+paste.getFromPath()+" by "+user.getName());
						else
							af.setDescription(GxtAccountingEntryType.PASTE.getName() + " v. "+paste.getVersion()+" from "+paste.getFromPath()+" by "+user.getName());
					}

					break;

				case REMOVAL:

					if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) ||  gxtEntryType.equals(GxtAccountingEntryType.REMOVE)){
						
						if(shubAccEntry instanceof AccountFolderEntryRemoval) {
							af.setOperation(GxtAccountingEntryType.REMOVE);
							AccountFolderEntryRemoval rem = (AccountFolderEntryRemoval) shubAccEntry;
							String msg = rem.getItemName()==null || rem.getItemName().isEmpty()?"":rem.getItemName()+" ";
	
							if(rem.getVersion()==null)
								msg+= GxtAccountingEntryType.REMOVE.getName() +" by "+user.getName();
							else
								msg+= GxtAccountingEntryType.REMOVE.getName() +" v."+rem.getVersion()+" by "+user.getName();
	
							af.setDescription(msg);
						}else {
							logger.warn("Found an "+AccountingEntryType.class.getSimpleName()+" of kind "+shubAccEntry.getType()+ " not castable to (instance of) "+AccountFolderEntryRemoval.class.getSimpleName());
						}
					}
					break;

				case RENAMING:

					if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.RENAME)){

						if(shubAccEntry instanceof AccountFolderEntryRenaming) {
							af.setOperation(GxtAccountingEntryType.RENAME);
							AccountFolderEntryRenaming ren = (AccountFolderEntryRenaming) shubAccEntry;
							String msg = ren.getOldItemName()==null || ren.getOldItemName().isEmpty()?"":ren.getOldItemName()+" ";
							if(ren.getVersion()==null)
								msg+= GxtAccountingEntryType.RENAME.getName() +" to "+ ren.getNewItemName()+ " by "+user.getName();
							else
								msg+= " v."+ren.getVersion() +" "+GxtAccountingEntryType.RENAME.getName() +" to "+ ren.getNewItemName()+ " by "+user.getName();
	
							af.setDescription(msg);
						}else {
							logger.warn("Found an "+AccountingEntryType.class.getSimpleName()+" of kind "+shubAccEntry.getType()+ " not castable to (instance of) "+AccountFolderEntryRenaming.class.getSimpleName());
						}
					}
					break;

				case ADD:

					if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.ADD)){
						
						if(shubAccEntry instanceof AccountFolderEntryAdd) {
							af.setOperation(GxtAccountingEntryType.ADD);
							AccountFolderEntryAdd acc = (AccountFolderEntryAdd) shubAccEntry;
							String msg = acc.getItemName()==null || acc.getItemName().isEmpty()?"":acc.getItemName()+" ";
							if(acc.getVersion()==null)
								msg+=GxtAccountingEntryType.ADD.getName()+ " by "+user.getName();
							else
								msg+=" v."+acc.getVersion()+ " "+GxtAccountingEntryType.ADD.getName()+ " by "+user.getName();
	
							af.setDescription(msg);
						}else {
							logger.warn("Found an "+AccountingEntryType.class.getSimpleName()+" of kind "+shubAccEntry.getType()+ " not castable to (instance of) "+AccountFolderEntryAdd.class.getSimpleName());
						}
					}
					break;

				case UPDATE:

					if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.UPDATE)){

						af.setOperation(GxtAccountingEntryType.UPDATE);
						AccountEntryUpdate upd = (AccountEntryUpdate) shubAccEntry;
						String msg = upd.getItemName()==null || upd.getItemName().isEmpty()?"":upd.getItemName()+" ";
						if(upd.getVersion()==null)
							msg+=GxtAccountingEntryType.UPDATE.getName()+" by "+user.getName();
						else
							msg+=" v."+upd.getVersion()+" "+GxtAccountingEntryType.UPDATE.getName()+" by "+user.getName();

						af.setDescription(msg);
					}
					break;

				case SHARE:

					if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.SHARE)){

						af.setOperation(GxtAccountingEntryType.SHARE);

						AccountEntryShare acc = (AccountEntryShare) shubAccEntry;

						String msg = "";
						if(acc.getItemName()==null || acc.getItemName().isEmpty())
							msg = user.getName() + " "+GxtAccountingEntryType.SHARE.getName()+ " workspace folder";
						else
							msg = user.getName() + " "+GxtAccountingEntryType.SHARE.getName()+ " workspace folder "+acc.getItemName();

						if(acc.getMembers()!=null && acc.getMembers().length>0)
							msg+=" with "+UserUtil.separateFullNameToCommaForPortalLogin(Arrays.asList(acc.getMembers()));

						af.setDescription(msg);
					}
					break;

				case UNSHARE:

					if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.UNSHARE)){

						af.setOperation(GxtAccountingEntryType.UNSHARE);
						AccountEntryUnshare uns = (AccountEntryUnshare) shubAccEntry;
						String msg = uns.getItemName()==null || uns.getItemName().isEmpty()?"":uns.getItemName()+" ";
						msg+=GxtAccountingEntryType.UNSHARE.getName()+" by "+user.getName();
						af.setDescription(msg);
					}
					break;

				case RESTORE:

					if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.RESTORE)){

						af.setOperation(GxtAccountingEntryType.RESTORE);
						AccountEntryRestore acc = (AccountEntryRestore) shubAccEntry;
						String msg = acc.getItemName()==null || acc.getItemName().isEmpty()?"":acc.getItemName()+" ";

						if(acc.getVersion()==null)
							msg+=GxtAccountingEntryType.RESTORE.getName()+" by "+user.getName();
						else
							msg+=" v."+acc.getVersion()+" "+GxtAccountingEntryType.RESTORE.getName() +" by "+user.getName();

						af.setDescription(msg);
					}
					break;

				case DISABLED_PUBLIC_ACCESS:

					if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.DISABLED_PUBLIC_ACCESS)){

						af.setOperation(GxtAccountingEntryType.DISABLED_PUBLIC_ACCESS);
						AccountEntryDisabledPublicAccess acc = (AccountEntryDisabledPublicAccess) shubAccEntry;
						//TODO acc.getItemName() is missing in SHUB
						//String msg = acc.getItemName()==null || acc.getItemName().isEmpty()?"":acc.getItemName()+" ";
						String msg=GxtAccountingEntryType.DISABLED_PUBLIC_ACCESS.getName()+" by "+user.getName();
						af.setDescription(msg);
					}

					break;

				case ENABLED_PUBLIC_ACCESS:

					if(gxtEntryType==null ||  gxtEntryType.equals(GxtAccountingEntryType.ALL) || gxtEntryType.equals(GxtAccountingEntryType.ALLWITHOUTREAD) || gxtEntryType.equals(GxtAccountingEntryType.ENABLED_PUBLIC_ACCESS)){

						af.setOperation(GxtAccountingEntryType.ENABLED_PUBLIC_ACCESS);
						AccountEntryEnabledPublicAccess acc = (AccountEntryEnabledPublicAccess) shubAccEntry;
						//TODO acc.getItemName() is missing in SHUB
						//String msg = acc.getItemName()==null || acc.getItemName().isEmpty()?"":acc.getItemName()+" ";
						String msg=GxtAccountingEntryType.ENABLED_PUBLIC_ACCESS.getName()+" by "+user.getName();
						af.setDescription(msg);
					}

					break;

				default:

					break;

				}
				listAccFields.add(af);
			}
		}
		logger.debug("get accounting readers converting completed - returning size "+listAccFields.size());

		return listAccFields;

	}


	/**
	 * Gets the gcube item properties for gcube item as HTML.
	 *
	 * @param wsItem the ws item
	 * @return the gcube item properties for gcube item as HTML
	 */
	public String getGcubeItemPropertiesForGcubeItemAsHTML(WorkspaceItem wsItem) {


		Map<String, String> properties = getGcubeItemProperties(wsItem);
	
		if(properties!=null){
	
			if(properties.size()==0){
				logger.warn("Map of Gcube Item Properties is empty for item: "+wsItem.getId());
				return null;
			}
	
			String html = "<div style=\"width: 100%; text-align:left; font-size: 10px;\">";
	
			for (String key : properties.keySet()) {
				String value = properties.get(key);
				logger.trace("Getting property: ["+key+","+properties.get(key)+"]");
				html+="<span style=\"font-weight:bold; padding-top: 5px;\">"+key+": </span>";
				html+="<span style=\"font-weight:normal;\">";
				html+=value;
				html+="</span><br/>";
			}
			
			html+="</div>";
	
			return html;
		}else {
			logger.warn("Gcube Item Properties not found for item: "+wsItem.getId());
			return null;
		}
	}


	/**
	 * Gets the gcube item properties.
	 *
	 * @param wsItem the ws item
	 * @return the gcube item properties
	 */
	public Map<String, String> getGcubeItemProperties(WorkspaceItem wsItem) {

		if(wsItem instanceof org.gcube.common.storagehubwrapper.shared.tohl.items.GCubeItem){
			GCubeItem gItem = (GCubeItem) wsItem;
			try {
				if(gItem.getProperties()!=null){
					PropertyMap map = gItem.getProperty();
					return toSimpleMap(map);
				}
			} catch (Exception e) {
				logger.error("Error on reading getProperty: ", e);
				return null;
			}
		}
		return null;
		
	}
}
