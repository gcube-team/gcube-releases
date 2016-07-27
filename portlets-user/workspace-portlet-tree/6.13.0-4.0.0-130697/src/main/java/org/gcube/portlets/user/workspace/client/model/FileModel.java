package org.gcube.portlets.user.workspace.client.model;

import java.io.Serializable;
import java.util.List;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.resources.Resources;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class FileModel extends BaseModelData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected GXTFolderItemTypeEnum folderItemType; //It's here to serialization don't remove
	protected InfoContactModel infoContacts;
	protected boolean isRoot = false;
	protected boolean isVreFolder;
	protected boolean isSpecialFolder = false;
	
	protected FileModel(){
	}
	
	/**
	 * 
	 * @param identifier
	 * @param name
	 * @param parent
	 * @param isDirectory
	 * @param isShared
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
	 * USED FOR ATTACHMENTS AND FolderBulkCreator
	 * @param identifier
	 * @param name
	 * @param isDirectory
	 */
	public FileModel(String identifier, String name, boolean isDirectory) {
		setIdentifier(identifier);
		setName(name);
		setIsDirectory(isDirectory);
		initDefaultProperties();
	}
	
	private void initDefaultProperties(){
		setShortcutCategory("");
		setShareable(true);
	}
	
	public void setShareable(boolean bool) {
		set(ConstantsExplorer.ISSHAREABLE,bool);
		
	}
	
	public void setDescription(String description){
		set(ConstantsExplorer.DIRECTORYDESCRIPTION, description);
	}
	
	public String getDescription(){
		return get(ConstantsExplorer.DIRECTORYDESCRIPTION);
	}
	
	public boolean isShareable() {
		return (Boolean) get(ConstantsExplorer.ISSHAREABLE);
		
	}

	/**
	 * 
	 * @param identifier
	 * @param name
	 * @param type
	 * @param folderItemTypeEnum
	 * @param isDirectory
	 * @param isShared
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
	
	public void setParentFileModel(FileModel parent) {
		set(ConstantsExplorer.PARENT,parent);
	}
	
	
	/**
	 * Status values
	 * 	ConstantsExplorer.FOLDERNOTLOAD = "notload";
	 *  ConstantsExplorer.FOLDERLOADED = "loaded";
	 * @param status
	 */
	public void setStatus(String status) {
		set("status", status);
	}
	
	public void setIcon() {
		
		Image icon = (Image) get(ConstantsExplorer.ICON);
		AbstractImagePrototype iconAbs;
		if(icon==null)
			iconAbs = getAbstractPrototypeIcon();
		else
			return;
		
		set(ConstantsExplorer.ICON, iconAbs.createImage());
		set(ConstantsExplorer.ABSTRACTICON, iconAbs);
	}
	
//	public void resetIcons(){
//		set(ConstantsExplorer.ICON,null);
//		set(ConstantsExplorer.ABSTRACTICON, null);
//	}
	
//	public void setOwner(InfoContactModel owner){
//		set(ConstantsExplorer.OWNER, owner);
//	}
	
	public void setOwnerFullName(String fullName){
		set(ConstantsExplorer.OWNERFULLNAME, fullName);
	}

