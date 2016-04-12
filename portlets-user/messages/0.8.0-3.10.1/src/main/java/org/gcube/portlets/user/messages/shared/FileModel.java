package org.gcube.portlets.user.messages.shared;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;
import org.gcube.portlets.user.messages.client.resources.Resources;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class FileModel extends BaseModelData implements IsSerializable {

	protected static final String STATUS = "status";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected GXTFolderItemTypeEnum test; //TODO remove?

	
	protected FileModel(){
	}
	
	public FileModel(String identifier, String name, FileModel parent, boolean isDirectory) {
		setIdentifier(identifier);
		setName(name);
		setParentFileModel(parent);
		setIsDirectory(isDirectory);
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
	}
	
	public FileModel(String identifier, String name, String type, GXTFolderItemTypeEnum folderItemTypeEnum, boolean isDirectory) {
		setIdentifier(identifier);
		setName(name);
		setType(type);
		setFolderItemType(folderItemTypeEnum);
		setIsDirectory(isDirectory);
	}
	
	public void setParentFileModel(FileModel parent) {
		set(ConstantsPortletMessages.PARENT,parent);
	}
	
	
	/**
	 * Status values
	 * 	ConstantsPortletMessages.FOLDERNOTLOAD = "notload";
	 *  ConstantsPortletMessages.FOLDERLOADED = "loaded";
	 * @param status
	 */
	public void setStatus(String status) {
		set(STATUS, status);
	}
	
	public void setIcon() {
		
		AbstractImagePrototype absImgPr = null;
		
		if (!this.isDirectory()) {
			if(this.getType()!=null)
				absImgPr = Resources.getIconByType(this.getType());
//				absImgPr = Resources.getImageCancel();
			else
				absImgPr = Resources.getIconByFolderItemType(this.getGXTFolderItemType());
		}
		else
			absImgPr = Resources.getIconFolder();
		
		set(ConstantsPortletMessages.ICON, absImgPr.createImage());
		set(ConstantsPortletMessages.ABSTRACTICON, absImgPr);
	}
	
	public Image getIcon() {
		return (Image) get(ConstantsPortletMessages.ICON);
	}
	
	public AbstractImagePrototype getAbstractPrototypeIcon() {
		return (AbstractImagePrototype) get(ConstantsPortletMessages.ABSTRACTICON);
	}
	
	
	public String getStatus() {
		return get(STATUS);
	}
	
	
	private void setIsDirectory(boolean flag){
		set(ConstantsPortletMessages.ISDIRECTORY, flag);
	}
	
	public void setIdentifier(String identifier) {
		set(ConstantsPortletMessages.IDENTIFIER, identifier);	
	}
	
	public String getIdentifier(){
		return get(ConstantsPortletMessages.IDENTIFIER);
	}
	
	public void setShortcutCategory(String category) {
		set(ConstantsPortletMessages.SHORTCUTCATEGORY, category);	
	}
	
	public String getShortcutCategory(){
		return get(ConstantsPortletMessages.SHORTCUTCATEGORY).toString();
	}
	

	private void setName(String name) {
		set(ConstantsPortletMessages.NAME, name);
	}
	
	public String getName() {
		return get(ConstantsPortletMessages.NAME);
	}
	
	public FileModel getParentFileModel(){
		return get(ConstantsPortletMessages.PARENT);
	}
	
	public boolean isDirectory(){
		return (Boolean) get(ConstantsPortletMessages.ISDIRECTORY);
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
	 * @return the isShared
	 */
	public boolean isShared() {
		return (Boolean) get(ConstantsPortletMessages.ISSHARED);
	}

	/**
	 * @param isShared the isShared to set
	 */
	public void setShared(boolean isShared) {
		set(ConstantsPortletMessages.ISSHARED, isShared);
	}
	
	/**
	 * It's: folder, mime type or unknown
	 * @param type
	 */
	public void setType(String type){
		set(ConstantsPortletMessages.TYPE, type);
	}
	
	public String getType(){
		return get(ConstantsPortletMessages.TYPE);
	}
	
	public void setFolderItemType(GXTFolderItemTypeEnum folderItemTypeEnum){
		set(ConstantsPortletMessages.FOLDERITEMTYPE, folderItemTypeEnum);
	}
	
	public GXTFolderItemTypeEnum getGXTFolderItemType(){
		return (GXTFolderItemTypeEnum) get(ConstantsPortletMessages.FOLDERITEMTYPE);
	}
	
	public String getGXTFolderItemTypeToString(){
		return get(ConstantsPortletMessages.FOLDERITEMTYPE).toString();
	}

	
}
