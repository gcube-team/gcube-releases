package org.gcube.portlets.user.messages.shared;

import java.util.Date;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class FolderGridModel extends FileGridModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected FolderGridModel() {
	}
	
	public FolderGridModel(String identifier, String name, String path, Date creationDate, FileModel parent, long size, boolean isDirectory) {
		super(identifier, name, path, creationDate, parent, size, isDirectory);
	}
	
	
	public FolderGridModel(String identifier, String name, Date creationDate, FileModel parent, long size, boolean isDirectory) {
		super(identifier, name, creationDate, parent, size, isDirectory);
	}
}