//	public InfoContactModel getOwner(){
//		return (InfoContactModel) get(ConstantsExplorer.OWNER);
//	}
	
	public String getOwnerFullName(){
		return get(ConstantsExplorer.OWNERFULLNAME);
	}
	
	public void setSharingValue(boolean isShared, List<InfoContactModel> listShareUsers){
		set(ConstantsExplorer.ISSHARED, isShared);
		set(ConstantsExplorer.SHAREUSERS, listShareUsers);
	}
	
	@SuppressWarnings("unchecked")
	public List<InfoContactModel> getListUserSharing(){
		return (List<InfoContactModel>) get(ConstantsExplorer.SHAREUSERS);
	}
	
	public void setListShareUser(List<InfoContactModel> listShareUsers){
		set(ConstantsExplorer.SHAREUSERS, listShareUsers);
	}
	
	public boolean isShared(){
		return (Boolean) get(ConstantsExplorer.ISSHARED);
	}
	
	public boolean isRoot(){
		return isRoot;
	}
	
	public void setIsRoot(boolean isRoot){
		this.isRoot = isRoot;
	}
	
	
	public Image getIcon() {
		return getAbstractPrototypeIcon().createImage();
	}
	
	public AbstractImagePrototype getAbstractPrototypeIcon() {
		
		AbstractImagePrototype absImgPr = Resources.getIconTable();
		
		if (!this.isDirectory()) { //IS FILE
			if(this.getType()!=null)
				absImgPr = Resources.getIconByType(this.getName(), this.getType());
			else
				absImgPr = Resources.getIconByFolderItemType(this.getGXTFolderItemType());
			
		}else if(this.isShared()){ //IS A SHARED FOLDER?
			
			GWT.log("setting icon is shared: "+this.isShared() + ", this.isVreFolder() "+this.isVreFolder() +", this.isShareable() "+this.isShareable());
			
			if(this.isVreFolder())
				absImgPr =  Resources.getIconVREFolder();
			else{
				if(this.isShareable()) //IS ROOT SHARED FOLDER
					absImgPr =  Resources.getIconSharedFolder();
				else
					absImgPr = Resources.getIconFolder(); //IS A DESCENDANT
			}

		}else if(this.getStatus() == ConstantsExplorer.FOLDERNOTLOAD){ //IS A FOLDER IN LOADING
			absImgPr = Resources.getIconLoading2();
			
		}else{
			//SPECIAL FOLDER?
			if(this.isSpecialFolder())
				absImgPr = Resources.getIconSpecialFolder();
			else //SIMPLE FOLDER
				absImgPr = Resources.getIconFolder();
		}
		
		return absImgPr;
	}
	
	
	
//	public Image getIcon() {
//		return (Image) get(ConstantsExplorer.ICON);
//	}
//	
//	public AbstractImagePrototype getAbstractPrototypeIcon() {
//		if((AbstractImagePrototype) get(ConstantsExplorer.ABSTRACTICON)==null)
//			setIcon();
//		return (AbstractImagePrototype) get(ConstantsExplorer.ABSTRACTICON);
//	}
	
	
	public String getStatus() {
		return get("status");
	}
	
	
	public void setIsDirectory(boolean flag){
		set(ConstantsExplorer.ISDIRECTORY, flag);
	}
	
	public void setIdentifier(String identifier) {
		set(ConstantsExplorer.IDENTIFIER, identifier);	
	}
	
	public String getIdentifier(){
		return get(ConstantsExplorer.IDENTIFIER);
	}
	
	public void setShortcutCategory(String category) {
		set(ConstantsExplorer.SHORTCUTCATEGORY, category);	
	}
	
	public String getShortcutCategory(){
		return get(ConstantsExplorer.SHORTCUTCATEGORY).toString();
	}
	

	public void setName(String name) {
		set(ConstantsExplorer.NAME, name);
	}
	
	public String getName() {
		return get(ConstantsExplorer.NAME);
	}
	
	public FileModel getParentFileModel(){
		return (FileModel) get(ConstantsExplorer.PARENT);
	}
	
	public boolean isDirectory(){
		return (Boolean) get(ConstantsExplorer.ISDIRECTORY);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileModel) {
			FileModel mobj = (FileModel) obj;
			return getIdentifier().equals(mobj.getIdentifier());
		}
		return super.equals(obj);
	}
	
	/**
	 * @param isShared the isShared to set
	 */
	public void setShared(boolean isShared) {
		set(ConstantsExplorer.ISSHARED, isShared);
	}
	
	/**
	 * It's: folder, mime type or unknown
	 * @param type
	 */
	public void setType(String type){
		set(ConstantsExplorer.TYPE, type);
	}
	
	public String getType(){
		return get(ConstantsExplorer.TYPE);
	}
	
	public void setFolderItemType(GXTFolderItemTypeEnum folderItemTypeEnum){
		set(ConstantsExplorer.FOLDERITEMTYPE, folderItemTypeEnum);
	}
	
	public GXTFolderItemTypeEnum getGXTFolderItemType(){
		return (GXTFolderItemTypeEnum) get(ConstantsExplorer.FOLDERITEMTYPE);
	}
	
	public boolean isVreFolder() {
		return isVreFolder;
	}

	public void setVreFolder(boolean isVreFolder) {
		this.isVreFolder = isVreFolder;
	}
	
	/**
	 * @param b
	 */
	public void setSpecialFolder(boolean bool) {
		this.isSpecialFolder = bool;
		
	}

	public boolean isSpecialFolder() {
		return isSpecialFolder;
	}

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
