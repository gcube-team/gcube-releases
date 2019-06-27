package org.gcube.portlets.user.workspace.client.model;

import java.util.Date;


/**
 * The Class FileGridModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 15, 2016
 */
public class FileGridModel extends FileModel {


	/**
	 *
	 */
	private static final long serialVersionUID = 2851920950408676250L;
	public static final String LASTMODIFIED = "lastModified";
	public static final String DESCRIPTION = "description";
	public static final String SIZE = "Size";
	public static final String GRIDCOLUMNCREATIONDATE = "Creation Date";
	public static final String EMPTY = "EMPTY";
	public static final String VERSION = "Version";

	/**
	 * Instantiates a new file grid model.
	 */
	protected FileGridModel() {
	}

	/**
	 * Instantiates a new file grid model.
	 *
	 * @param identifier
	 *            the identifier
	 * @param name
	 *            the name
	 * @param path
	 *            the path
	 * @param lastUpdate
	 *            the last update
	 * @param parent
	 *            the parent
	 * @param size
	 *            the size
	 * @param isDirectory
	 *            the is directory
	 * @param isShared
	 *            the is shared
	 */
	public FileGridModel(String identifier, String name, String path,
			Date lastUpdate, FileModel parent, long size, boolean isDirectory,
			boolean isShared) {
		super(identifier, name, parent, isDirectory, isShared);

		setLastModification(lastUpdate);
		setSize(size);
	}

	/**
	 * Instantiates a new file grid model.
	 *
	 * @param identifier
	 *            the identifier
	 * @param name
	 *            the name
	 * @param lastUpdate
	 *            the last update
	 * @param parent
	 *            the parent
	 * @param size
	 *            the size
	 * @param isDirectory
	 *            the is directory
	 * @param isShared
	 *            the is shared
	 */
	public FileGridModel(String identifier, String name, Date lastUpdate,
			FileModel parent, long size, boolean isDirectory, boolean isShared) {
		super(identifier, name, parent, isDirectory, isShared);

		setLastModification(lastUpdate);
		setSize(size);
	}

	/**
	 * Sets the size.
	 *
	 * @param size
	 *            the new size
	 */
	private void setSize(long size) {
		set(FileGridModel.SIZE, size);
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public long getSize() {
		return (Long) get(FileGridModel.SIZE);
	}

	/**
	 * Sets the last modification.
	 *
	 * @param lastUpdate
	 *            the new last modification
	 */
	private void setLastModification(Date lastUpdate) {
		set(FileGridModel.LASTMODIFIED, lastUpdate);

	}

	/**
	 * Gets the last modification.
	 *
	 * @return the last modification
	 */
	public Date getLastModification() {
		return (Date) get(FileGridModel.LASTMODIFIED);

	}


	/**
	 * Sets the version name.
	 *
	 * @param version the new version name
	 */
	public void setVersionName(String version){
		set(FileGridModel.VERSION, version);
	}

	/**
	 * Gets the version name.
	 *
	 * @return the version name
	 */
	public String getVersionName(){
		 return (String) get(FileGridModel.VERSION);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.gcube.portlets.user.workspace.client.model.FileModel#equals(java.
	 * lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileGridModel) {
			FileGridModel mobj = (FileGridModel) obj;
			return getIdentifier().equals(mobj.getIdentifier());
		}
		return super.equals(obj);
	}
}
