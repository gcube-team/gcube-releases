package org.gcube.portlets.user.workspace.client.model;


/**
 * The Class FolderModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 15, 2016
 */
public class FolderModel extends FileModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Instantiates a new folder model.
	 */
	protected FolderModel() {
	}

	/**
	 * Instantiates a new folder model.
	 *
	 * @param identifier the identifier
	 * @param name the name
	 * @param parent the parent
	 * @param isDirectory the is directory
	 * @param isShared the is shared
	 * @param isVreFolder the is vre folder
	 * @param isPublic the is public
	 */
	public FolderModel(String identifier, String name, FileModel parent, boolean isDirectory, boolean isShared, boolean isVreFolder, boolean isPublic) {
		super(identifier, name, parent, isDirectory, isShared);
		super.setVreFolder(isVreFolder);
		super.setIsPublic(isPublic);
	}

	/**
	 * Instantiates a new folder model.
	 * Used for attachments
	 *
	 * @param identifier the identifier
	 * @param name the name
	 * @param isDirectory the is directory
	 * @param isVreFolder the is vre folder
	 */
	public FolderModel(String identifier, String name, boolean isDirectory, boolean isVreFolder) {
		super(identifier, name, isDirectory);
		super.setVreFolder(isVreFolder);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.model.FileModel#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}


}
