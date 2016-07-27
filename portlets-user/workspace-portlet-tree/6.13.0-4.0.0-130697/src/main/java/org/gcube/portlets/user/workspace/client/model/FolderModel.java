package org.gcube.portlets.user.workspace.client.model;

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

	public FolderModel(String identifier, String name, FileModel parent, boolean isDirectory, boolean isShared, boolean isVreFolder) {
		super(identifier, name, parent, isDirectory, isShared);
		super.setVreFolder(isVreFolder);
	}
	
	public FolderModel(String identifier, String name, boolean isDirectory, boolean isVreFolder) {
		super(identifier, name, isDirectory);
		super.setVreFolder(isVreFolder);
	}

	@Override
	public String toString() {
		return super.toString();
	}


}
