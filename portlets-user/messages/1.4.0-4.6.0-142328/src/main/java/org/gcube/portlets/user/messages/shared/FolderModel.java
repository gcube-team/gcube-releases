package org.gcube.portlets.user.messages.shared;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class FolderModel extends FileModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected FolderModel() {
	}

	public FolderModel(String identifier, String name, FileModel parent, boolean isDirectory) {
		super(identifier, name, parent, isDirectory);
	}
	
	public FolderModel(String identifier, String name, boolean isDirectory) {
		super(identifier, name, isDirectory);
	}
}
