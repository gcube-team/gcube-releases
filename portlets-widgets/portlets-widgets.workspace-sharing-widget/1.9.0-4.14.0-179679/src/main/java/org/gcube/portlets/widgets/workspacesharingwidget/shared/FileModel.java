package org.gcube.portlets.widgets.workspacesharingwidget.shared;

import java.io.Serializable;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * 
 * @author Francesco Mangiacrapa Feb 25, 2014
 *
 */
public class FileModel extends BaseModelData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7291843683961579349L;
	private static final String DIRECTORYDESCRIPTION = "DIRECTORYDESCRIPTION";
	private static final String PARENT = "PARENT";
	private static final String OWNER = "OWNER";
	private static final String OWNERFULLNAME = "OWNERFULLNAME";
	private static final String ISSHARED = "ISSHARED";
	private static final String SHAREUSERS = "SHAREUSERS";
	private static final String ISDIRECTORY = "ISDIRECTORY";
	private static final String IDENTIFIER = "IDENTIFIER";
	private static final String TYPE = "TYPE";
	private static final String NAME = "NAME";
	private static final String FOLDERITEMTYPE = "FOLDERITEMTYPE";

	protected boolean isRoot = false;
	protected boolean isVreFolder;

	protected FileModel() {
	}

	public FileModel(String identifier, String name, String description, FileModel parent, boolean isDirectory, boolean isShared) {
		setIdentifier(identifier);
		setName(name);
		setDescription(description);
		setParentFileModel(parent);
		setIsDirectory(isDirectory);
		setShared(isShared);
	}

	public FileModel(String identifier, String name, String description,boolean isDirectory) {
		setIdentifier(identifier);
		setName(name);
		setDescription(description);
		setIsDirectory(isDirectory);
	}

	public void setDescription(String description) {
		set(DIRECTORYDESCRIPTION, description);
	}

	public String getDescription() {
		return get(DIRECTORYDESCRIPTION);
	}

	public void setParentFileModel(FileModel parent) {
		set(PARENT, parent);
	}

	/**
	 * Status values ConstantsExplorer.FOLDERNOTLOAD = "notload";
	 * ConstantsExplorer.FOLDERLOADED = "loaded";
	 * 
	 * @param status
	 *            Status
	 */
	public void setStatus(String status) {
		set("status", status);
	}

	// public void resetIcons(){
	// set(ConstantsExplorer.ICON,null);
	// set(ConstantsExplorer.ABSTRACTICON, null);
	// }

	public void setOwner(InfoContactModel owner) {
		set(OWNER, owner);

		// if(owner!=null)
		// set(ConstantsExplorer.OWNERFULLNAME, owner.getName());
	}

	public void setOwnerFullName(String fullName) {
		set(OWNERFULLNAME, fullName);
	}

	// TODO Accounting

	public InfoContactModel getOwner() {
		return (InfoContactModel) get(OWNER);
	}

	public String getOwnerFullName() {
		return get(OWNERFULLNAME);
	}

	public void setSharingValue(boolean isShared, List<InfoContactModel> listShareUsers) {
		set(ISSHARED, isShared);
		set(SHAREUSERS, listShareUsers);
	}

	@SuppressWarnings("unchecked")
	public List<InfoContactModel> getListUserSharing() {
		return (List<InfoContactModel>) get(SHAREUSERS);
	}

	public void setListShareUser(List<InfoContactModel> listShareUsers) {
		set(SHAREUSERS, listShareUsers);
	}

	public boolean isShared() {
		return (Boolean) get(ISSHARED);
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setIsRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public String getStatus() {
		return get("status");
	}

	private void setIsDirectory(boolean flag) {
		set(ISDIRECTORY, flag);
	}

	public void setIdentifier(String identifier) {
		set(IDENTIFIER, identifier);
	}

	public String getIdentifier() {
		return get(IDENTIFIER);
	}

	private void setName(String name) {
		set(NAME, name);
	}

	public String getName() {
		return get(NAME);
	}

	public FileModel getParentFileModel() {
		return get(PARENT);
	}

	public boolean isDirectory() {
		return (Boolean) get(ISDIRECTORY);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileModel) {
			FileModel mobj = (FileModel) obj;
			return getIdentifier().equals(mobj.getIdentifier());
		}
		return super.equals(obj);
	}

	public void setFolderItemType(GXTFolderItemTypeEnum folderItemTypeEnum) {
		set(FOLDERITEMTYPE, folderItemTypeEnum);
	}

	public GXTFolderItemTypeEnum getGXTFolderItemType() {
		return (GXTFolderItemTypeEnum) get(FOLDERITEMTYPE);
	}

	/**
	 * @param isShared
	 *            the isShared to set
	 */
	public void setShared(boolean isShared) {
		set(ISSHARED, isShared);
	}

	/**
	 * It's: folder, mime type or unknown
	 * 
	 * @param type
	 *            Type
	 */
	public void setType(String type) {
		set(TYPE, type);
	}

	public String getType() {
		return get(TYPE);
	}

	public boolean isVreFolder() {
		return isVreFolder;
	}

	public void setVreFolder(boolean isVreFolder) {
		this.isVreFolder = isVreFolder;
	}

	@Override
	public String toString() {
		return "FileModel [isRoot=" + isRoot + ", isVreFolder=" + isVreFolder + ", getDescription()=" + getDescription()
				+ ", getOwner()=" + getOwner() + ", getOwnerFullName()=" + getOwnerFullName()
				+ ", getListUserSharing()=" + getListUserSharing() + ", isShared()=" + isShared() + ", isRoot()="
				+ isRoot() + ", getStatus()=" + getStatus() + ", getIdentifier()=" + getIdentifier() + ", getName()="
				+ getName() + ", isDirectory()=" + isDirectory() + ", getType()=" + getType() + ", isVreFolder()="
				+ isVreFolder() + "]";
	}
	
	
	
}
