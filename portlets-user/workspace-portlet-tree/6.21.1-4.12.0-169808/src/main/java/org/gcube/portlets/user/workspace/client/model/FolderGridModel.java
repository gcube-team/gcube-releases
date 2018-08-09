package org.gcube.portlets.user.workspace.client.model;

import java.util.Date;


/**
 * The Class FolderGridModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 15, 2016
 */
public class FolderGridModel extends FileGridModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 8274585443980764897L;


	/**
	 * Instantiates a new folder grid model.
	 */
	protected FolderGridModel() {
	}

	/**
	 * Instantiates a new folder grid model.
	 *
	 * @param identifier the identifier
	 * @param name the name
	 * @param path the path
	 * @param creationDate the creation date
	 * @param parent the parent
	 * @param size the size
	 * @param isDirectory the is directory
	 * @param isShared the is shared
	 * @param isVREFolder the is vre folder
	 * @param isPublic the is public
	 */
	public FolderGridModel(String identifier, String name, String path, Date creationDate, FileModel parent, long size, boolean isDirectory, boolean isShared,  boolean isVREFolder, boolean isPublic) {
		super(identifier, name, path, creationDate, parent, size, isDirectory, isShared);
		super.setVreFolder(isVREFolder);
		super.setIsPublic(isPublic);
	}


	/**
	 * Instantiates a new folder grid model.
	 *
	 * @param identifier the identifier
	 * @param name the name
	 * @param creationDate the creation date
	 * @param parent the parent
	 * @param size the size
	 * @param isDirectory the is directory
	 * @param isShared the is shared
	 * @param isVREFolder the is vre folder
	 * @param isPublic the is public
	 */
	public FolderGridModel(String identifier, String name, Date creationDate, FileModel parent, long size, boolean isDirectory, boolean isShared, boolean isVREFolder, boolean isPublic) {
		super(identifier, name, creationDate, parent, size, isDirectory, isShared);
		super.setVreFolder(isVREFolder);
		super.setIsPublic(isPublic);
	}
}