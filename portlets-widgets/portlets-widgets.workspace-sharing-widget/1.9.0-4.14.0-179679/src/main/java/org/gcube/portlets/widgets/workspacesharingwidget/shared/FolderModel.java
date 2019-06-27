package org.gcube.portlets.widgets.workspacesharingwidget.shared;

/**
 * @author Francesco Mangiacrapa 
 *
 */
public class FolderModel extends FileModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8634086763244262855L;

	public FolderModel() {
	}

	public FolderModel(String identifier, String name, String description, FileModel parent, boolean isDirectory, boolean isShared, boolean isVreFolder) {
		super(identifier, name, description, parent, isDirectory, isShared);
		super.setVreFolder(isVreFolder);
	}
	
	public FolderModel(String identifier, String name, String description, boolean isDirectory, boolean isVreFolder) {
		super(identifier, name, description, isDirectory);
		super.setVreFolder(isVreFolder);
	}

	@Override
	public String toString() {
		return super.toString();
	}


}
