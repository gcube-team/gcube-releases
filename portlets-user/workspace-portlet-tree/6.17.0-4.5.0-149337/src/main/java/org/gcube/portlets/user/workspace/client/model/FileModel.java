package org.gcube.portlets.user.workspace.client.model;

import java.io.Serializable;
import java.util.List;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategorySmartFolder;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.resources.Resources;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;



/**
 * The Class FileModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 15, 2016
 */
public class FileModel extends BaseModelData implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected GXTFolderItemTypeEnum folderItemType; //It's here to serialization don't remove
	protected GXTCategorySmartFolder smartFolders; //It's here to serialization don't remove
	protected InfoContactModel infoContacts;
	protected boolean isRoot = false;
	protected boolean isVreFolder;
	protected boolean isSpecialFolder = false;
	protected boolean isPublic = false; // IS IT A PUBLIC FOLDER?


	public static final String NAME = "Name";
	public static final String TYPE = "Type";
	public static final String ICON = "Icon";
	public static final String ABSTRACTICON = "Abstract Icon";
	public static final String SHORTCUTCATEGORY = "Shortcut Category";
	public static final String PARENT = "parent";
	public static final String ISDIRECTORY = "isDirectory";
	public static final String ISSHARED = "isShared";
	public static final String SHAREUSERS = "shareUsers";
	public static final String IDENTIFIER = "identifier";
	public static final String FOLDERITEMTYPE = "folderItemType";
	public static final String QUERY = "query";
	public static final String OWNER = "Owner";
	public static final String OWNERFULLNAME = "Owner Name";
	public static final String HUMAN_REDABLE_CATEGORY = "HR Category";

	/**
	 * Instantiates a new file model.
	 */
	protected FileModel(){
	}

	/**
	 * Instantiates a new file model.
	 *
	 * @param identifier the identifier
	 * @param name the name
	 * @param parent the parent
	 * @param isDirectory the is directory
	 * @param isShared the is shared
	 */
	public FileModel(String identifier, String name, FileModel parent, boolean isDirectory, boolean isShared) {
		setIdentifier(identifier);
		setName(name);
		setParentFileModel(parent);
		setIsDirectory(isDirectory);
		setShared(isShared);
		initDefaultProperties();
	}

	/**
	 * USED FOR ATTACHMENTS AND FolderBulkCreator.
	 *
	 * @param identifier the identifier
	 * @param name the name
	 * @param isDirectory the is directory
	 */
	public FileModel(String identifier, String name, boolean isDirectory) {
		setIdentifier(identifier);
		setName(name);
		setIsDirectory(isDirectory);
		initDefaultProperties();
	}

	/**
	 * Inits the default properties.
	 */
	protected void initDefaultProperties(){
		setShortcutCategory(GXTCategorySmartFolder.SMF_UNKNOWN);
		setShareable(true);
	}

	/**
	 * Sets the shareable.
	 *
	 * @param bool the new shareable
	 */
	public void setShareable(boolean bool) {
		set(ConstantsExplorer.ISSHAREABLE,bool);

	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description){
		set(ConstantsExplorer.DIRECTORYDESCRIPTION, description);
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription(){
		return get(ConstantsExplorer.DIRECTORYDESCRIPTION);
	}

	/**
	 * Checks if is shareable.
	 *
	 * @return true, if is shareable
	 */
	public boolean isShareable() {
		Object sharable = get(ConstantsExplorer.ISSHAREABLE);
		if(sharable!=null)
			return (Boolean) sharable;
		return false;
	}

	/**
	 * Instantiates a new file model.
	 *
	 * @param identifier the identifier
	 * @param name the name
	 * @param type the type
	 * @param folderItemTypeEnum the folder item type enum
	 * @param isDirectory the is directory
	 * @param isShared the is shared
	 */
	public FileModel(String identifier, String name, String type, GXTFolderItemTypeEnum folderItemTypeEnum, boolean isDirectory, boolean isShared) {
		setIdentifier(identifier);
		setName(name);
		setType(type);
		setFolderItemType(folderItemTypeEnum);
		setIsDirectory(isDirectory);
		setShared(isShared);
		initDefaultProperties();
	}

	/**
	 * Sets the parent file model.
	 *
	 * @param parent the new parent file model
	 */
	public void setParentFileModel(FileModel parent) {
		set(PARENT,parent);
	}


	/**
	 * Status values
	 * 	ConstantsExplorer.FOLDERNOTLOAD = "notload";
	 *  ConstantsExplorer.FOLDERLOADED = "loaded";
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		set("status", status);
	}

	/**
	 * Sets the icon.
	 */
	public void setIcon() {

		Image icon = (Image) get(ICON);
		AbstractImagePrototype iconAbs;
		if(icon==null)
			iconAbs = getAbstractPrototypeIcon();
		else
			return;

		set(ICON, iconAbs.createImage());
		set(ABSTRACTICON, iconAbs);
	}

	/**
	 * Sets the owner full name.
	 *
	 * @param fullName the new owner full name
	 */
	public void setOwnerFullName(String fullName){
		set(FileModel.OWNERFULLNAME, fullName);
	}

	/**
	 * Gets the owner full name.
	 *
	 * @return the owner full name
	 */
	public String getOwnerFullName(){
		return get(FileModel.OWNERFULLNAME);
	}

	/**
	 * Sets the sharing value.
	 *
	 * @param isShared the is shared
	 * @param listShareUsers the list share users
	 */
	public void setSharingValue(boolean isShared, List<InfoContactModel> listShareUsers){
		set(ISSHARED, isShared);
		set(SHAREUSERS, listShareUsers);
	}

	/**
	 * Gets the list user sharing.
	 *
	 * @return the list user sharing
	 */
	@SuppressWarnings("unchecked")
	public List<InfoContactModel> getListUserSharing(){
		return (List<InfoContactModel>) get(SHAREUSERS);
	}

	/**
	 * Sets the list share user.
	 *
	 * @param listShareUsers the new list share user
	 */
	public void setListShareUser(List<InfoContactModel> listShareUsers){
		set(SHAREUSERS, listShareUsers);
	}

	/**
	 * Checks if is shared.
	 *
	 * @return true, if is shared
	 */
	public boolean isShared(){
		Object shared = get(ISSHARED);
		if(shared!=null)
			return (Boolean) shared;
		return false;
	}

	/**
	 * Checks if is root.
	 *
	 * @return true, if is root
	 */
	public boolean isRoot(){
		return isRoot;
	}

	/**
	 * Sets the checks if is root.
	 *
	 * @param isRoot the new checks if is root
	 */
	public void setIsRoot(boolean isRoot){
		this.isRoot = isRoot;
	}


	/**
	 * Gets the icon.
	 *
	 * @return the icon
	 */
	public Image getIcon() {
		return getAbstractPrototypeIcon().createImage();
	}

	/**
	 * Gets the abstract prototype icon.
	 *
	 * @return the abstract prototype icon
	 */
	public AbstractImagePrototype getAbstractPrototypeIcon() {

		AbstractImagePrototype absImgPr = Resources.getIconTable();

		if (!this.isDirectory()) { //IS FILE
			if(this.getType()!=null)
				absImgPr = Resources.getIconByType(this.getName(), this.getType());
			else
				absImgPr = Resources.getIconByFolderItemType(this.getGXTFolderItemType());

		}else if(this.isShared()){ //IS A SHARED FOLDER?

			//GWT.log("setting icon "+this.getName()+" is shared: "+this.isShared() + ", this.isVreFolder() "+this.isVreFolder() +", this.isShareable() "+this.isShareable());

			if(this.isVreFolder())
				absImgPr =  Resources.getIconVREFolder();
			else{
				if(this.isShareable()){ //IS ROOT SHARED FOLDER
					if(this.isPublic()) //IS PLUBIC
						absImgPr =  Resources.getIconFolderSharedPublic();
					else
						absImgPr =  Resources.getIconSharedFolder();
				}else{ //IS A DESCENDANT
					if(this.isPublic()) //IS PLUBIC
						absImgPr = Resources.getIconFolderPublic();
					else
						absImgPr = Resources.getIconFolder(); //IS A PRIVATE FOLDER
				}
			}

		}else if(this.getStatus() == ConstantsExplorer.FOLDERNOTLOAD){ //IS A FOLDER IN LOADING
			absImgPr = Resources.getIconLoading2();

		}else{
			//SPECIAL FOLDER?
			if(this.isSpecialFolder())
				absImgPr = Resources.getIconSpecialFolder();
			else{ //SIMPLE FOLDER
				if(this.isPublic()) //IS PLUBIC
					absImgPr =  Resources.getIconFolderPublic();
				else
					absImgPr =  Resources.getIconFolder();
			}
		}

		return absImgPr;
	}


	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return get("status");
	}


	/**
	 * Sets the checks if is directory.
	 *
	 * @param flag the new checks if is directory
	 */
	public void setIsDirectory(boolean flag){
		set(ISDIRECTORY, flag);
	}

	/**
	 * Sets the identifier.
	 *
	 * @param identifier the new identifier
	 */
	public void setIdentifier(String identifier) {
		set(IDENTIFIER, identifier);
	}

	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	public String getIdentifier(){
		return get(IDENTIFIER);
	}

	/**
	 * Sets the shortcut category.
	 *
	 * @param smfDocuments the new shortcut category
	 */
	public void setShortcutCategory(GXTCategorySmartFolder smfDocuments) {
		set(SHORTCUTCATEGORY, smfDocuments);
		if(smfDocuments!=null)
			set(HUMAN_REDABLE_CATEGORY, smfDocuments.getValue());
	}


	/**
	 * Gets the shortcut category.
	 *
	 * @return the shortcut category
	 */
	public GXTCategorySmartFolder getShortcutCategory(){
		return (GXTCategorySmartFolder) get(SHORTCUTCATEGORY);
	}


	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		set(NAME, name);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return get(NAME);
	}

	/**
	 * Gets the parent file model.
	 *
	 * @return the parent file model
	 */
	public FileModel getParentFileModel(){
		return (FileModel) get(PARENT);
	}

	/**
	 * Checks if is directory.
	 *
	 * @return true, if is directory
	 */
	public boolean isDirectory(){
		Object directory = get(ISDIRECTORY);
		if(directory!=null)
			return (Boolean) directory;
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileModel) {
			FileModel mobj = (FileModel) obj;
			return getIdentifier().equals(mobj.getIdentifier());
		}
		return super.equals(obj);
	}

	/**
	 * Sets the shared.
	 *
	 * @param isShared the isShared to set
	 */
	public void setShared(boolean isShared) {
		set(ISSHARED, isShared);
	}

	/**
	 * It's: folder, mime type or unknown.
	 *
	 * @param type the new type
	 */
	public void setType(String type){
		set(TYPE, type);
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType(){
		return get(TYPE);
	}

	/**
	 * Sets the folder item type.
	 *
	 * @param folderItemTypeEnum the new folder item type
	 */
	public void setFolderItemType(GXTFolderItemTypeEnum folderItemTypeEnum){
		set(FOLDERITEMTYPE, folderItemTypeEnum);
	}

	/**
	 * Gets the GXT folder item type.
	 *
	 * @return the GXT folder item type
	 */
	public GXTFolderItemTypeEnum getGXTFolderItemType(){
		return (GXTFolderItemTypeEnum) get(FOLDERITEMTYPE);
	}

	/**
	 * Checks if is vre folder.
	 *
	 * @return true, if is vre folder
	 */
	public boolean isVreFolder() {
		return isVreFolder;
	}

	/**
	 * Sets the vre folder.
	 *
	 * @param isVreFolder the new vre folder
	 */
	public void setVreFolder(boolean isVreFolder) {
		this.isVreFolder = isVreFolder;
	}

	/**
	 * Sets the special folder.
	 *
	 * @param bool the new special folder
	 */
	public void setSpecialFolder(boolean bool) {
		this.isSpecialFolder = bool;

	}

	/**
	 * Checks if is special folder.
	 *
	 * @return true, if is special folder
	 */
	public boolean isSpecialFolder() {
		return isSpecialFolder;
	}

	/**
	 * Sets the checks if is public.
	 *
	 * @param isPublic the new checks if is public
	 */
	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	/**
	 * Checks if is public.
	 *
	 * @return true, if is public
	 */
	public boolean isPublic(){
		return isPublic;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileModel [isRoot=");
		builder.append(isRoot);
		builder.append(", isVreFolder=");
		builder.append(isVreFolder);
		builder.append(", isSpecialFolder=");
		builder.append(isSpecialFolder);
		builder.append(", isShareable()=");
		builder.append(isShareable());
		builder.append(", isShared()=");
		builder.append(isShared());
		builder.append(", getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
//		builder.append(", getParentFileModel()=");
//		builder.append(getParentFileModel());
		builder.append(", isDirectory()=");
		builder.append(isDirectory());
		builder.append("]");
		return builder.toString();
	}

}
