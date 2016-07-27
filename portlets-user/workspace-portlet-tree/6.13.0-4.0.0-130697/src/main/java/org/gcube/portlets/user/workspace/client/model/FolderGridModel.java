package org.gcube.portlets.user.workspace.client.model;

import java.util.Date;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class FolderGridModel extends FileGridModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8274585443980764897L;


	protected FolderGridModel() {
	}
	
	public FolderGridModel(String identifier, String name, String path, Date creationDate, FileModel parent, long size, boolean isDirectory, boolean isShared,  boolean isVREFolder) {
		super(identifier, name, path, creationDate, parent, size, isDirectory, isShared);
		super.setVreFolder(isVREFolder);
	}
	
	
	public FolderGridModel(String identifier, String name, Date creationDate, FileModel parent, long size, boolean isDirectory, boolean isShared, boolean isVREFolder) {
		super(identifier, name, creationDate, parent, size, isDirectory, isShared);
		super.setVreFolder(isVREFolder);
	}
}