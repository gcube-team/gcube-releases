package org.gcube.portlets.user.workspace.client.model;

import java.util.Date;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
@Deprecated
public class FileDetailsModel extends FileGridModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected FileDetailsModel() {
	}

	public FileDetailsModel(String identifier, String name, String path, Date creationDate, FileModel parent, long size, boolean isDirectory, String description, Date lastModified, InfoContactModel owner, boolean isShared) {
		super(identifier,name,path,creationDate,parent,size,isDirectory, isShared);
		
		setLastModified(lastModified);
		setDescription(description);
//		setOwner(owner);
		
	}
	
	public void setLastModified(Date lastModified) {
		set(ConstantsExplorer.LASTMODIFIED, lastModified);		
	}
	
	
	public String getDescription(){	
		return get(ConstantsExplorer.DESCRIPTION);			
	}
	
}
